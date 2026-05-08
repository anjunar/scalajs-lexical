#!/usr/bin/env bash

set -euo pipefail

VERSION="${1:-}"
PUBLISHING_TYPE="${PUBLISHING_TYPE:-AUTOMATIC}"
DEPLOYMENT_NAME="${DEPLOYMENT_NAME:-}"
POLL_SECONDS="${POLL_SECONDS:-10}"
MAX_POLLS="${MAX_POLLS:-90}"
SKIP_PUBLISH_SIGNED="${SKIP_PUBLISH_SIGNED:-false}"

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$repo_root"

get_build_version() {
  local build_sbt
  build_sbt="$repo_root/build.sbt"
  sed -n 's/ThisBuild[[:space:]]*\/[[:space:]]*version[[:space:]]*:=[[:space:]]*"\([^"]*\)"/\1/p' "$build_sbt" | head -n1
}

get_central_credential_value() {
  local key="$1"
  local env_name="$2"
  local env_value="${!env_name:-}"
  local cred_file="${HOME}/.sbt/sonatype_central_credentials"

  if [[ -n "$env_value" ]]; then
    printf '%s\n' "$env_value"
    return 0
  fi

  if [[ ! -f "$cred_file" ]]; then
    echo "Credentials-Datei nicht gefunden: $cred_file" >&2
    return 1
  fi

  sed -n "s/^${key}=//p" "$cred_file" | head -n1
}

invoke_curl() {
  curl --silent --show-error --fail "$@"
}

if [[ -z "$VERSION" ]]; then
  VERSION="$(get_build_version)"
fi

if [[ -z "$VERSION" ]]; then
  echo "Konnte Version aus build.sbt nicht lesen." >&2
  exit 1
fi

if [[ -z "$DEPLOYMENT_NAME" ]]; then
  DEPLOYMENT_NAME="com.anjunar:scalajs-lexical:${VERSION}"
fi

bundle_dir="$repo_root/target/sona-staging"
bundle_zip="$repo_root/target/central-bundle-${VERSION}.zip"

if [[ "$SKIP_PUBLISH_SIGNED" != "true" ]]; then
  sbt --batch "scalajs-lexical/publishSigned"
fi

if [[ ! -d "$bundle_dir" ]]; then
  echo "Bundle-Verzeichnis nicht gefunden: $bundle_dir" >&2
  exit 1
fi

rm -f "$bundle_zip"

(
  cd "$bundle_dir"
  zip -qr "$bundle_zip" .
)

user="$(get_central_credential_value user SONATYPE_CENTRAL_USERNAME)"
password="$(get_central_credential_value password SONATYPE_CENTRAL_PASSWORD)"

if [[ -z "$user" || -z "$password" ]]; then
  echo "Central-Credentials konnten nicht gelesen werden." >&2
  exit 1
fi

token="$(printf '%s' "${user}:${password}" | base64 | tr -d '\r\n')"
encoded_name="$(python -c 'import sys, urllib.parse; print(urllib.parse.quote(sys.argv[1], safe=""))' "$DEPLOYMENT_NAME")"
upload_url="https://central.sonatype.com/api/v1/publisher/upload?name=${encoded_name}&publishingType=${PUBLISHING_TYPE}"

echo "Lade Bundle hoch: $bundle_zip"
deployment_id="$(invoke_curl \
  --request POST \
  --header "Authorization: Bearer ${token}" \
  --form "bundle=@${bundle_zip}" \
  "$upload_url")"

if [[ -z "$deployment_id" ]]; then
  echo "Sonatype hat keine Deployment-ID zurueckgegeben." >&2
  exit 1
fi

echo "Deployment-ID: $deployment_id"

for ((attempt=1; attempt<=MAX_POLLS; attempt++)); do
  sleep "$POLL_SECONDS"

  status_json="$(invoke_curl \
    --request POST \
    --header "Authorization: Bearer ${token}" \
    "https://central.sonatype.com/api/v1/publisher/status?id=${deployment_id}")"

  status="$(printf '%s' "$status_json" | python -c 'import json, sys; print(json.load(sys.stdin)["deploymentState"])')"
  echo "[$attempt/$MAX_POLLS] Status: $status"

  if [[ "$status" == "PUBLISHED" ]]; then
    echo "Maven Central Publish abgeschlossen."
    printf '%s\n' "$status_json"
    exit 0
  fi

  if [[ "$status" == "FAILED" || "$status" == "VALIDATED" ]]; then
    echo "Deployment beendet mit Status $status."
    printf '%s\n' "$status_json"
    exit 1
  fi
done

echo "Timeout beim Warten auf den Sonatype-Status. Deployment-ID: $deployment_id" >&2
exit 1

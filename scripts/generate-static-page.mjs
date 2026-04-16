import { execFile } from "node:child_process";
import { readFile, readdir, writeFile } from "node:fs/promises";
import { dirname, join, resolve } from "node:path";
import { fileURLToPath } from "node:url";
import { promisify } from "node:util";

const scriptDir = dirname(fileURLToPath(import.meta.url));
const root = resolve(scriptDir, "..");
const outDir = join(root, "docs");
const indexPath = join(outDir, "index.html");
const ssrRouteScript = join(scriptDir, "render-ssr-route.mjs");
const execFileAsync = promisify(execFile);

async function findSsrEntryPath() {
  const assetsDir = join(outDir, "assets");
  const files = await readdir(assetsDir);
  const entry = files.find(file => /^index-[A-Za-z0-9_-]+\.js$/.test(file));

  if (!entry) {
    throw new Error(`Could not find built SSR entry in ${assetsDir}`);
  }

  return join(assetsDir, entry);
}

const ssrEntryPath = await findSsrEntryPath();
const indexHtml = await readFile(indexPath, "utf8");

// Prepare output path
const ssrTempPath = join(outDir, "ssr-temp.html");

// Run SSR
await execFileAsync(
  process.execPath,
  [ssrRouteScript, ssrEntryPath, "/", ssrTempPath],
  {
    cwd: root,
    timeout: 30000,
    maxBuffer: 1024 * 1024 * 8,
  }
);

const ssrContent = await readFile(ssrTempPath, "utf8");

// Simple injection into index.html
// This assumes the renderer script returns the innerHTML of the editor and toolbar
const [editorHtml, toolbarHtml, stateHtml] = ssrContent.split("<!-- SPLIT -->");

let finalHtml = indexHtml
  .replace(/(<div id="editor"[^>]*>)(<\/div>)/, `$1${editorHtml}$2`)
  .replace(/(<div id="toolbar"[^>]*>)(<\/div>)/, `$1${toolbarHtml}$2`)
  .replace(/(<pre id="state"[^>]*>)(<\/pre>)/, `$1${stateHtml}$2`);

await writeFile(indexPath, finalHtml);
console.log("Static page generated successfully.");

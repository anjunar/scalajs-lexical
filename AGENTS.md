# AGENTS.md

Diese Datei ergaenzt die uebergeordnete Anleitung in `..\AGENTS.md`.

## Vererbung

- Zuerst immer `..\AGENTS.md` lesen und befolgen.
- Diese Datei enthaelt nur projektspezifische Ergaenzungen fuer `lexical`.
- Bei Widerspruch gilt die spezifischere Regel aus dieser Datei, sofern sie nicht gegen Sicherheits- oder Nutzeranweisungen verstoesst.

## Projektbild

- `lexical` ist ein oeffentlich wirkendes Scala.js-Projekt fuer einen typsicheren Wrapper um den Lexical Rich-Text-Editor.
- Das Repository besteht aus einer publizierbaren Bibliothek und einer Demo-Anwendung.
- Die Bibliothek heisst in sbt `scalajs-lexical` und liegt unter `library`.
- Die Demo heisst in sbt `scalajs-lexical-demo` und liegt unter `application`.
- Die Demo wird ueber Vite gebaut und nach `docs` ausgegeben, vermutlich fuer statisches Hosting.
- Das Projekt ist Scala 3 / Scala.js / Vite / npm, nicht JVM-Server-zentriert.

## Wichtige Pfade

- `build.sbt`: Multi-Projekt-Build, Publishing-Metadaten, Scala.js-Linker-Konfiguration.
- `project/plugins.sbt`: sbt-Plugins, insbesondere `sbt-scalajs`, `sbt-pgp`, `sbt-sonatype`.
- `project/build.properties`: sbt-Version.
- `package.json`: Vite-Skripte und npm-Abhaengigkeiten fuer Lexical, CodeMirror und Scala.js-Vite.
- `vite.config.js`: Vite-Root ist `application/src/main/webapp`; Output ist `docs`; Scala.js-Projekt ist `scalajs-lexical-demo`.
- `library/src/main/scala/lexical`: Kern der Scala.js-Wrapper-Bibliothek.
- `library/src/main/scala/lexical/codemirror`: CodeMirror-Integration.
- `library/src/main/resources/lexical/index.css`: CSS-Ressource der Bibliothek, in Vite als `@lexical-css` eingebunden.
- `application/src/main/scala/lexical/Demo.scala`: Demo-Einstiegspunkt und gutes Integrationsbeispiel.
- `application/src/main/webapp`: Vite-Webapp der Demo.
- `scripts/generate-static-page.mjs`: Post-Build-Schritt zur statischen HTML-Erzeugung.
- `scripts/render-ssr-route.mjs`: Happy-DOM-basierte SSR-/Pre-Render-Hilfe.
- `docs`: gebautes statisches Artefakt, nicht als Quelle der Wahrheit behandeln.

## Lokale Arbeitsregeln

- Fuer sbt immer `sbtn-x86_64-pc-win32.exe` bzw. `sbtn` verwenden, wie in `..\AGENTS.md` beschrieben.
- Nicht unbedacht einen neuen sbt-Server starten.
- Vor Server-Neustarts den Nutzer fragen.
- `node_modules`, `target`, `docs/assets` und andere generierte Artefakte nicht manuell pflegen, ausser die Aufgabe verlangt es ausdruecklich.
- Bestehende uncommitted Aenderungen respektieren. Zum Zeitpunkt dieser Datei gab es bereits lokale Aenderungen in `build.sbt`, `application/src/main/scala/lexical/Demo.scala`, `library/src/main/scala/lexical/LinkModule.scala`, `library/src/main/scala/lexical/LinkCommands.scala` und CodeMirror-bezogenen Dateien.

## Build und Checks

- Scala.js-Kompilation der Bibliothek:
  `sbtn-x86_64-pc-win32.exe scalajs-lexical/compile`
- Scala.js-Kompilation der Demo:
  `sbtn-x86_64-pc-win32.exe scalajs-lexical-demo/compile`
- Alle Scala-Teilprojekte kompilieren:
  `sbtn-x86_64-pc-win32.exe compile`
- Tests, falls vorhanden:
  `sbtn-x86_64-pc-win32.exe test`
- Demo/Web-Build:
  `npm run build`
- Lokale Demo-Entwicklung:
  `npm run dev`
- Preview des gebauten Outputs:
  `npm run preview`

Hinweis: Es wurden aktuell keine `*Test.scala`, `*Spec.scala` oder JS-Testdateien gefunden. Wenn Verhalten geaendert wird, mindestens passend kompilieren; fuer UI-/Demo-Aenderungen zusaetzlich `npm run build` pruefen, sofern praktikabel.

## Architektur

- `Lexical.scala` enthaelt die zentralen Scala.js-Facades zu `lexical` und den Lexical-Packages.
- `LexicalEditor.scala` beschreibt die Editor-Facade und Extension-Methoden wie `getSelectionWrapper`, `getDialogService` und `setDialogService`.
- `LexicalBuilder.scala` ist der Haupt-Einstieg fuer Nutzer der Bibliothek: Namespace, Theme, Nodes, Module, Toolbar, Floating Toolbar, Initial State, Placeholder und State-Change-Listener.
- `EditorModule.scala` definiert das Modulmodell: Toolbar-Metadaten, Aktivierbarkeit, Ausfuehrung, Keybinding und Registrierung.
- Toolbar-Struktur liegt in `ToolbarElement.scala`, `ToolbarDropdown.scala`, `ToolbarRegistry.scala`, `ToolbarRenderer.scala`, `ToolbarManager.scala` und den Renderer-Klassen.
- Feature-Module wie `HistoryModule`, `MarkdownModule`, `LinkModule`, `ListModule`, `TableModule`, `ImageModule`, `TextFormatModule` kapseln editorbezogene Funktionalitaet.
- Node-Facades und Custom Nodes liegen unter anderem in `LexicalNodes.scala`, `LexicalList.scala`, `LexicalTable.scala`, `ImageNode.scala` und `codemirror/CodeMirrorNode.scala`.
- `DialogService` ist die Abstraktion fuer UI-Dialoge; die Demo liefert mit `DemoDialogService` eine konkrete DOM-Implementierung.

## Demo-Fluss

- `application/src/main/webapp/src/main.js` importiert Demo-CSS, Bibliotheks-CSS und `scalajs:main.js`.
- `Demo.scala` baut den Editor per `LexicalBuilder`, registriert Nodes, Toolbar-Module, Floating Toolbar, History, Markdown, Link-/Image-Dialoge und initialen Manifest-Content.
- Die Demo nutzt Material Icons und Google Fonts im HTML.
- Das Seitendesign ist bewusst "Manifesto of Silence": ruhig, archivalisch, strukturiert, mit Sidebar, Editor, Toolbar und JSON-State-Ansicht.

## Bekannte Eigenheiten und Stolperstellen

- `withToolbar` sammelt Toolbar-Elemente und registriert enthaltene `EditorModule` automatisch in `_modules`.
- `withFloatingToolbar` sammelt ebenfalls Module und fuegt sie `_modules` hinzu.
- `LexicalBuilder` registriert RichText und History standardmaessig; `HistoryModule` kann zusaetzlich genutzt werden, wenn ein expliziter History-State benoetigt wird.
- Tabellen-Support wird in `LexicalBuilder` nur registriert, wenn `TableNode`, `TableRowNode` und `TableCellNode` in den Builder-Nodes vorhanden sind.
- Custom Decorator Nodes brauchen oft sowohl eine Scala.js-Facade/Node-Klasse als auch passende Registrierung in `withNodes`.
- Die CodeMirror-Integration liegt inzwischen unter `lexical.codemirror`; alte Top-Level-Pfade koennen noch in Git-Status oder Historie auftauchen.
- Link- und Image-Dialoge sind Demo-spezifisch an `DialogService` gekoppelt. Bibliotheksmodule sollten moeglichst nicht hart an Demo-DOM-Strukturen haengen.
- `docs/index.html` und `docs/assets/*` sind Build-Ergebnis. Aenderungen an Demo-Quellen gehoeren normalerweise nach `application/src/main/webapp` oder `application/src/main/scala`.

## Stil und Aenderungsstrategie

- Facades zu JS-Bibliotheken knapp und nah an der upstream API halten.
- Oeffentliche Builder- und Modul-APIs nicht leichtfertig brechen; das Projekt ist mit `early-semver` markiert und published nach Maven Central/Sonatype.
- Neue Module sollten dem Muster `EditorModule` plus `ToolbarMetadata` folgen.
- Demo-Code darf konkreter und DOM-naeher sein als Bibliothekscode.
- Bibliothekscode sollte wiederverwendbar bleiben und keine Demo-Texte, Demo-Styles oder globalen Browser-Hooks voraussetzen.
- Bei UI-Arbeit die vorhandene visuelle Sprache bewahren: ruhige Flaechen, klare Schichten, bewusste Typografie, kein generisches Dashboard-Gefuehl.
- Keine unnoetigen Kommentare einfuegen; bestehende Lesbarkeit lieber durch klare Struktur verbessern.

## Schneller Einstieg fuer spaetere Agenten

1. `..\AGENTS.md` lesen.
2. `git status --short` pruefen und fremde Aenderungen respektieren.
3. Fuer Bibliotheksverhalten zuerst `LexicalBuilder.scala`, `EditorModule.scala`, `Lexical.scala` und das passende Feature-Modul lesen.
4. Fuer Demo-/UI-Verhalten zuerst `Demo.scala`, `application/src/main/webapp/index.html`, `application/src/main/webapp/src/main.js` und die CSS-Dateien lesen.
5. Vor groesseren API-Aenderungen kurz pruefen, ob README, Demo und Build-/Publishing-Metadaten mitgezogen werden muessen.

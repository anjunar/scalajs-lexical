# Scala.js Lexical

`scalajs-lexical` is a Scala.js wrapper for [Lexical](https://lexical.dev/), Meta's extensible rich-text editor framework.

It gives Scala.js applications a typed way to create Lexical editors, register nodes, compose toolbar modules, wire commands, and keep application-specific UI such as dialogs outside of the reusable library layer.

## Why This Library?

Lexical is powerful, but its JavaScript API is command-oriented and heavily shaped by dynamic objects. In Scala.js projects that can quickly lead to:

- scattered `js.Dynamic` calls
- weakly typed command payloads
- duplicated toolbar glue
- editor setup code that is hard to reuse
- dialog and UI behavior mixed into editor modules

`scalajs-lexical` wraps the common Lexical pieces in Scala-facing facades and modules while staying close to the upstream API.

The library is built around a few core ideas:

- configure editors through `LexicalBuilder`
- model editor actions as `EditorModule`
- use typed command payloads for custom behavior
- keep application dialogs customizable
- expose Lexical state as JSON when needed
- support rich text, lists, tables, links, images, markdown shortcuts, history, and CodeMirror blocks

## Installation

Add the library to your Scala.js project:

```scala
libraryDependencies += "com.anjunar" %%% "scalajs-lexical" % "1.0.6"
```

Install the NPM companion package next to it:

```bash
npm install @anjunar/scalajs-lexical
```

The current build uses:

- Scala `3.8.3`
- Scala.js DOM `2.8.1`
- Lexical `0.43.x`
- ES module output targeting `ES2021`

For a Vite-based app, import the generated Scala.js entrypoint and the library CSS:

```javascript
import '@anjunar/scalajs-lexical/index.css'
import 'scalajs:main.js'
```

The NPM package intentionally carries the Lexical and CodeMirror JavaScript dependencies used by the Scala.js facades, so application builds do not have to duplicate every `@lexical/*` and `@codemirror/*` dependency manually.

## Minimal Editor

The smallest useful setup creates an editor, registers rich text defaults, and mounts it into an existing DOM element.

```scala
import lexical.*
import scala.scalajs.js
import org.scalajs.dom

@main
def main(): Unit =
  val container =
    dom.document.getElementById("editor").asInstanceOf[dom.HTMLElement]

  val editor =
    new LexicalBuilder()
      .withNamespace("My Editor")
      .withPlaceholder("Start writing...")
      .build(container)

  editor.focus(() => (), js.Dynamic.literal().asInstanceOf[EditorFocusOptions])
```

HTML:

```html
<div id="editor"></div>
```

## Quick Start With Theme And Toolbar

Most applications will configure a theme, register nodes, add modules, and render a toolbar.

```scala
import lexical.*
import scala.scalajs.js
import org.scalajs.dom

@main
def main(): Unit =
  val editorContainer =
    dom.document.getElementById("editor").asInstanceOf[dom.HTMLElement]

  val toolbarContainer =
    dom.document.getElementById("toolbar").asInstanceOf[dom.HTMLElement]

  val historyState = LexicalHistory.createEmptyHistoryState()

  val builder =
    new LexicalBuilder()
      .withNamespace("Scala.js Lexical")
      .withPlaceholder("Write something rich...")
      .withTheme(
        new EditorThemeBuilder()
          .withParagraph("lexical-paragraph")
          .withTextBold("lexical-text-bold")
          .withTextItalic("lexical-text-italic")
          .withTextUnderline("lexical-text-underline")
          .withTextStrikethrough("lexical-text-strikethrough")
          .withCode("lexical-text-code")
          .build()
      )
      .withNodes(js.Array(
        LexicalLink.LinkNode,
        LexicalRichText.HeadingNode,
        LexicalRichText.QuoteNode,
        LexicalList.ListNode,
        LexicalList.ListItemNode,
        LexicalTable.TableNode,
        LexicalTable.TableRowNode,
        LexicalTable.TableCellNode,
        LexicalCode.CodeNode
      ))
      .withToolbar(
        new UndoModule(),
        new RedoModule(),
        new HeadingDropdown(),
        EditorModules.BOLD,
        EditorModules.ITALIC,
        EditorModules.UNDERLINE,
        EditorModules.STRIKETHROUGH,
        ListModules.BULLET,
        ListModules.NUMBERED,
        new LinkModule(),
        new TableModule(),
        new RemoveTableModule()
      )
      .withFloatingToolbar(
        EditorModules.BOLD,
        EditorModules.ITALIC,
        EditorModules.UNDERLINE,
        EditorModules.STRIKETHROUGH,
        new LinkModule()
      )
      .withModules(
        new HistoryModule(historyState),
        new MarkdownModule()
      )
      .onStateChange { json =>
        dom.console.log("Editor state:", json)
      }

  val editor = builder.build(editorContainer)

  LexicalList.registerList(editor)

  val registry = new ToolbarRegistry(builder.getRibbonModules.toList)
  val toolbar = new ToolbarManager(editor, registry, new RibbonRenderer())
  toolbar.createToolbar(toolbarContainer)
```

## Dialogs Stay In Your Application

Editor modules should not own your product UI. `LinkModule` demonstrates the intended pattern:

- the module dispatches `OPEN_LINK_DIALOG_COMMAND`
- your application opens whatever dialog it wants
- the dialog confirms with `INSERT_LINK_COMMAND`
- the library performs the Lexical update

```scala
import lexical.*
import org.scalajs.dom

editor.registerCommand(
  LinkCommands.OPEN_LINK_DIALOG_COMMAND,
  (payload: LinkCommands.LinkDialogPayload, editor: LexicalEditor) => {
    val selectionSnapshot = payload.selection
    val currentUrl = payload.currentUrl

    editor.getDialogService.show(
      "Insert Link",
      () => {
        val content = dom.document.createElement("div").asInstanceOf[dom.HTMLElement]

        val input = dom.document.createElement("input").asInstanceOf[dom.HTMLInputElement]
        input.setAttribute("type", "url")
        input.value = currentUrl
        input.placeholder = "https://example.com"
        input.id = "link-url-input"

        content.appendChild(input)
        content
      },
      content => {
        val input =
          content.querySelector("#link-url-input").asInstanceOf[dom.HTMLInputElement]

        val enteredUrl = input.value.trim

        editor.dispatchCommand(
          LinkCommands.INSERT_LINK_COMMAND,
          new LinkCommands.LinkInsertPayload:
            var url = enteredUrl
            var selection = selectionSnapshot
        )
      }
    )

    true
  },
  COMMAND_PRIORITY.LOW
)
```

The important detail is to avoid reusing field names as local values inside anonymous `js.Object` payloads. Prefer names such as `enteredUrl` and `selectionSnapshot` instead of:

```scala
val url = input.value.trim
new LinkCommands.LinkInsertPayload:
  var url = url
```

## Dialog Service

The library only defines a small dialog abstraction. Applications provide the implementation.

```scala
import lexical.DialogService
import org.scalajs.dom
import org.scalajs.dom.HTMLElement

final class BrowserDialogService extends DialogService:
  override def show(
      title: String,
      contentProvider: () => HTMLElement,
      onConfirm: HTMLElement => Unit
  ): Unit =
    val content = contentProvider()
    val confirmed = dom.window.confirm(title)

    if confirmed then
      onConfirm(content)
```

Register it on the editor:

```scala
editor.setDialogService(new BrowserDialogService())
```

## Commands And Modules

Simple toolbar actions can be implemented with `CommandModule`.

```scala
import lexical.*

val clearEditor =
  new CommandModule[Unit](
    name = "Clear",
    command = Lexical.CLEAR_EDITOR_COMMAND,
    payload = (),
    iconName = Some("delete_sweep"),
    metadata = new ToolbarMetadata:
      val tabName = "Home"
      val sectionName = "Actions"
      val order = 10
  ) {}
```

For custom behavior, implement `EditorModule` directly:

```scala
import lexical.*

final class CustomFormatModule extends EditorModule:
  override def name: String = "Important"
  override def iconName: Option[String] = Some("priority_high")

  override def metadata: ToolbarMetadata = new ToolbarMetadata:
    val tabName = "Home"
    val sectionName = "Formatting"
    val order = 20

  override def execute(editor: LexicalEditor): Unit =
    editor.dispatchCommand(Lexical.FORMAT_TEXT_COMMAND, "bold")
    editor.dispatchCommand(Lexical.FORMAT_TEXT_COMMAND, "underline")
```

## CodeMirror Blocks

CodeMirror support lives under `lexical.codemirror`.

```scala
import lexical.*
import lexical.codemirror.{CodeMirrorModule, CodeMirrorNode}
import scala.scalajs.js

val builder =
  new LexicalBuilder()
    .withNodes(js.Array(
      js.constructorOf[CodeMirrorNode],
      LexicalCode.CodeNode
    ))
    .withToolbar(
      new CodeMirrorModule()
    )
```

You can also dispatch the insert command yourself:

```scala
import lexical.codemirror.CodeMirrorPlugin

editor.dispatchCommand(
  CodeMirrorPlugin.INSERT_CODEMIRROR_COMMAND,
  new CodeMirrorPlugin.CodeMirrorPayload:
    var code = "println(\"Hello from Scala.js\")"
    var language = "scala"
)
```

## State Changes

`LexicalBuilder.onStateChange` receives serialized editor state as JSON.

```scala
new LexicalBuilder()
  .onStateChange { json =>
    dom.document.getElementById("state").textContent = json
  }
```

This is useful for previews, persistence, debugging, or integration tests.

## Showcase Application

The repository includes a Vite-based showcase application in `application`.

It demonstrates:

- the `LexicalBuilder` setup flow
- ribbon and floating toolbars
- link and image dialogs supplied by application code
- table insertion
- markdown shortcuts
- CodeMirror blocks
- live Lexical JSON state rendering

Run it locally:

```bash
npm run dev
```

The Vite app serves the demo on `http://localhost:5173/` by default.

Build the static showcase:

```bash
npm run build
```

The static output is generated into `docs`.

## Project Structure

```text
library/src/main/scala/lexical
|-- Lexical.scala              # Facades for lexical and @lexical packages
|-- LexicalEditor.scala        # Editor facade and extension methods
|-- LexicalBuilder.scala       # Main editor builder
|-- EditorModule.scala         # Toolbar/module model
|-- ToolbarRenderer.scala      # Ribbon rendering
|-- LinkModule.scala           # Dialog hook and insert command
|-- TableModule.scala          # Table insertion module
|-- ImageNode.scala            # Custom image decorator node
`-- codemirror/                # CodeMirror 6 integration
```

## Development

Use the local sbt client:

```bash
sbtn-x86_64-pc-win32.exe library/compile
sbtn-x86_64-pc-win32.exe scalajs-lexical-demo/compile
sbtn-x86_64-pc-win32.exe test
```

For the web showcase:

```bash
npm run dev
npm run build
```

## When Should You Use It?

Use `scalajs-lexical` if:

- you want Lexical in a Scala.js application
- you prefer typed builder and module APIs over ad-hoc JavaScript glue
- you need rich-text editing with custom toolbar behavior
- your dialogs and product UI should remain application-owned

Do not use it if:

- you need a React plugin ecosystem directly
- you want a complete CMS editor out of the box
- you only need a plain textarea or simple markdown input

## Positioning

- vs raw Lexical JavaScript: stronger Scala-facing structure and typed payloads
- vs React Lexical plugins: framework-neutral Scala.js integration
- vs a custom editor: much less editor-engine code to own
- vs simple markdown editors: richer document structure and command-based editing

## Publishing To Maven Central

The reusable module is `library` and is published as:

```text
com.anjunar::scalajs-lexical
```

The build is configured for Sonatype Central publishing with `sbt-pgp`.

Typical release commands:

```bash
sbtn-x86_64-pc-win32.exe "library/publishSigned"
sbtn-x86_64-pc-win32.exe sonaUpload
sbtn-x86_64-pc-win32.exe sonaRelease
```

The demo project `scalajs-lexical-demo` is not published.

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.

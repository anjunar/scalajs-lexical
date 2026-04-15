package lexical

import scala.scalajs.js
import org.scalajs.dom
import lexical.codemirror.{$createCodeMirrorNode, CodeMirrorPlugin}
import org.scalajs.dom.console

@main
def main(): Unit =
  val editorContainer = dom.document.getElementById("editor")

  val editorConfig = js.Dynamic.literal(
    namespace = "Vanilla JS Demo",
    theme = js.Dynamic.literal(
      paragraph = "editor-paragraph",
      quote = "PlaygroundEditorTheme__quote"
    ),
    nodes = js.Array(js.Dynamic.global.CodeMirrorNode),
    onError = (error: js.Error) => throw error.asInstanceOf[Throwable],
    editable = true
  ).asInstanceOf[EditorConfig]

  val editor = Lexical.createEditor(editorConfig)

  editor.setRootElement(editorContainer.asInstanceOf[dom.HTMLElement])

  val unregisterKeyHandlers = setupKeyHandlers(editor)

  LexicalRichText.registerRichText(editor)
  LexicalHistory.registerHistory(editor, LexicalHistory.createEmptyHistoryState(), 300)
  CodeMirrorPlugin.register(editor)

  editor.update(() =>
    val root = Lexical.$getRoot()
    val paragraph = Lexical.$createParagraphNode()
    val textNode = Lexical.$createTextNode("Hello Lexical! Click and type here... ")
    paragraph.append(textNode)
    root.append(paragraph)
  , js.Dynamic.literal(tag = HISTORY_MERGE_TAG).asInstanceOf[EditorUpdateOptions])

  editor.focus(() => (), js.Dynamic.literal().asInstanceOf[EditorFocusOptions])

  setupToolbar(editor)

  dom.console.log("Lexical Demo initialized!")

def setupToolbar(editor: LexicalEditor): Unit =
  val toolbar = dom.document.getElementById("toolbar")

  val boldBtn = dom.document.createElement("button").asInstanceOf[dom.HTMLElement]
  boldBtn.textContent = "Bold"
  boldBtn.onclick = (_: dom.MouseEvent) =>
    editor.dispatchCommand(Lexical.FORMAT_TEXT_COMMAND, "bold")

  val italicBtn = dom.document.createElement("button").asInstanceOf[dom.HTMLElement]
  italicBtn.textContent = "Italic"
  italicBtn.onclick = (_: dom.MouseEvent) =>
    editor.dispatchCommand(Lexical.FORMAT_TEXT_COMMAND, "italic")

  val clearBtn = dom.document.createElement("button").asInstanceOf[dom.HTMLElement]
  clearBtn.textContent = "Clear"
  clearBtn.onclick = (_: dom.MouseEvent) =>
    editor.dispatchCommand(Lexical.CLEAR_EDITOR_COMMAND, ())

  val addBtn = dom.document.createElement("button").asInstanceOf[dom.HTMLElement]
  addBtn.textContent = "+ Para"
  addBtn.onclick = (_: dom.MouseEvent) =>
    editor.update(() =>
      val root = Lexical.$getRoot()
      val paragraph = Lexical.$createParagraphNode()
      val text = Lexical.$createTextNode("New paragraph! ")
      paragraph.append(text)
      root.append(paragraph)
    , js.Dynamic.literal().asInstanceOf[EditorUpdateOptions])

  val addCodeMirrorBtn = dom.document.createElement("button").asInstanceOf[dom.HTMLElement]
  addCodeMirrorBtn.textContent = "+ Code"
  addCodeMirrorBtn.onclick = (_: dom.MouseEvent) =>
    editor.update(() =>
      val root = Lexical.$getRoot()
      val codeMirrorNode = $createCodeMirrorNode("console.log('Hello from CodeMirror!');", "javascript")
      root.append(codeMirrorNode)
    , js.Dynamic.literal().asInstanceOf[EditorUpdateOptions])

  toolbar.appendChild(boldBtn)
  toolbar.appendChild(italicBtn)
  toolbar.appendChild(clearBtn)
  toolbar.appendChild(addBtn)
  toolbar.appendChild(addCodeMirrorBtn)

def setupKeyHandlers(editor: LexicalEditor): js.Function0[Unit] =
  editor.registerCommand(
    Lexical.KEY_ENTER_COMMAND,
    (_: Unit, _: LexicalEditor) =>
      dom.console.log("Enter pressed!")
      true,
    COMMAND_PRIORITY.HIGH
  )

val HISTORY_MERGE_TAG = "history-merge"

package lexical

import scala.scalajs.js
import org.scalajs.dom as dom
import lexical.{LexicalRichText, LexicalHistory, LexicalDragon}

@main
def main(): Unit =
  val editorContainer = dom.document.getElementById("editor")
  val editorElement = dom.document.createElement("div").asInstanceOf[dom.HTMLElement]
  editorElement.id = "lexical-editor"
  editorElement.style.cssText = "border: 1px solid #ccc; min-height: 200px; padding: 16px;"
  editorContainer.appendChild(editorElement)

  val editor = Lexical.createEditor(js.Dynamic.literal(
    namespace = "demo-editor",
    theme = js.Dynamic.literal(
      paragraph = "editor-paragraph"
    )
  ))

  editor.setRootElement(editorElement)

  LexicalRichText.registerRichText(editor)
  
  val historyState = LexicalHistory.createEmptyHistoryState()
  LexicalHistory.registerHistory(editor, historyState, 300)
  
  LexicalDragon.registerDragonSupport(editor)

  val updateOpts = js.Dynamic.literal(
    onUpdate = () => (),
    skipTransforms = false,
    tag = "init",
    discrete = false
  ).asInstanceOf[EditorUpdateOptions]

  editor.update(() =>
    val root = Lexical.$getRoot()
    val paragraph = Lexical.$createParagraphNode()
    val textNode = Lexical.$createTextNode("Hello Lexical! Click and type here... ")
    paragraph.append(textNode)
    root.append(paragraph)
  , updateOpts)

  editor.focus(() => (), js.Dynamic.literal().asInstanceOf[EditorFocusOptions])

  setupToolbar(editor)

  dom.console.log("Lexical Demo initialized!")

def setupToolbar(editor: LexicalEditor): Unit =
  val toolbar = dom.document.getElementById("toolbar")

  val updateOpts = js.Dynamic.literal(
    onUpdate = () => (),
    skipTransforms = false,
    tag = "",
    discrete = false
  ).asInstanceOf[EditorUpdateOptions]

  val boldBtn = dom.document.createElement("button").asInstanceOf[dom.HTMLElement]
  boldBtn.textContent = "Bold"
  boldBtn.onclick = (_: dom.MouseEvent) =>
    editor.dispatchCommand(Lexical.FORMAT_TEXT_COMMAND, "bold".asInstanceOf[js.Dynamic])

  val italicBtn = dom.document.createElement("button").asInstanceOf[dom.HTMLElement]
  italicBtn.textContent = "Italic"
  italicBtn.onclick = (_: dom.MouseEvent) =>
    editor.dispatchCommand(Lexical.FORMAT_TEXT_COMMAND, "italic".asInstanceOf[js.Dynamic])

  val clearBtn = dom.document.createElement("button").asInstanceOf[dom.HTMLElement]
  clearBtn.textContent = "Clear"
  clearBtn.onclick = (_: dom.MouseEvent) =>
    editor.dispatchCommand(Lexical.CLEAR_EDITOR_COMMAND, js.Dynamic.literal())

  val addBtn = dom.document.createElement("button").asInstanceOf[dom.HTMLElement]
  addBtn.textContent = "+ Para"
  addBtn.onclick = (_: dom.MouseEvent) =>
    editor.update(() =>
      val root = Lexical.$getRoot()
      val paragraph = Lexical.$createParagraphNode()
      val text = Lexical.$createTextNode("New paragraph! ")
      paragraph.append(text)
      root.append(paragraph)
    , updateOpts)

  toolbar.appendChild(boldBtn)
  toolbar.appendChild(italicBtn)
  toolbar.appendChild(clearBtn)
  toolbar.appendChild(addBtn)
package lexical

import scala.scalajs.js
import org.scalajs.dom
import codemirror.CodeMirrorModule

@main
def main(): Unit =
  val editorContainer = dom.document.getElementById("editor").asInstanceOf[dom.HTMLElement]
  val toolbarContainer = dom.document.getElementById("toolbar").asInstanceOf[dom.HTMLElement]

  val builder = new LexicalBuilder()
    .withNamespace("Lexical Scala.js Demo")
    .withTheme(js.Dynamic.literal(
      paragraph = "editor-paragraph",
      quote = "PlaygroundEditorTheme__quote"
    ))
    .withNodes(js.Array(js.constructorOf[codemirror.CodeMirrorNode]))
    .withModules(
      EditorModules.BOLD,
      EditorModules.ITALIC,
      new ParagraphModule(),
      new CodeMirrorModule(),
      EditorModules.CLEAR
    )

  val editor = builder.build(editorContainer)
  
  val toolbarManager = new ToolbarManager(editor, builder.getModules)
  toolbarManager.createToolbar(toolbarContainer)

  // Initial State
  editor.update(() =>
    val root = Lexical.$getRoot()
    val paragraph = Lexical.$createParagraphNode()
    val textNode = Lexical.$createTextNode("Hello Lexical! Click and type here... ")
    paragraph.append(textNode)
    root.append(paragraph)
  , js.Dynamic.literal(tag = 0).asInstanceOf[EditorUpdateOptions])

  editor.focus(() => (), js.Dynamic.literal().asInstanceOf[EditorFocusOptions])

  dom.console.log("Lexical Demo initialized with LexicalBuilder!")

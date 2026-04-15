package lexical

import scala.scalajs.js
import org.scalajs.dom
import codemirror.{$createCodeMirrorNode, CodeMirrorPlugin}

@main
def main(): Unit =
  val editorContainer = dom.document.getElementById("editor")

  val editorConfig = js.Dynamic.literal(
    namespace = "Vanilla JS Demo",
    theme = js.Dynamic.literal(
      paragraph = "editor-paragraph",
      quote = "PlaygroundEditorTheme__quote"
    ),
    nodes = js.Array(js.constructorOf[codemirror.CodeMirrorNode]),
    editable = true
    ).asInstanceOf[EditorConfig]

    val editor = Lexical.createEditor(editorConfig)

    editor.setRootElement(editorContainer.asInstanceOf[dom.HTMLElement])

    LexicalRichText.registerRichText(editor)
    LexicalHistory.registerHistory(editor, LexicalHistory.createEmptyHistoryState(), 300)
    
    val modules = js.Array[EditorModule](
      EditorModules.BOLD,
      EditorModules.ITALIC,
      new ParagraphModule(),
      new codemirror.CodeMirrorModule(),
      EditorModules.CLEAR
    )

    modules.foreach(_.register(editor))

    val toolbarContainer = dom.document.getElementById("toolbar").asInstanceOf[dom.HTMLElement]
    val toolbarManager = new ToolbarManager(editor, modules)
    toolbarManager.createToolbar(toolbarContainer)

    // Robust Decorator Listener for Vanilla JS
    editor.registerDecoratorListener((decorators: js.Dynamic) => {
      val decoratorsDict = decorators.asInstanceOf[js.Dictionary[dom.Node]]
      js.Object.entries(decoratorsDict).foreach { entry =>
        val nodeKey = entry._1
        val decoratorElement = entry._2
        val container = editor.getElementByKey(nodeKey)
        if (container != null && !container.contains(decoratorElement)) {
          container.innerHTML = ""
          container.appendChild(decoratorElement)
        }
      }
    })

  editor.update(() =>
    val root = Lexical.$getRoot()
    val paragraph = Lexical.$createParagraphNode()
    val textNode = Lexical.$createTextNode("Hello Lexical! Click and type here... ")
    paragraph.append(textNode)
    root.append(paragraph)
  , js.Dynamic.literal(tag = "history_merge").asInstanceOf[EditorUpdateOptions])

  editor.focus(() => (), js.Dynamic.literal().asInstanceOf[EditorFocusOptions])

  dom.console.log("Lexical Demo initialized!")

class ParagraphModule extends EditorModule:
  override def name: String = "Paragraph"
  override def iconName: Option[String] = Some("add")
  
  override def execute(editor: LexicalEditor): Unit =
    editor.update(() => {
      val root = Lexical.$getRoot()
      val paragraph = Lexical.$createParagraphNode()
      val text = Lexical.$createTextNode("New paragraph! ")
      paragraph.append(text)
      root.append(paragraph)
    }, js.Dynamic.literal().asInstanceOf[EditorUpdateOptions])

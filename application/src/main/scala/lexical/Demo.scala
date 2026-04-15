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
    .withTheme(new EditorThemeBuilder()
      .withParagraph("editor-paragraph")
      .withQuote("PlaygroundEditorTheme__quote")
      .build()
    )
    .withNodes(js.Array(
      js.constructorOf[codemirror.CodeMirrorNode],
      LexicalLink.LinkNode,
      LexicalRichText.HeadingNode,
      LexicalRichText.QuoteNode,
      LexicalList.ListNode,
      LexicalList.ListItemNode,
      LexicalCode.CodeNode
    ))
    .withToolbar(
      ToolbarGroup(EditorModules.BOLD, EditorModules.ITALIC, new LinkModule()),
      ToolbarSeparator(),
      ToolbarGroup(new ParagraphModule(), new CodeMirrorModule()),
      ToolbarSeparator(),
      EditorModules.CLEAR
    )
    .withFloatingToolbar(
      EditorModules.BOLD,
      EditorModules.ITALIC,
      new LinkModule()
    )
    .withModules(new MarkdownModule())
    .onStateChange(json => {
      val stateDiv = dom.document.getElementById("state")
      if (stateDiv != null) {
        stateDiv.textContent = json
      }
    })

  val editor = builder.build(editorContainer)
  
  val toolbarManager = new ToolbarManager(editor, builder.getToolbarElements)
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

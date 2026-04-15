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
    .withPlaceholder("Enter some rich text...")
    .withTheme(new EditorThemeBuilder()
      .withParagraph("lexical-paragraph")
      .withQuote("lexical-quote")
      .withTextBold("lexical-text-bold")
      .withTextItalic("lexical-text-italic")
      .withCode("lexical-text-code")
      .build()
    )
    .withNodes(js.Array(
      js.constructorOf[codemirror.CodeMirrorNode],
      LexicalLink.LinkNode,
      LexicalRichText.HeadingNode,
      LexicalRichText.QuoteNode,
      LexicalList.ListNode,
      LexicalList.ListItemNode,
      LexicalCode.CodeNode,
      js.constructorOf[ImageNode]
    ))
    .withToolbar(
      EditorModules.BOLD,
      EditorModules.ITALIC,
      new LinkModule(),
      new ImageModule(),
      new ParagraphModule(),
      new CodeMirrorModule(),
      EditorModules.CLEAR,
      new MarkdownModule()
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
  
  val ribbonModules = builder.getRibbonModules.toList
  val registry = new ToolbarRegistry(ribbonModules)
  val renderer = new RibbonRenderer()
  
  val toolbarManager = new ToolbarManager(editor, registry, renderer)
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

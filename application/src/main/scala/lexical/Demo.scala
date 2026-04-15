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
  
  editor.registerCommand(ImageNode.OPEN_IMAGE_DIALOG_COMMAND, (editor: LexicalEditor, _: LexicalEditor) => {
    editor.getDialogService.show("Insert Image", () => {
      val content = dom.document.createElement("div").asInstanceOf[dom.HTMLElement]
      
      val urlLabel = dom.document.createElement("label").asInstanceOf[dom.HTMLElement]
      urlLabel.textContent = "Image URL"
      val urlInput = dom.document.createElement("input").asInstanceOf[dom.HTMLInputElement]
      urlInput.placeholder = "https://..."
      urlInput.id = "image-url-input"
      urlInput.style.width = "100%"
      urlInput.style.marginBottom = "8px"
      
      val altLabel = dom.document.createElement("label").asInstanceOf[dom.HTMLElement]
      altLabel.textContent = "Alt Text"
      val altInput = dom.document.createElement("input").asInstanceOf[dom.HTMLInputElement]
      altInput.placeholder = "Description"
      altInput.id = "image-alt-input"
      altInput.style.width = "100%"
      
      content.appendChild(urlLabel)
      content.appendChild(urlInput)
      content.appendChild(altLabel)
      content.appendChild(altInput)
      content
    }, (content) => {
      val urlInput = content.querySelector("#image-url-input").asInstanceOf[dom.HTMLInputElement]
      val altInput = content.querySelector("#image-alt-input").asInstanceOf[dom.HTMLInputElement]
      val src = urlInput.value
      if (src.nonEmpty) {
        editor.dispatchCommand(ImageNode.INSERT_IMAGE_COMMAND, new ImagePayload:
          var src = urlInput.value
          var altText = altInput.value
          var maxWidth = 500
        )
      }
    })
    true
  }, 1)
  
  editor.setDialogService(new DemoDialogService())
  
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

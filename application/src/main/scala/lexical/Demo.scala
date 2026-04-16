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
      .withTextUnderline("lexical-text-underline")
      .withTextStrikethrough("lexical-text-strikethrough")
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
      new HeadingDropdown(),
      EditorModules.BOLD,
      EditorModules.ITALIC,
      EditorModules.UNDERLINE,
      EditorModules.STRIKETHROUGH,
      ListModules.BULLET,
      ListModules.NUMBERED,
      new LinkModule(),
      new ImageModule(),
      new CodeMirrorModule(),
      new MarkdownModule()
    )

    .withFloatingToolbar(
      EditorModules.BOLD,
      EditorModules.ITALIC,
      EditorModules.UNDERLINE,
      EditorModules.STRIKETHROUGH,
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
  js.Dynamic.global.window.editor = editor
  LexicalList.registerList(editor)
  
  editor.registerCommand(ImageNode.OPEN_IMAGE_DIALOG_COMMAND, (editor: LexicalEditor, _: LexicalEditor) => {
    editor.getDialogService.show("Insert Image", () => {
      val content = dom.document.createElement("div").asInstanceOf[dom.HTMLElement]
      
      val preview = dom.document.createElement("img").asInstanceOf[dom.HTMLImageElement]
      preview.style.width = "320px"
      preview.style.height = "200px"
      preview.style.backgroundColor = "#eee"
      preview.style.display = "block"
      preview.style.marginBottom = "10px"
      preview.style.setProperty("object-fit", "cover")
      preview.id = "image-preview"

      val fileInput = dom.document.createElement("input").asInstanceOf[dom.HTMLInputElement]
      fileInput.setAttribute("type", "file")
      fileInput.accept = "image/*"
      fileInput.style.marginBottom = "10px"
      
      fileInput.onchange = (_: dom.Event) => {
        val file = fileInput.files.item(0)
        if (file != null) {
          val reader = new dom.FileReader()
          reader.onload = (e: dom.Event) => {
            preview.src = reader.result.asInstanceOf[String]
          }
          reader.readAsDataURL(file)
        }
      }

      val altLabel = dom.document.createElement("label").asInstanceOf[dom.HTMLElement]
      altLabel.textContent = "Alt Text"
      val altInput = dom.document.createElement("input").asInstanceOf[dom.HTMLInputElement]
      altInput.placeholder = "Description"
      altInput.id = "image-alt-input"
      altInput.style.width = "100%"
      
      val widthLabel = dom.document.createElement("label").asInstanceOf[dom.HTMLElement]
      widthLabel.textContent = "Width (px)"
      val widthInput = dom.document.createElement("input").asInstanceOf[dom.HTMLInputElement]
      widthInput.setAttribute("type", "number")
      widthInput.value = "500"
      widthInput.id = "image-width-input"
      widthInput.style.width = "100%"

      content.appendChild(preview)
      content.appendChild(fileInput)
      content.appendChild(altLabel)
      content.appendChild(altInput)
      content.appendChild(widthLabel)
      content.appendChild(widthInput)
      content
    }, (content) => {
      val preview = content.querySelector("#image-preview").asInstanceOf[dom.HTMLImageElement]
      val altInput = content.querySelector("#image-alt-input").asInstanceOf[dom.HTMLInputElement]
      val widthInput = content.querySelector("#image-width-input").asInstanceOf[dom.HTMLInputElement]
      val src = preview.src
      if (src.nonEmpty) {
        editor.dispatchCommand(ImageNode.INSERT_IMAGE_COMMAND, new ImagePayload:
          var src = preview.src
          var altText = altInput.value
          var maxWidth = widthInput.value.toIntOption.getOrElse(500)
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

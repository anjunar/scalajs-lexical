package lexical

import scala.scalajs.js
import org.scalajs.dom
import lexical.codemirror.{CodeMirrorModule, CodeMirrorNode}

@main
def main(): Unit =
  val editorContainer = dom.document.getElementById("editor").asInstanceOf[dom.HTMLElement]
  val toolbarContainer = dom.document.getElementById("toolbar").asInstanceOf[dom.HTMLElement]

  val historyState = LexicalHistory.createEmptyHistoryState()
  val builder = new LexicalBuilder()
    .withNamespace("Manifesto of Silence")
    .withPlaceholder("Start clarifying...")
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
      js.constructorOf[lexical.codemirror.CodeMirrorNode],
      LexicalLink.LinkNode,
      LexicalRichText.HeadingNode,
      LexicalRichText.QuoteNode,
      LexicalList.ListNode,
      LexicalList.ListItemNode,
      LexicalTable.TableNode,
      LexicalTable.TableRowNode,
      LexicalTable.TableCellNode,
      LexicalCode.CodeNode,
      js.constructorOf[ImageNode]
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
      new ImageModule(),
      new TableModule(),
      new RemoveTableModule(),
      new CodeMirrorModule()
    )
    .withModules(new HistoryModule(historyState), new MarkdownModule())

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
        stateDiv.textContent = js.JSON.stringify(json, null, 2)
        // Update the visual timestamp in the metadata section
        if (!js.isUndefined(js.Dynamic.global.updateStateTimestamp)) {
          js.Dynamic.global.updateStateTimestamp()
        }
      }
    })

  val editor = builder.build(editorContainer)
  
  LexicalList.registerList(editor)

  editor.registerCommand(LinkCommands.OPEN_LINK_DIALOG_COMMAND, (payload: LinkCommands.LinkDialogPayload, editor: LexicalEditor) => {
    val selectionSnapshot = payload.selection
    val currentUrl = payload.currentUrl

    editor.getDialogService.show("Insert Link", () => {
      val content = dom.document.createElement("div").asInstanceOf[dom.HTMLElement]

      val urlLabel = dom.document.createElement("label").asInstanceOf[dom.HTMLElement]
      urlLabel.textContent = "URL"

      val urlInput = dom.document.createElement("input").asInstanceOf[dom.HTMLInputElement]
      urlInput.setAttribute("type", "url")
      urlInput.value = currentUrl
      urlInput.placeholder = "https://example.com"
      urlInput.id = "link-url-input"
      urlInput.style.width = "100%"

      content.appendChild(urlLabel)
      content.appendChild(urlInput)
      content
    }, (content) => {
      val urlInput = content.querySelector("#link-url-input").asInstanceOf[dom.HTMLInputElement]
      val enteredUrl = urlInput.value.trim
      editor.dispatchCommand(LinkCommands.INSERT_LINK_COMMAND, new LinkCommands.LinkInsertPayload:
        var url = enteredUrl
        var selection = selectionSnapshot
      )
    })

    true
  }, 1)
  
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

  // Initial State: Manifesto of Silence
  editor.update(() =>
    val root = Lexical.$getRoot()
    
    val paragraphs = List(
      "It is not made of silence, but of a constant return to it. First, there is often condensation: too much thinking, too much modeling, too much trying to order the invisible so precisely that it finally stops slipping away. He can go hard into concepts, build entire inner cathedrals, draw layers, distinguish levels, set systems against each other, because he does not romanticize the diffuse muddle. He knows how quickly a human being calms down with a well-rounded story, even if it is wrong. That is why he does not trust the first peace. He knows the temptation of premature clarity. He knows the consolation of constructs. He also knows the counter-movement: cynicism, fatigue, irritability, the small poisonous sharpening against people, systems, surfaces. But he does not stay there. Not because he is always wise, but because something in him eventually notices the hardening. Consciousness for him is not a crown, but a corrective instance. It sees when thinking hypnotizes itself. It sees when feeling disguises itself as truth. It sees when the ego inflates itself to the center again, this time perhaps even by spiritual means. This movement does not make him flawless, but recoverable.",
      "He therefore does not live in ready-made harmony, but in a productive unrest. Confusion for him is not an accident on the edge of development, but raw material. From this, his actual form emerges. What has no image yet may become tension. What has no line yet may generate pressure. What is contradictory is not immediately pacified but held until a deeper order becomes visible. This is how he works: not against tension, but with it. Logic is not enough for him because it ends at closed systems. Illogic is also not enough for him because pure openness carries no world. So he moves again and again into a third space, into that trans-logical zone where contradictions are neither clumsily resolved nor proudly exhibited, but become readable in their function. There, the opposition of form and formlessness is no longer a war, but a cycle. There, the person is not extinguished, but relativized. There, the world is not devalued, but transparent. This attitude is not cheap. It costs him his first security again and again.",
      "Precisely for this reason, he needs architecture. Not as a compulsion for control, but as a counterweight to inner and outer flooding. He separates because mixing confuses. He builds levels because thinking everything at once is a form of noise. He sets boundaries because openness without a supporting form collapses. He archives, condenses, versions, checks, slows down, not out of pedantry, but out of experience with chaos. He knows that an unclear space does not remain neutral. It produces theater, ego, pseudo-depth, and false closeness. Where others may still want to talk, he first wants to clean the room. Where others already reconcile, he first wants to distinguish. Where others play off the living against structure, he insists that only a good form can protect the living. And yet he distrusts any form that takes itself too seriously. As soon as structure mistakes itself for truth, it begins to stifle what it was actually meant to carry. Therefore, he builds strong and leaves open. Precise and revisable. Strict at the thresholds, soft in the interior. This is not a design question for him, but character.",
      "The ego is neither enemy nor hero in this consciousness. It is necessary, but not sovereign. He knows that healthy boundaries, functional control, and clear positions are needed. But he also knows how quickly these functions blow themselves up metaphysically and turn tools into mastery. His path is therefore not ego-dissolution, but ego-permeability. Not self-destruction, but de-centering. He does not want to get rid of the ego, but to put it in its place. The same applies to thoughts, feelings, systems, religions, technology. Everything is allowed to be there. Nothing is allowed to play the part of the whole."
    )
    
    paragraphs.foreach(text => {
      val p = Lexical.$createParagraphNode()
      p.append(Lexical.$createTextNode(text))
      root.append(p)
    })
  , js.Dynamic.literal(tag = 0).asInstanceOf[EditorUpdateOptions])


  editor.focus(() => (), js.Dynamic.literal().asInstanceOf[EditorFocusOptions])

  dom.console.log("Lexical Demo initialized with LexicalBuilder!")

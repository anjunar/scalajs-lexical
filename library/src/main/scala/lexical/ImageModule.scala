package lexical

import scala.scalajs.js
import org.scalajs.dom

class ImageModule extends EditorModule:
  override def name: String = "Image"
  override def iconName: Option[String] = Some("image")

  override def execute(editor: LexicalEditor): Unit =
    val content = dom.document.createElement("div").asInstanceOf[dom.HTMLElement]
    
    val urlLabel = dom.document.createElement("label").asInstanceOf[dom.HTMLElement]
    urlLabel.textContent = "Image URL"
    val urlInput = dom.document.createElement("input").asInstanceOf[dom.HTMLInputElement]
    urlInput.placeholder = "https://..."
    urlInput.style.width = "100%"
    urlInput.style.marginBottom = "8px"
    
    val altLabel = dom.document.createElement("label").asInstanceOf[dom.HTMLElement]
    altLabel.textContent = "Alt Text"
    val altInput = dom.document.createElement("input").asInstanceOf[dom.HTMLInputElement]
    altInput.placeholder = "Description"
    altInput.style.width = "100%"
    
    content.appendChild(urlLabel)
    content.appendChild(urlInput)
    content.appendChild(altLabel)
    content.appendChild(altInput)
    
    Dialog.show("Insert Image", content, () => {
      val src = urlInput.value
      if (src.nonEmpty) {
        editor.dispatchCommand(ImageNode.INSERT_IMAGE_COMMAND, new ImagePayload:
          var src = urlInput.value
          var altText = altInput.value
          var maxWidth = 500
        )
      }
    })

  override def register(editor: LexicalEditor): js.Function0[Unit] =
    ImageNode.register(editor)

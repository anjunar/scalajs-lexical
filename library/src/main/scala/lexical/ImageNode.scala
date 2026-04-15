package lexical

import scala.scalajs.js
import scala.scalajs.js.annotation.*
import org.scalajs.dom

class ImageNode(var src: String, var altText: String, var maxWidth: Int, key: Option[NodeKey] = None) 
  extends DecoratorNode[dom.HTMLElement](key.getOrElse("")):
  
  def this() = this("", "", 0)

  override def getType(): String = "image"
  
  override def createDOM(config: js.Dynamic, editor: LexicalEditor): dom.HTMLElement =
    val container = dom.document.createElement("span").asInstanceOf[dom.HTMLElement]
    container.className = "image-node-container"
    container

  override def updateDOM(prevNode: js.Any, domElement: dom.HTMLElement, config: js.Dynamic): Boolean = false

  override def decorate(editor: LexicalEditor): dom.HTMLElement =
    val container = dom.document.createElement("div").asInstanceOf[dom.HTMLElement]
    container.className = "image-node-container"

    val img = dom.document.createElement("img").asInstanceOf[dom.HTMLImageElement]
    img.src = src
    img.alt = altText
    img.style.maxWidth = s"${maxWidth}px"
    container.appendChild(img)

    // Resize handle
    val resizer = dom.document.createElement("div").asInstanceOf[dom.HTMLElement]
    resizer.className = "image-resizer bottom-right"
    container.appendChild(resizer)

    resizer.onmousedown = (e: dom.MouseEvent) => {
      e.preventDefault()
      val startX = e.clientX
      val startWidth = maxWidth

      var onMouseMove: js.Function1[dom.MouseEvent, Unit] = null
      var onMouseUp: js.Function1[dom.MouseEvent, Unit] = null

      onMouseMove = (moveEvent: dom.MouseEvent) => {
        val newWidth = (startWidth + (moveEvent.clientX - startX)).toInt
        img.style.maxWidth = s"${newWidth}px"
        maxWidth = newWidth
      }

      onMouseUp = (upEvent: dom.MouseEvent) => {
        dom.window.removeEventListener("mousemove", onMouseMove)
        dom.window.removeEventListener("mouseup", onMouseUp)
      }

      dom.window.addEventListener("mousemove", onMouseMove)
      dom.window.addEventListener("mouseup", onMouseUp)
    }


    container.onclick = (e: dom.MouseEvent) => {
        e.stopPropagation()
        // Remove focus from all other images if necessary
        dom.document.querySelectorAll(".image-node-container").foreach(_.classList.remove("focused"))
        container.classList.add("focused")
    }

    dom.document.addEventListener("click", (_: dom.Event) => {
        container.classList.remove("focused")
    })
    
    container

  override def exportJSON(): js.Dynamic =
    js.Dynamic.literal(
      src = src,
      altText = altText,
      maxWidth = maxWidth,
      `type` = "image",
      version = 1
    )

  def register(editor: LexicalEditor): js.Function0[Unit] =
    editor.registerCommand(
      ImageNode.INSERT_IMAGE_COMMAND,
      (payload: ImagePayload, lexicalEditor: LexicalEditor) => {
        lexicalEditor.update(() => {
          val imageNode = new ImageNode(payload.src, payload.altText, payload.maxWidth)
          Lexical.$insertNodes(js.Array(imageNode))
        }, js.Dynamic.literal().asInstanceOf[EditorUpdateOptions])
        true
      },
      COMMAND_PRIORITY.EDITOR
    )

object ImageNode:

  @JSExportStatic
  def getType(): String = "image"

  @JSExportStatic
  def clone(other : ImageNode) : ImageNode = new ImageNode(other.src, other.altText, other.maxWidth, Some(other.getKey()))

  @JSExportStatic
  def importJSON(serializedNode: js.Dynamic): ImageNode =
    new ImageNode(
      serializedNode.src.asInstanceOf[String],
      serializedNode.altText.asInstanceOf[String],
      serializedNode.maxWidth.asInstanceOf[Int]
    )

  val INSERT_IMAGE_COMMAND: LexicalCommand[ImagePayload] = Lexical.createCommand[ImagePayload]("INSERT_IMAGE_COMMAND")
  val OPEN_IMAGE_DIALOG_COMMAND: LexicalCommand[LexicalEditor] = Lexical.createCommand[LexicalEditor]("OPEN_IMAGE_DIALOG_COMMAND")

  def register(editor: LexicalEditor): js.Function0[Unit] =
    editor.registerCommand(
      ImageNode.INSERT_IMAGE_COMMAND,
      (payload: ImagePayload, lexicalEditor: LexicalEditor) => {
        lexicalEditor.update(() => {
          val imageNode = new ImageNode(payload.src, payload.altText, payload.maxWidth)
          Lexical.$insertNodes(js.Array(imageNode))
        }, js.Dynamic.literal().asInstanceOf[EditorUpdateOptions])
        true
      },
      COMMAND_PRIORITY.EDITOR
    )



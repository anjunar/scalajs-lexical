package lexical

import scala.scalajs.js
import scala.scalajs.js.annotation.*
import org.scalajs.dom

class ImageNode(var src: String, var altText: String, var maxWidth: Int, key: Option[NodeKey] = None) 
  extends DecoratorNode[dom.HTMLElement](key.getOrElse("")):
  
  override def getType(): String = "image"
  
  override def createDOM(config: js.Dynamic, editor: LexicalEditor): dom.HTMLElement =
    val container = dom.document.createElement("span").asInstanceOf[dom.HTMLElement]
    container.className = "image-node-container"
    container

  override def updateDOM(prevNode: js.Any, domElement: dom.HTMLElement, config: js.Dynamic): Boolean = false

  override def decorate(editor: LexicalEditor): dom.HTMLElement =
    val img = dom.document.createElement("img").asInstanceOf[dom.HTMLImageElement]
    img.src = src
    img.alt = altText
    img.style.maxWidth = s"${maxWidth}px"
    img

  override def exportJSON(): js.Dynamic =
    js.Dynamic.literal(
      src = src,
      altText = altText,
      maxWidth = maxWidth,
      `type` = "image",
      version = 1
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

  @JSExportStatic
  def isImageNode(node: LexicalNode | Null): Boolean = node != null && node.getType() == getType()

  val INSERT_IMAGE_COMMAND: LexicalCommand[ImagePayload] = Lexical.createCommand[ImagePayload]("INSERT_IMAGE_COMMAND")

  def register(editor: LexicalEditor): js.Function0[Unit] =
    editor.registerCommand(
      INSERT_IMAGE_COMMAND,
      (payload: ImagePayload, lexicalEditor: LexicalEditor) => {
        lexicalEditor.update(() => {
          val imageNode = new ImageNode(payload.src, payload.altText, payload.maxWidth)
          Lexical.$insertNodes(js.Array(imageNode))
        }, js.Dynamic.literal().asInstanceOf[EditorUpdateOptions])
        true
      },
      COMMAND_PRIORITY.EDITOR
    )

package lexical

import scala.scalajs.js
import org.scalajs.dom

class ImageModule extends EditorModule:
  override def name: String = "Image"
  override def iconName: Option[String] = Some("image")
  override def metadata: ToolbarMetadata = new ToolbarMetadata:
    val tabName = "Insert"
    val sectionName = "Media"
    val order = 1

  override def execute(editor: LexicalEditor): Unit =
    editor.dispatchCommand(ImageNode.OPEN_IMAGE_DIALOG_COMMAND, editor)

  override def register(editor: LexicalEditor): js.Function0[Unit] =
    ImageNode.register(editor)

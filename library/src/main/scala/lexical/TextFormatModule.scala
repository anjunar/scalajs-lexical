package lexical

import scala.scalajs.js

class TextFormatModule(
    val formatType: String,
    val bitmask: Int,
    override val iconName: Option[String] = None,
    override val keyBinding: Option[String] = None
) extends EditorModule:
  override def name: String = formatType.capitalize

  override def isActive(editor: LexicalEditor): Boolean =
    editor.getEditorState().read(() => {
      val selection = Lexical.$getSelection()
      selection match
        case s: RangeSelection => (s.format & bitmask) != 0
        case _ => false
    }).asInstanceOf[Boolean]

  override def execute(editor: LexicalEditor): Unit =
    editor.dispatchCommand(Lexical.FORMAT_TEXT_COMMAND, formatType)

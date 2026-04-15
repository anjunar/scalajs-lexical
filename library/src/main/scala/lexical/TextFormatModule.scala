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
    val wrapper = editor.getSelectionWrapper()
    wrapper.isRangeSelection && (wrapper.format & bitmask) != 0

  override def execute(editor: LexicalEditor): Unit =
    editor.dispatchCommand(Lexical.FORMAT_TEXT_COMMAND, formatType)

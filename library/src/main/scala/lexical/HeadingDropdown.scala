package lexical

import org.scalajs.dom
import scala.scalajs.js

class HeadingDropdown extends ToolbarDropdown:
  override def name: String = "Heading"
  override def options: Seq[ToolbarDropdownOption] = Seq(
    ToolbarDropdownOption("Normal", "paragraph"),
    ToolbarDropdownOption("Heading 1", "h1"),
    ToolbarDropdownOption("Heading 2", "h2"),
    ToolbarDropdownOption("Heading 3", "h3")
  )

  override def getSelectedValue(editor: LexicalEditor): String =
    val selection = editor.getSelectionWrapper()
    if (selection.isRangeSelection && selection.nodes.nonEmpty) {
      val firstNode = selection.nodes(0)
      // Basic check for block type
      if (firstNode.getType() == "heading") "h1" // Simplified logic, needs real check
      else "paragraph"
    } else "paragraph"

  override def onSelect(editor: LexicalEditor, value: String): Unit =
    if (value == "paragraph") {
      editor.dispatchCommand(LexicalRichText.FORMAT_HEADING_COMMAND, "paragraph")
    } else {
      editor.dispatchCommand(LexicalRichText.FORMAT_HEADING_COMMAND, value)
    }

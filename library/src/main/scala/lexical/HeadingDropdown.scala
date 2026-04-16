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
    editor.read(() =>
      val selection = Lexical.$getSelection()
      if (selection == null || !Lexical.$isRangeSelection(selection)) then
        "paragraph"
      else
        val rangeSelection = selection.asInstanceOf[js.Dynamic]
        val anchorNode = rangeSelection.anchor.getNode().asInstanceOf[js.Dynamic]
        val block = anchorNode.getTopLevelElementOrThrow().asInstanceOf[js.Dynamic]

        if (block != null && block.getType().asInstanceOf[String] == "heading") then
          block.getTag().asInstanceOf[String]
        else
          "paragraph"
    )

  override def onSelect(editor: LexicalEditor, value: String): Unit =
    editor.update(() =>
      val selection = Lexical.$getSelection()
      if (selection != null && Lexical.$isRangeSelection(selection)) then
        if (value == "paragraph") then
          LexicalSelection.$setBlocksType(
            selection.asInstanceOf[RangeSelection],
            () => Lexical.$createParagraphNode()
          )
        else
          LexicalSelection.$setBlocksType(
            selection.asInstanceOf[RangeSelection],
            () => LexicalRichText.$createHeadingNode(value)
          )
    , js.Dynamic.literal().asInstanceOf[EditorUpdateOptions])
    editor.focus(() => (), js.Dynamic.literal().asInstanceOf[EditorFocusOptions])

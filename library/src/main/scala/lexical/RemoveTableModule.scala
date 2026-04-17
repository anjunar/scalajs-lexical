package lexical

import org.scalajs.dom
import scala.scalajs.js

private val REMOVE_TABLE_COMMAND: LexicalCommand[Unit] = Lexical.createCommand[Unit]("REMOVE_TABLE_COMMAND")

class RemoveTableModule extends CommandModule[Unit](
  "Remove Table",
  REMOVE_TABLE_COMMAND,
  (),
  Some("delete"),
  metadata = new ToolbarMetadata {
    val tabName = "Insert"
    val sectionName = "Table"
    val order = 2
  }
) {
  override def canActivate(editor: LexicalEditor): Boolean = {
    val selection = Lexical.$getSelection()
    if (selection != null && Lexical.$isRangeSelection(selection)) {
      val rangeSelection = selection.asInstanceOf[RangeSelection]
      val anchorNode = rangeSelection.anchor.getNode()
      val table = Lexical.$findMatchingParent(anchorNode, (node: LexicalNode) => LexicalTable.TableNode.getType().asInstanceOf[String] == node.getType().asInstanceOf[String])
      table != null
    } else false
  }

  override def execute(editor: LexicalEditor): Unit = {
    editor.update(() => {
      val selection = Lexical.$getSelection()
      if (selection != null && Lexical.$isRangeSelection(selection)) {
        val rangeSelection = selection.asInstanceOf[RangeSelection]
        val anchorNode = rangeSelection.anchor.getNode()
        val table = Lexical.$findMatchingParent(anchorNode, (node: LexicalNode) => LexicalTable.TableNode.getType().asInstanceOf[String] == node.getType().asInstanceOf[String])
        if (table != null) {
          table.remove(true)
        }
      }
    }, js.Dynamic.literal().asInstanceOf[EditorUpdateOptions])
  }
}

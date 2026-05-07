package lexical

import scala.scalajs.js

class ListModule(val listType: String, override val command: LexicalCommand[Unit], name: String, icon: String) extends CommandModule[Unit](
  name,
  command,
  (),
  Some(icon),
  metadata = new ToolbarMetadata {
    val tabName = "Home"
    val sectionName = "Lists"
    val order = 2
  }
):
  override def isActive(editor: LexicalEditor): Boolean =
    editor.read(() => {
      val selection = Lexical.$getSelection()
      if (selection != null && Lexical.$isRangeSelection(selection)) {
        val nodes = selection.asInstanceOf[BaseSelection].getNodes()
        if (nodes.length > 0) {
          val parent = Lexical.$findMatchingParent(nodes(0), node => Lexical.$isElementNode(node))
          if (parent != null) {
            val block = nodes(0).getTopLevelElement()
            if (block != null && block.getType() == "list") {
              // We need to check the list type.
              // In Lexical, ListNode has a getListType() method.
              block.asInstanceOf[js.Dynamic].getListType().asInstanceOf[String] == listType
            } else false
          } else false
        } else false
      } else false
    })


object ListModules:
  val BULLET = new ListModule("bullet", LexicalList.INSERT_UNORDERED_LIST_COMMAND, "Bulleted List", "format_list_bulleted")
  val NUMBERED = new ListModule("number", LexicalList.INSERT_ORDERED_LIST_COMMAND, "Numbered List", "format_list_numbered")

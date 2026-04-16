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
)

object ListModules:
  val BULLET = new ListModule("bullet", LexicalList.INSERT_UNORDERED_LIST_COMMAND, "Bulleted List", "format_list_bulleted")
  val NUMBERED = new ListModule("number", LexicalList.INSERT_ORDERED_LIST_COMMAND, "Numbered List", "format_list_numbered")

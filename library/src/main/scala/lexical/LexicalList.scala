package lexical

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@JSImport("@lexical/list", JSImport.Namespace)
@js.native
object LexicalList extends js.Object:
  def ListNode: js.Dynamic = js.native
  def ListItemNode: js.Dynamic = js.native
  def INSERT_UNORDERED_LIST_COMMAND: LexicalCommand[Unit] = js.native
  def INSERT_ORDERED_LIST_COMMAND: LexicalCommand[Unit] = js.native
  def REMOVE_LIST_COMMAND: LexicalCommand[Unit] = js.native
  def $createListNode(listType: String): ElementNode = js.native
  def $createListItemNode(): ElementNode = js.native
  def registerList(editor: LexicalEditor): js.Function0[Unit] = js.native

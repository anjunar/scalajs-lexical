package lexical

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

@js.native
trait LexicalCommand[P] extends js.Object:
  var `type`: String = js.native

@js.native
@JSGlobal("LexicalCommands")
object LexicalCommands extends js.Object:
  val COMMAND_PRIORITY_CRITICAL: Int = js.native
  val COMMAND_PRIORITY_EDITOR: Int = js.native
  val COMMAND_PRIORITY_HIGH: Int = js.native
  val COMMAND_PRIORITY_LOW: Int = js.native
  val COMMAND_PRIORITY_NORMAL: Int = js.native
  val KEY_ENTER_COMMAND: LexicalCommand[Unit] = js.native
  val KEY_BACKSPACE_COMMAND: LexicalCommand[Unit] = js.native
  val KEY_DELETE_COMMAND: LexicalCommand[Unit] = js.native
  val KEY_ESCAPE_COMMAND: LexicalCommand[Unit] = js.native
  val KEY_TAB_COMMAND: LexicalCommand[Unit] = js.native
  val INSERT_TEXT_COMMAND: LexicalCommand[String] = js.native
  val FORMAT_TEXT_COMMAND: LexicalCommand[String] = js.native
  val UNDO_COMMAND: LexicalCommand[Unit] = js.native
  val REDO_COMMAND: LexicalCommand[Unit] = js.native
  val CLEAR_EDITOR_COMMAND: LexicalCommand[Unit] = js.native
  val CLEAR_HISTORY_COMMAND: LexicalCommand[Unit] = js.native
  val SELECT_ALL_COMMAND: LexicalCommand[Unit] = js.native
  val COPY_COMMAND: LexicalCommand[Unit] = js.native
  val CUT_COMMAND: LexicalCommand[Unit] = js.native
  val PASTE_COMMAND: LexicalCommand[Unit] = js.native
  val SELECTION_CHANGE_COMMAND: LexicalCommand[Unit] = js.native

def createCommand[P](): LexicalCommand[P] =
  js.Dynamic.newInstance(js.Dynamic.global.selectDynamic("createCommand").asInstanceOf[js.Dynamic])()
    .asInstanceOf[LexicalCommand[P]]
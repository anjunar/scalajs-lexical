package lexical

import scala.scalajs.js

object LinkCommands:
  trait LinkDialogPayload extends js.Object:
    var currentUrl: String
    var selection: BaseSelection | Null

  val OPEN_LINK_DIALOG_COMMAND: LexicalCommand[LinkDialogPayload] =
    Lexical.createCommand[LinkDialogPayload]("OPEN_LINK_DIALOG_COMMAND")

  trait LinkInsertPayload extends js.Object:
    var url: String | Null
    var selection: BaseSelection | Null

  val INSERT_LINK_COMMAND: LexicalCommand[LinkInsertPayload] =
    Lexical.createCommand[LinkInsertPayload]("INSERT_LINK_COMMAND")

package lexical

object LinkCommands:
  val OPEN_LINK_DIALOG_COMMAND: LexicalCommand[LexicalEditor] =
    Lexical.createCommand[LexicalEditor]("OPEN_LINK_DIALOG_COMMAND")

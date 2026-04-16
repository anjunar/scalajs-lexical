package lexical

import scala.scalajs.js

class HistoryModule(val historyState: HistoryState) extends EditorModule:
  override def name: String = "History"
  override def metadata: ToolbarMetadata = new ToolbarMetadata:
    val tabName = "Home"
    val sectionName = "Actions"
    val order = 3
  
  override def execute(editor: LexicalEditor): Unit = {}

  override def register(editor: LexicalEditor): js.Function0[Unit] =
    LexicalHistory.registerHistory(editor, historyState, 300)

class UndoModule extends CommandModule[Unit](
  "Undo",
  Lexical.UNDO_COMMAND,
  (),
  Some("undo"),
  metadata = new ToolbarMetadata {
    val tabName = "Home"
    val sectionName = "Actions"
    val order = 1
  }
)

class RedoModule extends CommandModule[Unit](
  "Redo",
  Lexical.REDO_COMMAND,
  (),
  Some("redo"),
  metadata = new ToolbarMetadata {
    val tabName = "Home"
    val sectionName = "Actions"
    val order = 2
  }
)

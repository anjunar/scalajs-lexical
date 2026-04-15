package lexical

object EditorModules:
  val BOLD = new TextFormatModule("bold", 1, Some("format_bold"), Some("Control+B"))
  val ITALIC = new TextFormatModule("italic", 2, Some("format_italic"), Some("Control+I"))
  
  class ClearEditorModule extends CommandModule[Unit]("Clear", Lexical.CLEAR_EDITOR_COMMAND, (), Some("delete_sweep"))
  val CLEAR = new ClearEditorModule()

package lexical

object EditorModules:
  val DEFAULT_METADATA = new ToolbarMetadata:
    val tabName = "Home"
    val sectionName = "Formatting"
    val order = 1

  val BOLD = new TextFormatModule("bold", 1, Some("format_bold"), Some("Control+B"), DEFAULT_METADATA)
  val ITALIC = new TextFormatModule("italic", 2, Some("format_italic"), Some("Control+I"), DEFAULT_METADATA)
  
  class ClearEditorModule extends CommandModule[Unit](
    "Clear", 
    Lexical.CLEAR_EDITOR_COMMAND, 
    (), 
    Some("delete_sweep"), 
    metadata = new ToolbarMetadata {
      val tabName = "Home"
      val sectionName = "Actions"
      val order = 1
    }
  )
  val CLEAR = new ClearEditorModule()

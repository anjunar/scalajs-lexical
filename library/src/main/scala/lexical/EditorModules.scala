package lexical

object EditorModules:
  val DEFAULT_METADATA = new ToolbarMetadata:
    val tabName = "Home"
    val sectionName = "Formatting"
    val order = 1

  val BOLD = new TextFormatModule(LexicalConstants.IS_BOLD, 1, Some("format_bold"), Some("Control+B"), DEFAULT_METADATA)
  val ITALIC = new TextFormatModule(LexicalConstants.IS_ITALIC, 2, Some("format_italic"), Some("Control+I"), DEFAULT_METADATA)
  val UNDERLINE = new TextFormatModule(LexicalConstants.IS_UNDERLINE, 4, Some("format_underlined"), Some("Control+U"), DEFAULT_METADATA)
  val STRIKETHROUGH = new TextFormatModule(LexicalConstants.IS_STRIKETHROUGH, 8, Some("format_strikethrough"), None, DEFAULT_METADATA)
  
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

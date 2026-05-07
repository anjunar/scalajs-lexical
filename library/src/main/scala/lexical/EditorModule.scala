package lexical

import org.scalajs.dom.KeyboardEvent

import scala.scalajs.js

trait ToolbarMetadata:
  def tabName: String
  def sectionName: String
  def order: Int

trait EditorModule extends ToolbarElement:
  def name: String
  def iconName: Option[String] = None
  def metadata: ToolbarMetadata
  
  def canActivate(editor: LexicalEditor): Boolean = true
  def isActive(editor: LexicalEditor): Boolean = false
  
  def execute(editor: LexicalEditor): Unit
  
  def keyBinding: Option[String] = None
  
  def register(editor: LexicalEditor): js.Function0[Unit] =
    keyBinding match
      case Some(binding) =>
        editor.registerCommand(
          Lexical.KEY_DOWN_COMMAND,
          (event: KeyboardEvent, _: LexicalEditor) => {
            val parts = binding.split("\\+")
            val key = parts.last.toUpperCase()
            val ctrl = parts.contains("Control") || parts.contains("Ctrl")
            val shift = parts.contains("Shift")
            val alt = parts.contains("Alt")
            val meta = parts.contains("Meta")

            if (event.key.toUpperCase() == key &&
                event.ctrlKey == ctrl &&
                event.shiftKey == shift &&
                event.altKey == alt &&
                event.metaKey == meta) {
              event.preventDefault()
              execute(editor)
              true
            } else {
              false
            }
          },
          COMMAND_PRIORITY.EDITOR
        )
      case None =>
        () => ()


  override def equals(other: Any): Boolean = other match
    case that: EditorModule => this.name == that.name
    case _ => false

  override def hashCode(): Int = name.hashCode()

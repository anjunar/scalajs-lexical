package lexical

import scala.scalajs.js
import scala.scalajs.js.annotation.*
import org.scalajs.dom

trait EditorModule:
  def name: String
  def iconName: Option[String] = None
  
  def canActivate(editor: LexicalEditor): Boolean = true
  def isActive(editor: LexicalEditor): Boolean = false
  
  def execute(editor: LexicalEditor): Unit
  
  def keyBinding: Option[String] = None
  
  def register(editor: LexicalEditor): js.Function0[Unit] = () => ()

abstract class CommandModule[P](
    override val name: String,
    val command: LexicalCommand[P],
    val payload: P,
    override val iconName: Option[String] = None,
    override val keyBinding: Option[String] = None
) extends EditorModule:
  override def execute(editor: LexicalEditor): Unit =
    editor.dispatchCommand(command, payload)

class TextFormatModule(
    val formatType: String,
    val bitmask: Int,
    override val iconName: Option[String] = None,
    override val keyBinding: Option[String] = None
) extends EditorModule:
  override def name: String = formatType.capitalize

  override def isActive(editor: LexicalEditor): Boolean =
    editor.getEditorState().read(() => {
      val selection = Lexical.$getSelection()
      selection match
        case s: RangeSelection => (s.format & bitmask) != 0
        case _ => false
    }).asInstanceOf[Boolean]

  override def execute(editor: LexicalEditor): Unit =
    editor.dispatchCommand(Lexical.FORMAT_TEXT_COMMAND, formatType)

object EditorModules:
  val BOLD = new TextFormatModule("bold", 1, Some("format_bold"), Some("Control+B"))
  val ITALIC = new TextFormatModule("italic", 2, Some("format_italic"), Some("Control+I"))
  
  class ClearEditorModule extends CommandModule[Unit]("Clear", Lexical.CLEAR_EDITOR_COMMAND, (), Some("delete_sweep"))
  val CLEAR = new ClearEditorModule()

class ToolbarManager(editor: LexicalEditor, modules: js.Array[EditorModule]):
  def createToolbar(container: dom.HTMLElement): Unit =
    modules.foreach { module =>
      val btn = dom.document.createElement("button").asInstanceOf[dom.HTMLButtonElement]
      
      module.iconName match
        case Some(icon) =>
          val iconSpan = dom.document.createElement("span").asInstanceOf[dom.HTMLElement]
          iconSpan.className = "material-icons"
          iconSpan.textContent = icon
          btn.appendChild(iconSpan)
        case None =>
          btn.textContent = module.name

      btn.title = module.name + module.keyBinding.map(k => s" ($k)").getOrElse("")
      
      btn.onclick = (_: dom.MouseEvent) => module.execute(editor)
      
      // Update button state on editor changes
      editor.registerUpdateListener((_ : js.Dynamic) => {
        btn.classList.toggle("active", module.isActive(editor))
        btn.disabled = !module.canActivate(editor)
      })
      
      container.appendChild(btn)
    }

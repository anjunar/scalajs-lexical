package lexical

import scala.scalajs.js
import org.scalajs.dom

class ToolbarManager(editor: LexicalEditor, elements: js.Array[ToolbarElement]):
  private val updateCallbacks = js.Array[() => Unit]()

  def createToolbar(container: dom.HTMLElement): Unit =
    elements.foreach(renderElement(_, container))
    
    // Only update on state changes, NOT on every selection change command
    // to avoid interfering with mouse dragging
    editor.registerUpdateListener((_ : js.Dynamic) => {
      updateCallbacks.foreach(_())
    })

  private def renderElement(element: ToolbarElement, parent: dom.HTMLElement): Unit =
    element match
      case module: EditorModule =>
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
        
        val updateButton = () => {
          btn.classList.toggle("active", module.isActive(editor))
          btn.disabled = !module.canActivate(editor)
        }
        
        updateCallbacks.push(updateButton)
        parent.appendChild(btn)

      case ToolbarGroup(children @ _*) =>
        val groupDiv = dom.document.createElement("div").asInstanceOf[dom.HTMLElement]
        groupDiv.className = "toolbar-group"
        children.foreach(renderElement(_, groupDiv))
        parent.appendChild(groupDiv)

      case ToolbarSeparator() =>
        val sep = dom.document.createElement("div").asInstanceOf[dom.HTMLElement]
        sep.className = "toolbar-separator"
        parent.appendChild(sep)

      case _ => // Should not happen

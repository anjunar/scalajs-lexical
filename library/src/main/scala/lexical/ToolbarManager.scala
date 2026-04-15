package lexical

import scala.scalajs.js
import org.scalajs.dom

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

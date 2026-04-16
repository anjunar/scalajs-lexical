package lexical

import org.scalajs.dom.{HTMLElement, document}

class FloatingToolbarRenderer extends ToolbarRenderer:
  def render(model: ToolbarModel, editor: LexicalEditor): HTMLElement =
    val container = document.createElement("div").asInstanceOf[HTMLElement]
    container.className = "lexical-floating-toolbar"
    
    // Flatten all sections into a single row of buttons
    model.tabs.foreach { tab =>
      tab.sections.foreach { section =>
        section.modules.foreach { mod =>
          val btn = document.createElement("button").asInstanceOf[org.scalajs.dom.HTMLButtonElement]
          btn.className = "lexical-floating-button"
          mod.iconName.foreach { icon =>
            val iconSpan = document.createElement("span").asInstanceOf[HTMLElement]
            iconSpan.className = "material-icons"
            iconSpan.textContent = icon
            btn.appendChild(iconSpan)
          }
          btn.title = mod.name
          btn.onclick = (_: org.scalajs.dom.MouseEvent) => mod.execute(editor)
          
          val updateButton = () => {
            editor.read(() => {
              btn.classList.toggle("active", mod.isActive(editor))
              btn.disabled = !mod.canActivate(editor)
            })
          }
          editor.registerUpdateListener(_ => updateButton())
          updateButton()
          
          container.appendChild(btn)
        }
      }
    }
    container

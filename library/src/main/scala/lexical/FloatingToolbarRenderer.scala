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
          mod match
            case em: EditorModule =>
              val btn = document.createElement("button").asInstanceOf[org.scalajs.dom.HTMLButtonElement]
              btn.className = "lexical-floating-button"
              em.iconName.foreach { icon =>
                val iconSpan = document.createElement("span").asInstanceOf[HTMLElement]
                iconSpan.className = "material-icons"
                iconSpan.textContent = icon
                btn.appendChild(iconSpan)
              }
              btn.title = em.name
              btn.onmousedown = (event: org.scalajs.dom.MouseEvent) => {
                event.preventDefault()
              }
              btn.onclick = (_: org.scalajs.dom.MouseEvent) => em.execute(editor)

              ToolbarRefresh.bindActiveState(btn, editor, em)

              container.appendChild(btn)
            case _ => // Ignore dropdowns in floating toolbar for now
        }
      }
    }
    container

package lexical

import org.scalajs.dom.{HTMLElement, document}

trait ToolbarRenderer:
  def render(model: ToolbarModel, editor: LexicalEditor): HTMLElement

class RibbonRenderer extends ToolbarRenderer:
  def render(model: ToolbarModel, editor: LexicalEditor): HTMLElement =
    val container = document.createElement("div").asInstanceOf[HTMLElement]
    container.className = "lexical-ribbon"
    
    model.tabs.foreach { tab =>
      val tabEl = document.createElement("div").asInstanceOf[HTMLElement]
      tabEl.className = "lexical-ribbon-tab"
      tabEl.textContent = tab.name
      container.appendChild(tabEl)
      
      tab.sections.foreach { section =>
        val sectionEl = document.createElement("div").asInstanceOf[HTMLElement]
        sectionEl.className = "lexical-ribbon-section"
        
        val buttonsContainer = document.createElement("div").asInstanceOf[HTMLElement]
        buttonsContainer.style.display = "flex"
        buttonsContainer.style.setProperty("gap", "5px")
        
        section.modules.foreach { mod =>
          val btn = document.createElement("button").asInstanceOf[org.scalajs.dom.HTMLButtonElement]
          btn.className = "lexical-ribbon-button"
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
          
          buttonsContainer.appendChild(btn)
        }
        sectionEl.appendChild(buttonsContainer)
        
        val labelEl = document.createElement("div").asInstanceOf[HTMLElement]
        labelEl.className = "lexical-ribbon-section-label"
        labelEl.textContent = section.name
        sectionEl.appendChild(labelEl)
        
        container.appendChild(sectionEl)
      }
    }
    container

class MenuRenderer extends ToolbarRenderer:
  def render(model: ToolbarModel, editor: LexicalEditor): HTMLElement =
    val container = document.createElement("div").asInstanceOf[HTMLElement]
    container.className = "lexical-menu"
    // Rendering logic for menu-based structure...
    container

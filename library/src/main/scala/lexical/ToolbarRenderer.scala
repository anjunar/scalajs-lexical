package lexical

import org.scalajs.dom
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
        buttonsContainer.className = "lexical-ribbon-buttons-container"
        buttonsContainer.style.display = "flex"
        buttonsContainer.style.setProperty("gap", "2px")
        
        section.modules.foreach { 
          case dropdown: ToolbarDropdown =>
            val wrapper = document.createElement("div").asInstanceOf[HTMLElement]
            wrapper.className = "lexical-dropdown-wrapper"
            
            val trigger = document.createElement("button").asInstanceOf[dom.HTMLButtonElement]
            trigger.className = "lexical-ribbon-button"
            trigger.textContent = dropdown.name
            val menu = document.createElement("div").asInstanceOf[HTMLElement]
            menu.className = "lexical-dropdown-menu"
            menu.style.display = "none"
            
            dropdown.options.foreach { opt =>
              val item = document.createElement("div").asInstanceOf[HTMLElement]
              item.className = "lexical-dropdown-item"
              item.textContent = opt.label
              item.onclick = (_: dom.MouseEvent) => {
                dropdown.onSelect(editor, opt.value)
                menu.style.display = "none"
              }
              menu.appendChild(item)
            }
            
            trigger.onclick = (_: dom.MouseEvent) => {
              menu.style.display = if (menu.style.display == "none") "block" else "none"
            }
            
            wrapper.appendChild(trigger)
            wrapper.appendChild(menu)
            buttonsContainer.appendChild(wrapper)
          
          case em: EditorModule =>
            val btn = document.createElement("button").asInstanceOf[org.scalajs.dom.HTMLButtonElement]
            btn.className = "lexical-ribbon-button"
            em.iconName.foreach { icon =>
              val iconSpan = document.createElement("span").asInstanceOf[HTMLElement]
              iconSpan.className = "material-icons"
              iconSpan.textContent = icon
              btn.appendChild(iconSpan)
            }
            btn.title = em.name
            btn.onclick = (_: org.scalajs.dom.MouseEvent) => em.execute(editor)
            
            val updateButton = () => {
              editor.read(() => {
                btn.classList.toggle("active", em.isActive(editor))
                btn.disabled = !em.canActivate(editor)
              })
            }
            editor.registerUpdateListener(_ => updateButton())
            updateButton()
            
            buttonsContainer.appendChild(btn)
          case _ =>
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

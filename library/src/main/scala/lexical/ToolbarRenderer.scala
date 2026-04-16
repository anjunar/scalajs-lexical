package lexical

import scala.scalajs.js
import org.scalajs.dom
import org.scalajs.dom.{HTMLElement, document}

trait ToolbarRenderer:
  def render(model: ToolbarModel, editor: LexicalEditor): HTMLElement

class RibbonRenderer extends ToolbarRenderer:
  def render(model: ToolbarModel, editor: LexicalEditor): HTMLElement =
    val container = document.createElement("div").asInstanceOf[HTMLElement]
    container.className = "lexical-ribbon-wrapper"

    val tabsHeader = document.createElement("div").asInstanceOf[HTMLElement]
    tabsHeader.className = "lexical-ribbon-tabs"
    
    val sectionsContainer = document.createElement("div").asInstanceOf[HTMLElement]
    sectionsContainer.className = "lexical-ribbon-content"

    var activeTab: Option[HTMLElement] = None
    var activeContent: Option[HTMLElement] = None

    model.tabs.zipWithIndex.foreach { case (tab, index) =>
      val tabEl = document.createElement("div").asInstanceOf[HTMLElement]
      tabEl.className = "lexical-ribbon-tab"
      tabEl.textContent = tab.name
      tabsHeader.appendChild(tabEl)

      val contentEl = document.createElement("div").asInstanceOf[HTMLElement]
      contentEl.className = "lexical-ribbon-tab-content"
      contentEl.style.display = "none"

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
            wrapper.style.position = "relative"
            
            val trigger = document.createElement("button").asInstanceOf[dom.HTMLButtonElement]
            trigger.className = "lexical-ribbon-button"
            trigger.asInstanceOf[js.Dynamic].updateDynamic("type")("button")

            val updateTriggerLabel = () => {
              val selectedValue = editor.read(() => dropdown.getSelectedValue(editor))
              val selectedLabel =
                dropdown.options.find(_.value == selectedValue).map(_.label).getOrElse(selectedValue)
              trigger.textContent = s"${dropdown.name}: $selectedLabel"
              trigger.title = s"${dropdown.name} - $selectedLabel"
            }
            updateTriggerLabel()
            editor.registerUpdateListener(_ => updateTriggerLabel())
            
            val menu = document.createElement("div").asInstanceOf[HTMLElement]
            menu.className = "lexical-dropdown-menu"
            menu.style.display = "none"
            menu.style.position = "absolute"

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
              val rect = trigger.getBoundingClientRect()
              menu.style.position = "fixed"
              menu.style.left = rect.left + "px"
              menu.style.top = rect.bottom + "px"
              menu.style.display = if (menu.style.display == "none") "block" else "none"
            }
            wrapper.appendChild(trigger)
            wrapper.appendChild(menu)
            buttonsContainer.appendChild(wrapper)
          case em: EditorModule =>
            val btn = document.createElement("button").asInstanceOf[org.scalajs.dom.HTMLButtonElement]
            btn.className = "lexical-ribbon-button"
            btn.asInstanceOf[js.Dynamic].updateDynamic("type")("button")
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
        labelEl.onclick = (_: dom.MouseEvent) => {
          val isCollapsed = sectionEl.classList.contains("collapsed")
          if (isCollapsed) {
            sectionEl.classList.remove("collapsed")
            buttonsContainer.style.display = "flex"
          } else {
            sectionEl.classList.add("collapsed")
            buttonsContainer.style.display = "none"
          }
        }
        sectionEl.appendChild(labelEl)
        contentEl.appendChild(sectionEl)
      }

      tabEl.onclick = (_: dom.MouseEvent) => {
        activeTab.foreach(_.classList.remove("active"))
        activeContent.foreach(_.style.display = "none")
        tabEl.classList.add("active")
        contentEl.style.display = "flex"
        activeTab = Some(tabEl)
        activeContent = Some(contentEl)
      }

      if (index == 0) {
        tabEl.classList.add("active")
        contentEl.style.display = "flex"
        activeTab = Some(tabEl)
        activeContent = Some(contentEl)
      }
      sectionsContainer.appendChild(contentEl)
    }

    container.appendChild(tabsHeader)
    container.appendChild(sectionsContainer)
    container

class MenuRenderer extends ToolbarRenderer:
  def render(model: ToolbarModel, editor: LexicalEditor): HTMLElement =
    val container = document.createElement("div").asInstanceOf[HTMLElement]
    container.className = "lexical-menu"
    // Rendering logic for menu-based structure...
    container

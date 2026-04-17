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

    val tabsHeader = createTabsHeader()
    val sectionsContainer = document.createElement("div").asInstanceOf[HTMLElement]
    sectionsContainer.className = "lexical-ribbon-content"

    var activeTab: Option[HTMLElement] = None
    var activeContent: Option[HTMLElement] = None

    model.tabs.zipWithIndex.foreach { case (tab, index) =>
      val tabContent = createTabContent(tab, editor)

      tabContent.tabEl.onclick = (_: dom.MouseEvent) => {
        activeTab.foreach(_.classList.remove("active"))
        activeContent.foreach(_.style.display = "none")
        tabContent.tabEl.classList.add("active")
        tabContent.contentEl.style.display = "flex"
        activeTab = Some(tabContent.tabEl)
        activeContent = Some(tabContent.contentEl)
      }

      if (index == 0) {
        tabContent.tabEl.classList.add("active")
        tabContent.contentEl.style.display = "flex"
        activeTab = Some(tabContent.tabEl)
        activeContent = Some(tabContent.contentEl)
      }

      tabsHeader.appendChild(tabContent.tabEl)
      sectionsContainer.appendChild(tabContent.contentEl)
    }

    container.appendChild(tabsHeader)
    container.appendChild(sectionsContainer)
    container

  private def createTabsHeader(): HTMLElement =
    val tabsHeader = document.createElement("div").asInstanceOf[HTMLElement]
    tabsHeader.className = "lexical-ribbon-tabs"
    tabsHeader

  private case class TabContent(tabEl: HTMLElement, contentEl: HTMLElement)

  private def createTabContent(tab: ToolbarTab, editor: LexicalEditor): TabContent =
    val tabEl = document.createElement("div").asInstanceOf[HTMLElement]
    tabEl.className = "lexical-ribbon-tab"
    tabEl.textContent = tab.name

    val contentEl = document.createElement("div").asInstanceOf[HTMLElement]
    contentEl.className = "lexical-ribbon-tab-content"
    contentEl.style.display = "none"

    tab.sections.foreach { section =>
      contentEl.appendChild(createSection(section, editor))
    }

    TabContent(tabEl, contentEl)

  private def createSection(section: ToolbarSection, editor: LexicalEditor): HTMLElement =
    val sectionEl = document.createElement("div").asInstanceOf[HTMLElement]
    sectionEl.className = "lexical-ribbon-section"

    val buttonsContainer = document.createElement("div").asInstanceOf[HTMLElement]
    buttonsContainer.className = "lexical-ribbon-buttons-container"
    buttonsContainer.style.display = "flex"
    buttonsContainer.style.setProperty("gap", "2px")

    section.modules.foreach {
      case dropdown: ToolbarDropdown =>
        buttonsContainer.appendChild(createDropdown(dropdown, editor))
      case em: EditorModule =>
        buttonsContainer.appendChild(createModuleButton(em, editor))
      case _ =>
    }

    sectionEl.appendChild(buttonsContainer)
    sectionEl.appendChild(createSectionLabel(section, buttonsContainer, sectionEl))
    sectionEl

  private def createSectionLabel(
      section: ToolbarSection,
      buttonsContainer: HTMLElement,
      sectionEl: HTMLElement
  ): HTMLElement =
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
    labelEl

  private def createModuleButton(module: EditorModule, editor: LexicalEditor): dom.HTMLButtonElement =
    val btn = document.createElement("button").asInstanceOf[dom.HTMLButtonElement]
    btn.className = "lexical-ribbon-button"
    btn.asInstanceOf[js.Dynamic].updateDynamic("type")("button")
    module.iconName.foreach { icon =>
      val iconSpan = document.createElement("span").asInstanceOf[HTMLElement]
      iconSpan.className = "material-icons"
      iconSpan.textContent = icon
      btn.appendChild(iconSpan)
    }
    btn.title = module.name
    btn.onclick = (_: dom.MouseEvent) => module.execute(editor)
    ToolbarRefresh.bindActiveState(btn, editor, module)
    btn

  private def createDropdown(dropdown: ToolbarDropdown, editor: LexicalEditor): HTMLElement =
    val wrapper = document.createElement("div").asInstanceOf[HTMLElement]
    wrapper.className = "lexical-dropdown-wrapper"
    wrapper.style.position = "relative"

    val trigger = document.createElement("button").asInstanceOf[dom.HTMLButtonElement]
    trigger.className = "lexical-ribbon-button"
    trigger.asInstanceOf[js.Dynamic].updateDynamic("type")("button")
    ToolbarRefresh.bindDropdownLabel(trigger, editor, dropdown)

    val menu = document.createElement("div").asInstanceOf[HTMLElement]
    menu.className = "lexical-dropdown-menu"
    menu.style.display = "none"
    menu.style.position = "absolute"

    dropdown.options.foreach { opt =>
      menu.appendChild(createDropdownItem(dropdown, editor, menu, opt))
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
    wrapper

  private def createDropdownItem(
      dropdown: ToolbarDropdown,
      editor: LexicalEditor,
      menu: HTMLElement,
      opt: ToolbarDropdownOption
  ): HTMLElement =
    val item = document.createElement("div").asInstanceOf[HTMLElement]
    item.className = "lexical-dropdown-item"
    item.textContent = opt.label
    item.onclick = (_: dom.MouseEvent) => {
      dropdown.onSelect(editor, opt.value)
      menu.style.display = "none"
    }
    item

class MenuRenderer extends ToolbarRenderer:
  def render(model: ToolbarModel, editor: LexicalEditor): HTMLElement =
    val container = document.createElement("div").asInstanceOf[HTMLElement]
    container.className = "lexical-menu"
    // Rendering logic for menu-based structure...
    container

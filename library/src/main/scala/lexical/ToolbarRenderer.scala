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
    btn.onmousedown = (event: dom.MouseEvent) => {
      event.preventDefault()
    }
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
    trigger.onmousedown = (event: dom.MouseEvent) => {
      event.preventDefault()
    }
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

    val menuBar = document.createElement("div").asInstanceOf[HTMLElement]
    menuBar.className = "lexical-menu-bar"

    var activeMenu: Option[HTMLElement] = None
    var activeTrigger: Option[HTMLElement] = None

    def closeMenus(): Unit =
      activeMenu.foreach(_.style.display = "none")
      activeTrigger.foreach(_.classList.remove("active"))
      activeMenu = None
      activeTrigger = None

    model.tabs.foreach { tab =>
      val menuItem = createMenuItem(tab, editor, closeMenus)

      menuItem.trigger.onclick = (event: dom.MouseEvent) => {
        event.preventDefault()
        event.stopPropagation()

        val isOpen = activeMenu.contains(menuItem.menu)
        closeMenus()

        if (!isOpen) {
          val rect = menuItem.trigger.getBoundingClientRect()
          menuItem.menu.style.position = "fixed"
          menuItem.menu.style.left = rect.left + "px"
          menuItem.menu.style.top = rect.bottom + "px"
          menuItem.menu.style.display = "block"
          menuItem.trigger.classList.add("active")
          activeMenu = Some(menuItem.menu)
          activeTrigger = Some(menuItem.trigger)
        }
      }

      menuBar.appendChild(menuItem.wrapper)
    }

    document.addEventListener(
      "click",
      (_: dom.MouseEvent) => closeMenus()
    )

    container.appendChild(menuBar)
    container

  private case class MenuItem(
      wrapper: HTMLElement,
      trigger: dom.HTMLButtonElement,
      menu: HTMLElement
  )

  private def createMenuItem(
      tab: ToolbarTab,
      editor: LexicalEditor,
      closeMenus: () => Unit
  ): MenuItem =
    val wrapper = document.createElement("div").asInstanceOf[HTMLElement]
    wrapper.className = "lexical-menu-item"
    wrapper.style.position = "relative"

    val trigger = document.createElement("button").asInstanceOf[dom.HTMLButtonElement]
    trigger.className = "lexical-menu-trigger lexical-ribbon-button"
    trigger.asInstanceOf[js.Dynamic].updateDynamic("type")("button")
    trigger.textContent = tab.name
    trigger.onmousedown = (event: dom.MouseEvent) => {
      event.preventDefault()
    }

    val menu = document.createElement("div").asInstanceOf[HTMLElement]
    menu.className = "lexical-menu-panel lexical-dropdown-menu"
    menu.style.display = "none"

    val showSectionLabels =
      !(tab.sections.length == 1 && tab.sections.headOption.exists(_.name == tab.name))

    tab.sections.foreach { section =>
      menu.appendChild(createMenuSection(section, editor, closeMenus, showSectionLabels))
    }

    wrapper.appendChild(trigger)
    wrapper.appendChild(menu)
    MenuItem(wrapper, trigger, menu)

  private def createMenuSection(
      section: ToolbarSection,
      editor: LexicalEditor,
      closeMenus: () => Unit,
      showLabel: Boolean
  ): HTMLElement =
    val sectionEl = document.createElement("div").asInstanceOf[HTMLElement]
    sectionEl.className = "lexical-menu-section"

    if (showLabel) {
      val labelEl = document.createElement("div").asInstanceOf[HTMLElement]
      labelEl.className = "lexical-menu-section-label"
      labelEl.textContent = section.name
      sectionEl.appendChild(labelEl)
    }

    val contentEl = document.createElement("div").asInstanceOf[HTMLElement]
    contentEl.className = "lexical-menu-section-content"
    contentEl.style.display = "flex"
    contentEl.style.setProperty("flex-wrap", "wrap")
    contentEl.style.setProperty("gap", "4px")

    section.modules.foreach {
      case dropdown: ToolbarDropdown =>
        contentEl.appendChild(createMenuDropdown(dropdown, editor, closeMenus))
      case em: EditorModule =>
        contentEl.appendChild(createMenuModuleButton(em, editor, closeMenus))
      case _ =>
    }

    sectionEl.appendChild(contentEl)
    sectionEl

  private def createMenuModuleButton(
      module: EditorModule,
      editor: LexicalEditor,
      closeMenus: () => Unit
  ): dom.HTMLButtonElement =
    val btn = document.createElement("button").asInstanceOf[dom.HTMLButtonElement]
    btn.className = "lexical-menu-button lexical-ribbon-button"
    btn.asInstanceOf[js.Dynamic].updateDynamic("type")("button")
    btn.onmousedown = (event: dom.MouseEvent) => {
      event.preventDefault()
    }
    module.iconName.foreach { icon =>
      val iconSpan = document.createElement("span").asInstanceOf[HTMLElement]
      iconSpan.className = "material-icons"
      iconSpan.textContent = icon
      btn.appendChild(iconSpan)
    }
    if (module.iconName.isEmpty) {
      btn.textContent = module.name
    }
    btn.title = module.name
    btn.onclick = (_: dom.MouseEvent) => {
      module.execute(editor)
      closeMenus()
    }
    ToolbarRefresh.bindActiveState(btn, editor, module)
    btn

  private def createMenuDropdown(
      dropdown: ToolbarDropdown,
      editor: LexicalEditor,
      closeMenus: () => Unit
  ): HTMLElement =
    val wrapper = document.createElement("div").asInstanceOf[HTMLElement]
    wrapper.className = "lexical-dropdown-wrapper lexical-menu-dropdown"
    wrapper.style.position = "relative"

    val trigger = document.createElement("button").asInstanceOf[dom.HTMLButtonElement]
    trigger.className = "lexical-menu-button lexical-ribbon-button"
    trigger.asInstanceOf[js.Dynamic].updateDynamic("type")("button")
    trigger.onmousedown = (event: dom.MouseEvent) => {
      event.preventDefault()
    }
    ToolbarRefresh.bindDropdownLabel(trigger, editor, dropdown)

    val menu = document.createElement("div").asInstanceOf[HTMLElement]
    menu.className = "lexical-dropdown-menu lexical-menu-dropdown-panel"
    menu.style.display = "none"

    dropdown.options.foreach { opt =>
      val item = document.createElement("div").asInstanceOf[HTMLElement]
      item.className = "lexical-dropdown-item"
      item.textContent = opt.label
      item.onclick = (_: dom.MouseEvent) => {
        dropdown.onSelect(editor, opt.value)
        menu.style.display = "none"
        closeMenus()
      }
      menu.appendChild(item)
    }

    trigger.onclick = (event: dom.MouseEvent) => {
      event.preventDefault()
      event.stopPropagation()
      val rect = trigger.getBoundingClientRect()
      menu.style.position = "fixed"
      menu.style.left = rect.left + "px"
      menu.style.top = rect.bottom + "px"
      menu.style.display = if (menu.style.display == "none") "block" else "none"
    }

    wrapper.appendChild(trigger)
    wrapper.appendChild(menu)
    wrapper

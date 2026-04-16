package lexical

case class ToolbarModel(
  tabs: List[ToolbarTab]
)

case class ToolbarTab(
  name: String,
  sections: List[ToolbarSection]
)

case class ToolbarSection(
  name: String,
  modules: List[ToolbarElement]
)

class ToolbarRegistry(elements: List[ToolbarElement]):
  def getModel: ToolbarModel =
    val groupedByTab = elements.groupBy {
      case m: EditorModule => 
        org.scalajs.dom.console.log(s"Grouping EditorModule: ${m.name} to ${m.metadata.tabName}")
        m.metadata.tabName
      case d: ToolbarDropdown => 
        org.scalajs.dom.console.log(s"Grouping Dropdown: ${d.name} to Home")
        "Home"
    }

    val toolbarTabs = groupedByTab.map { case (tabName, elementsInTab) =>
      val sections = elementsInTab.groupBy {
        case m: EditorModule => m.metadata.sectionName
        case d: ToolbarDropdown => "Formatting"
      }.map { case (sectionName, elementsInSection) =>
        org.scalajs.dom.console.log(s"Creating section $sectionName with ${elementsInSection.size} elements")
        ToolbarSection(sectionName, elementsInSection)
      }.toList.sortBy(_.name)
      ToolbarTab(tabName, sections)
    }.toList.sortBy(_.name)
    
    ToolbarModel(toolbarTabs)

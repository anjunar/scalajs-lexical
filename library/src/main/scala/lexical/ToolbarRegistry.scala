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
      case m: EditorModule => m.metadata.tabName
      case _: ToolbarDropdown => "Home"
    }

    val toolbarTabs = groupedByTab.map { case (tabName, elementsInTab) =>
      val sections = elementsInTab.groupBy {
        case m: EditorModule => m.metadata.sectionName
        case d: ToolbarDropdown => "Formatting"
      }.map { case (sectionName, elementsInSection) =>
        ToolbarSection(sectionName, elementsInSection)
      }.toList.sortBy(_.name)
      ToolbarTab(tabName, sections)
    }.toList.sortBy(_.name)
    
    ToolbarModel(toolbarTabs)

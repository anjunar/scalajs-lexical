package lexical

enum ToolbarLayout:
  case Ribbon, Menu

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

class ToolbarRegistry(
    elements: List[ToolbarElement],
    layout: ToolbarLayout = ToolbarLayout.Ribbon
):
  def getModel: ToolbarModel =
    layout match
      case ToolbarLayout.Ribbon => buildRibbonModel()
      case ToolbarLayout.Menu => buildMenuModel()

  private def buildRibbonModel(): ToolbarModel =
    val groupedByTab = elements.groupBy(tabNameForRibbon)

    val toolbarTabs = groupedByTab.map { case (tabName, elementsInTab) =>
      val sections = elementsInTab.groupBy(sectionNameForRibbon).map { case (sectionName, elementsInSection) =>
        ToolbarSection(sectionName, elementsInSection)
      }.toList.sortBy(_.name)
      ToolbarTab(tabName, sections)
    }.toList.sortBy(_.name)

    ToolbarModel(toolbarTabs)

  private def buildMenuModel(): ToolbarModel =
    val groupedBySection = elements.groupBy(sectionNameForMenu)

    val menuTabs = groupedBySection.map { case (sectionName, elementsInSection) =>
      ToolbarTab(sectionName, List(ToolbarSection(sectionName, elementsInSection)))
    }.toList.sortBy(_.name)

    ToolbarModel(menuTabs)

  private def tabNameForRibbon(element: ToolbarElement): String = element match
    case m: EditorModule => m.metadata.tabName
    case _: ToolbarDropdown => "Home"

  private def sectionNameForRibbon(element: ToolbarElement): String = element match
    case m: EditorModule => m.metadata.sectionName
    case _: ToolbarDropdown => "Formatting"

  private def sectionNameForMenu(element: ToolbarElement): String = element match
    case m: EditorModule => m.metadata.sectionName
    case d: ToolbarDropdown => d.name

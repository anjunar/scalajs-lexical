package lexical

import scala.collection.mutable

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
    val tabs = mutable.LinkedHashMap.empty[String, mutable.LinkedHashMap[String, mutable.ListBuffer[ToolbarElement]]]

    elements.foreach { element =>
      val tabName = tabNameForRibbon(element)
      val sectionName = sectionNameForRibbon(element)
      val sections = tabs.getOrElseUpdate(tabName, mutable.LinkedHashMap.empty)
      val modules = sections.getOrElseUpdate(sectionName, mutable.ListBuffer.empty)
      modules += element
    }

    ToolbarModel(
      tabs.iterator.map { case (tabName, sections) =>
        ToolbarTab(
          tabName,
          sections.iterator.map { case (sectionName, modules) =>
            ToolbarSection(sectionName, modules.toList)
          }.toList
        )
      }.toList
    )

  private def buildMenuModel(): ToolbarModel =
    val menus = mutable.LinkedHashMap.empty[String, mutable.LinkedHashMap[String, mutable.ListBuffer[ToolbarElement]]]

    elements.zipWithIndex.foreach { case (element, index) =>
      val sectionName = sectionNameForMenu(index)
      val menuName = normalizeMenuName(sectionName)
      val sections = menus.getOrElseUpdate(menuName, mutable.LinkedHashMap.empty)
      val modules = sections.getOrElseUpdate(sectionName, mutable.ListBuffer.empty)
      modules += element
    }

    ToolbarModel(
      menus.iterator.map { case (menuName, sections) =>
        ToolbarTab(
          menuName,
          sections.iterator.map { case (sectionName, modules) =>
            ToolbarSection(sectionName, modules.toList)
          }.toList
        )
      }.toList
    )

  private def tabNameForRibbon(element: ToolbarElement): String = element match
    case m: EditorModule => m.metadata.tabName
    case _: ToolbarDropdown => "Home"

  private def sectionNameForRibbon(element: ToolbarElement): String = element match
    case m: EditorModule => m.metadata.sectionName
    case _: ToolbarDropdown => "Formatting"

  private def sectionNameForMenu(index: Int): String =
    val element = elements(index)
    element match
      case m: EditorModule => m.metadata.sectionName
      case _: ToolbarDropdown =>
        nearestSectionName(index).getOrElse("Formatting")

  private def normalizeMenuName(sectionName: String): String =
    sectionName match
      case "Links" | "Media" | "Table" | "Code" => "Insert"
      case other => other

  private def nearestSectionName(index: Int): Option[String] =
    nextSectionName(index).orElse(previousSectionName(index))

  private def nextSectionName(index: Int): Option[String] =
    elements.iterator
      .drop(index + 1)
      .collectFirst { case module: EditorModule => module.metadata.sectionName }

  private def previousSectionName(index: Int): Option[String] =
    elements
      .take(index)
      .reverseIterator
      .collectFirst { case module: EditorModule => module.metadata.sectionName }

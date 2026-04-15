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
  modules: List[EditorModule]
)

class ToolbarRegistry(modules: List[EditorModule]):
  def getModel: ToolbarModel =
    val grouped = modules.groupBy(_.metadata.tabName)
    val tabs = grouped.map { case (tabName, tabModules) =>
      val sections = tabModules.groupBy(_.metadata.sectionName)
        .map { case (sectionName, sectionModules) =>
          ToolbarSection(sectionName, sectionModules.sortBy(_.metadata.order))
        }.toList.sortBy(_.name)
      ToolbarTab(tabName, sections)
    }.toList.sortBy(_.name)
    
    ToolbarModel(tabs)

package lexical

import scala.scalajs.js

class ParagraphModule extends EditorModule:
  override def name: String = "Paragraph"
  override def iconName: Option[String] = Some("add")
  override def metadata: ToolbarMetadata = new ToolbarMetadata:
    val tabName = "Home"
    val sectionName = "Actions"
    val order = 2
  
  override def execute(editor: LexicalEditor): Unit =
    editor.update(() => {
      val root = Lexical.$getRoot()
      val paragraph = Lexical.$createParagraphNode()
      val text = Lexical.$createTextNode("New paragraph! ")
      paragraph.append(text)
      root.append(paragraph)
    }, js.Dynamic.literal().asInstanceOf[EditorUpdateOptions])

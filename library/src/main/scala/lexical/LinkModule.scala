package lexical

import scala.scalajs.js
import org.scalajs.dom

class LinkModule extends EditorModule:
  override def name: String = "Link"
  override def iconName: Option[String] = Some("link")
  override def keyBinding: Option[String] = Some("Control+K")
  override def metadata: ToolbarMetadata = new ToolbarMetadata:
    val tabName = "Insert"
    val sectionName = "Links"
    val order = 1

  override def isActive(editor: LexicalEditor): Boolean =
    editor.getSelectionWrapper().getNodes.exists(node => LexicalLink.$isLinkNode(node))

  override def execute(editor: LexicalEditor): Unit =
    val selection = currentSelection(editor)
    if (!editor.dispatchCommand(LinkCommands.OPEN_LINK_DIALOG_COMMAND, editor)) {
      val currentUrl = currentLinkUrl(editor)
      val url = dom.window.prompt("Enter URL:", currentUrl)
      if (url != null) {
        applyLink(editor, url, selection)
      }
    }

  def applyLink(editor: LexicalEditor, url: String | Null, selection: BaseSelection | Null): Unit =
    editor.update(() =>
      if (selection != null) {
        Lexical.$setSelection(selection.clone().asInstanceOf[RangeSelection | NodeSelection])
      }
      val normalizedUrl = Option(url).map(_.trim).filter(_.nonEmpty).orNull
      LexicalLink.$toggleLink(normalizedUrl)
    , js.Dynamic.literal().asInstanceOf[EditorUpdateOptions])

  private def currentSelection(editor: LexicalEditor): RangeSelection | Null =
    editor.read(() => Lexical.$getSelection())

  private def currentLinkUrl(editor: LexicalEditor): String =
    editor.getEditorState().read(() => {
      val nodes = editor.getSelectionWrapper().getNodes
      nodes.find(node => LexicalLink.$isLinkNode(node)).map { node =>
        node.asInstanceOf[js.Dynamic].getURL().asInstanceOf[String]
      }.getOrElse("")
    }).asInstanceOf[String]

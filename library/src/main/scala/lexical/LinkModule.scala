package lexical

import scala.scalajs.js
import org.scalajs.dom

class LinkModule extends EditorModule:
  override def name: String = "Link"
  override def iconName: Option[String] = Some("link")
  override def keyBinding: Option[String] = Some("Control+K")

  override def isActive(editor: LexicalEditor): Boolean =
    editor.getSelectionWrapper().getNodes.exists(node => LexicalLink.$isLinkNode(node))

  override def execute(editor: LexicalEditor): Unit =
    val currentUrl = editor.getEditorState().read(() => {
      val nodes = editor.getSelectionWrapper().getNodes
      nodes.find(node => LexicalLink.$isLinkNode(node)).map { node =>
         // LinkNode has getURL() in JS
         node.asInstanceOf[js.Dynamic].getURL().asInstanceOf[String]
      }.getOrElse("")
    }).asInstanceOf[String]

    val url = dom.window.prompt("Enter URL:", currentUrl)
    if (url != null) {
      val finalUrl = if (url.isEmpty) null else url
      editor.dispatchCommand(LexicalLink.TOGGLE_LINK_COMMAND, finalUrl)
    }

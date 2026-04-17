package lexical

import scala.scalajs.js

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
    editor.dispatchCommand(
      LinkCommands.OPEN_LINK_DIALOG_COMMAND,
      new LinkCommands.LinkDialogPayload:
        var currentUrl = currentLinkUrl(editor)
        var selection = currentSelection(editor)
    )

  override def register(editor: LexicalEditor): js.Function0[Unit] =
    val removeInsertLink = editor.registerCommand(
      LinkCommands.INSERT_LINK_COMMAND,
      (payload: LinkCommands.LinkInsertPayload, lexicalEditor: LexicalEditor) => {
        lexicalEditor.update(() => {
          applyLinkInCurrentUpdate(payload.url, payload.selection)
        }, js.Dynamic.literal().asInstanceOf[EditorUpdateOptions])
        true
      },
      COMMAND_PRIORITY.EDITOR
    )

    () => removeInsertLink()

  private def applyLinkInCurrentUpdate(url: String | Null, selection: BaseSelection | Null): Unit =
    if (selection != null) {
      Lexical.$setSelection(selection.clone())
    }
    val normalizedUrl = Option(url).map(_.trim).filter(_.nonEmpty).orNull
    LexicalLink.$toggleLink(normalizedUrl)

  private def currentSelection(editor: LexicalEditor): BaseSelection | Null =
    editor.read(() => {
      val selection = Lexical.$getSelection()
      if (selection != null) selection.clone() else null
    })

  private def currentLinkUrl(editor: LexicalEditor): String =
    editor.getEditorState().read(() => {
      val nodes = editor.getSelectionWrapper().getNodes
      nodes.find(node => LexicalLink.$isLinkNode(node)).map { node =>
        node.asInstanceOf[js.Dynamic].getURL().asInstanceOf[String]
      }.getOrElse("")
    }).asInstanceOf[String]

package lexical

import scala.scalajs.js
import scala.scalajs.js.annotation.*
import org.scalajs.dom

type NodeKey = String

@js.native
trait LexicalCommand[P] extends js.Object

@JSImport("lexical", JSImport.Namespace)
@js.native
object Lexical extends js.Object:
  def createEditor(config: CreateEditorArgs): LexicalEditor = js.native
  def TextNode: TextNodeStatic = js.native
  def ParagraphNode: ParagraphNodeStatic = js.native
  def RootNode: RootNodeStatic = js.native
  def ElementNode: ElementNodeStatic = js.native
  def LineBreakNode: LineBreakNodeStatic = js.native
  def DecoratorNode: DecoratorNodeStatic = js.native
  def TabNode: TabNodeStatic = js.native
  def $createTextNode(text: String): TextNode = js.native
  def $createParagraphNode(): ParagraphNode = js.native
  def $createLineBreakNode(): LineBreakNode = js.native
  def $createTabNode(): TabNode = js.native
  def $getSelection(): BaseSelection | Null = js.native
  def $getTextContent(): String = js.native
  def $getRoot(): RootNode = js.native
  def $getEditor(): LexicalEditor = js.native
  def $getNearestNodeFromDOMNode(domNode: dom.Node): LexicalNode | Null = js.native
  def getDOMSelection(target: dom.Window): dom.Selection | Null = js.native
  def $insertNodes(nodes: js.Array[LexicalNode]): Boolean = js.native
  def $createNodeSelection(): NodeSelection = js.native
  def $setSelection(selection: BaseSelection | Null): Unit = js.native
  def $isTextNode(node: LexicalNode): Boolean = js.native
  def $isParagraphNode(node: LexicalNode): Boolean = js.native
  def $isRootNode(node: LexicalNode): Boolean = js.native
  def $isElementNode(node: LexicalNode): Boolean = js.native
  def $isDecoratorNode(node: LexicalNode): Boolean = js.native
  def $isLineBreakNode(node: LexicalNode): Boolean = js.native
  def $isRangeSelection(node: Any): Boolean = js.native
  def $isRootOrShadowRoot(node: LexicalNode): Boolean = js.native
  def $findMatchingParent(node: LexicalNode, callback: js.Function1[LexicalNode, Boolean]): LexicalNode | Null = js.native
  def $getNodeByKey(key: NodeKey): LexicalNode | Null = js.native
  def KEY_ENTER_COMMAND: LexicalCommand[Unit] = js.native
  def FORMAT_TEXT_COMMAND: LexicalCommand[String] = js.native
  def DELETE_COMMAND: LexicalCommand[Unit] = js.native
  def CLEAR_EDITOR_COMMAND: LexicalCommand[Unit] = js.native
  def CLEAR_HISTORY_COMMAND: LexicalCommand[Unit] = js.native
  def UNDO_COMMAND: LexicalCommand[Unit] = js.native
  def REDO_COMMAND: LexicalCommand[Unit] = js.native
  def SELECTION_CHANGE_COMMAND: LexicalCommand[Unit] = js.native
  @JSName("createCommand")
  def createCommand[P](name: String): LexicalCommand[P] = js.native

trait ImagePayload extends js.Object:
  var src: String
  var altText: String
  var maxWidth: Int

object LexicalConstants:
  val IS_BOLD = "bold"
  val IS_ITALIC = "italic"
  val IS_UNDERLINE = "underline"
  val IS_STRIKETHROUGH = "strikethrough"
  val IS_CODE = "code"
  val IS_SUBSCRIPT = "subscript"
  val IS_SUPERSCRIPT = "superscript"
  val IS_HIGHLIGHT = "highlight"

@JSImport("@lexical/link", JSImport.Namespace)
@js.native
object LexicalLink extends js.Object:
  def LinkNode: js.Dynamic = js.native
  def TOGGLE_LINK_COMMAND: LexicalCommand[String | js.Object | Null] = js.native
  def $isLinkNode(node: LexicalNode): Boolean = js.native
  def $toggleLink(urlOrAttributes: String | js.Object | Null, attributes: js.Object | Null = null): Unit = js.native

@JSImport("@lexical/markdown", JSImport.Namespace)
@js.native
object LexicalMarkdown extends js.Object:
  def registerMarkdownShortcuts(editor: LexicalEditor, transformers: js.Array[js.Any]): js.Function0[Unit] = js.native
  def TRANSFORMERS: js.Array[js.Any] = js.native

@JSImport("@lexical/code", JSImport.Namespace)
@js.native
object LexicalCode extends js.Object:
  def CodeNode: js.Dynamic = js.native

@JSImport("@lexical/rich-text", JSImport.Namespace)
@js.native
object LexicalRichText extends js.Object:
  def registerRichText(editor: LexicalEditor): js.Function0[Unit] = js.native
  def HeadingNode: js.Dynamic = js.native
  def QuoteNode: js.Dynamic = js.native
  def FORMAT_HEADING_COMMAND: LexicalCommand[String] = js.native
  def $createHeadingNode(tag: String): ElementNode = js.native

@JSImport("@lexical/history", JSImport.Namespace)
@js.native
object LexicalHistory extends js.Object:
  def registerHistory(editor: LexicalEditor, historyState: HistoryState, delay: Int = 300): js.Function0[Unit] = js.native
  def createEmptyHistoryState(): HistoryState = js.native

@js.native
trait HistoryState extends js.Object

@JSImport("@lexical/dragon", JSImport.Namespace)
@js.native
object LexicalDragon extends js.Object:
  def registerDragonSupport(editor: LexicalEditor): js.Function0[Unit] = js.native

@JSImport("@lexical/utils", JSImport.Namespace)
@js.native
object LexicalUtils extends js.Object:
  def mergeRegister(unregisters: js.Array[js.Function0[Unit]]): js.Function0[Unit] = js.native

@js.native
trait CreateEditorArgs extends js.Object:
  var namespace: js.UndefOr[String]
  var theme: js.UndefOr[js.Object]
  var nodes: js.UndefOr[js.Array[js.Any]]
  var onError: js.UndefOr[js.Function1[js.Error, Unit]]
  var editable: js.UndefOr[Boolean]
  var disableEvents: js.UndefOr[Boolean]
  var editorState: js.UndefOr[js.Any]
  var parentEditor: js.UndefOr[LexicalEditor]
  var html: js.UndefOr[js.Object]

object COMMAND_PRIORITY:
  val EDITOR = 0
  val LOW = 1
  val NORMAL = 2
  val HIGH = 3
  val CRITICAL = 4

def createCommand[P](name: String): LexicalCommand[P] =
  js.Dynamic.literal(`type` = name).asInstanceOf[LexicalCommand[P]]

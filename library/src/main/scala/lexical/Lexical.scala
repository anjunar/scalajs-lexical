package lexical

import scala.scalajs.js
import scala.scalajs.js.annotation.*
import org.scalajs.dom

type NodeKey = String

@JSImport("lexical", JSImport.Namespace)
@js.native
object Lexical extends js.Object:
  def createEditor(config: js.Dynamic): LexicalEditor = js.native
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
  def $getSelection(): RangeSelection | Null = js.native
  def $getTextContent(): String = js.native
  def $getRoot(): RootNode = js.native
  def $getEditor(): LexicalEditor = js.native
  def $getNearestNodeFromDOMNode(domNode: dom.Node): LexicalNode | Null = js.native
  def getDOMSelection(target: dom.Window): dom.Selection | Null = js.native
  def $insertNodes(nodes: js.Array[LexicalNode]): Boolean = js.native
  def $isTextNode(node: LexicalNode): Boolean = js.native
  def $isParagraphNode(node: LexicalNode): Boolean = js.native
  def $isRootNode(node: LexicalNode): Boolean = js.native
  def $isElementNode(node: LexicalNode): Boolean = js.native
  def $isDecoratorNode(node: LexicalNode): Boolean = js.native
  def $isLineBreakNode(node: LexicalNode): Boolean = js.native
  def $isTabNode(node: LexicalNode): Boolean = js.native
  def $getNodeByKey(key: NodeKey): LexicalNode | Null = js.native
  def FORMAT_TEXT_COMMAND: js.Dynamic = js.native
  def CLEAR_EDITOR_COMMAND: js.Dynamic = js.native
  def CLEAR_HISTORY_COMMAND: js.Dynamic = js.native
  def UNDO_COMMAND: js.Dynamic = js.native
  def REDO_COMMAND: js.Dynamic = js.native

object LexicalConstants:
  val IS_BOLD = "bold"
  val IS_ITALIC = "italic"
  val IS_UNDERLINE = "underline"
  val IS_STRIKETHROUGH = "strikethrough"
  val IS_CODE = "code"
  val IS_SUBSCRIPT = "subscript"
  val IS_SUPERSCRIPT = "superscript"
  val IS_HIGHLIGHT = "highlight"

@JSImport("@lexical/rich-text", JSImport.Namespace)
@js.native
object LexicalRichText extends js.Object:
  def registerRichText(editor: LexicalEditor): Unit = js.native
  def RichTextExtension: js.Dynamic = js.native

@JSImport("@lexical/history", JSImport.Namespace)
@js.native
object LexicalHistory extends js.Object:
  def registerHistory(editor: LexicalEditor, historyState: js.Dynamic, delay: Int = 300): Unit = js.native
  def createEmptyHistoryState(): js.Dynamic = js.native

@JSImport("@lexical/dragon", JSImport.Namespace)
@js.native
object LexicalDragon extends js.Object:
  def registerDragonSupport(editor: LexicalEditor): Unit = js.native

object COMMAND_PRIORITY:
  val EDITOR = 0
  val LOW = 1
  val NORMAL = 2
  val HIGH = 3
  val CRITICAL = 4
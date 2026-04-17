package lexical

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSGlobal, JSImport}
import org.scalajs.dom as dom

@js.native
trait Point extends js.Object:
  def getKey(): NodeKey = js.native
  def getOffset(): Int = js.native
  def getType(): String = js.native
  def getNode(): LexicalNode = js.native

@js.native
trait BaseSelection extends js.Object:
  def getNodes(): js.Array[LexicalNode] = js.native
  def getCharacters(): js.Array[js.Dynamic] = js.native
  def isCollapsed(): Boolean = js.native
  def getStartKey(): NodeKey = js.native
  def getEndKey(): NodeKey = js.native
  override def clone(): BaseSelection = js.native

@js.native
trait RangeSelection extends BaseSelection:
  def anchor: Point = js.native
  def focus: Point = js.native
  def format: Int = js.native
  def style: String = js.native
  def isBackward(): Boolean = js.native
  def isFocused(): Boolean = js.native
  def getTextContent(): String = js.native
  def insertText(text: String): Unit = js.native
  def insertRawText(text: String): Unit = js.native
  def insertNodes(nodes: js.Array[LexicalNode]): Unit = js.native
  def setFormat(format: Int): Unit = js.native
  def setStyle(style: String): Unit = js.native
  def hasFormat(formatType: String): Boolean = js.native
  def forEach(callback: js.Function2[LexicalNode, Int, Unit]): Unit = js.native

@js.native
trait NodeSelection extends BaseSelection:
  override def getNodes(): js.Array[LexicalNode] = js.native
  def add(key: NodeKey): Unit = js.native
  def delete(key: NodeKey): Unit = js.native
  def clear(): Unit = js.native
  def has(key: NodeKey): Boolean = js.native
  def deleteNodes(): Unit = js.native

@js.native
@JSImport("@lexical/selection", JSImport.Namespace)
object LexicalSelection extends js.Object:
  def $createRangeSelection(): RangeSelection = js.native

  def $setBlocksType(selection: RangeSelection, createNode: js.Function0[ElementNode]): Unit = js.native

  def $createPoint(key: NodeKey, offset: Int, `type`: String): Point = js.native

  def $createNodeSelection(): NodeSelection = js.native

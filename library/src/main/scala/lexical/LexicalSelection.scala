package lexical

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal
import org.scalajs.dom as dom

@js.native
trait Point extends js.Object:
  def getKey(): NodeKey = js.native
  def getOffset(): Int = js.native
  def getType(): String = js.native

@js.native
trait BaseSelection extends js.Object:
  def getNodes(): js.Array[LexicalNode] = js.native
  def getCharacters(): js.Array[js.Dynamic] = js.native
  def isCollapsed(): Boolean = js.native
  def getStartKey(): NodeKey = js.native
  def getEndKey(): NodeKey = js.native

@js.native
trait RangeSelection extends BaseSelection:
  def anchor: Point = js.native
  def focus: Point = js.native
  def format: Int = js.native
  def isBackward(): Boolean = js.native
  def isFocused(): Boolean = js.native
  def getNativeText(): String = js.native // Keeping it for compatibility if needed, but adding the correct one
  def getTextContent(): String = js.native
  def apply(): Unit = js.native
  def detach(): Unit = js.native
  def insertNodes(nodes: js.Array[LexicalNode]): Boolean = js.native
  def format(formatType: String): Unit = js.native
  def forEach(callback: js.Function2[LexicalNode, Int, Unit]): Unit = js.native
  override def toString(): String = js.native

@js.native
trait NodeSelection extends BaseSelection:
  override def getNodes(): js.Array[LexicalNode] = js.native
  def add(key: NodeKey): Unit = js.native

@js.native
@JSGlobal("LexicalSelection")
object LexicalSelection extends js.Object:
  def $createRangeSelection(
    anchorKey: NodeKey,
    anchorOffset: Int,
    focusKey: NodeKey,
    focusOffset: Int
  ): RangeSelection = js.native

  def $createPoint(key: NodeKey, offset: Int, `type`: String): Point = js.native

  def $createNodeSelection(): NodeSelection = js.native
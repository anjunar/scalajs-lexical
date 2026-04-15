package lexical

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal
import org.scalajs.dom as dom

@js.native
trait LexicalNode extends js.Object:
  def getType(): String = js.native
  def getKey(): NodeKey = js.native
  def getParent[T](): T = js.native
  def getParentOrThrow[T](): T = js.native
  def getParentKeys(): js.Array[NodeKey] = js.native
  def getIndexWithinParent(): Int = js.native
  def getPreviousSibling[T](): T = js.native
  def getNextSibling[T](): T = js.native
  def getNextSiblings[T](): js.Array[T] = js.native
  def getPreviousSiblings[T](): js.Array[T] = js.native
  def getParents(): js.Array[ElementNode] = js.native
  def getTopLevelElement(): ElementNode | Null = js.native
  def isAttached(): Boolean = js.native
  def isSelected(selection: BaseSelection | Null): Boolean = js.native
  def isInline(): Boolean = js.native
  def isDirty(): Boolean = js.native
  def isNode(obj: LexicalNode | Null): Boolean = js.native
  def isBefore(targetNode: LexicalNode): Boolean = js.native
  def isParentOf(targetNode: LexicalNode): Boolean = js.native
  def getTextContent(): String = js.native
  def getTextContentSize(): Int = js.native
  def getLatest(): this.type = js.native
  def getWritable(): this.type = js.native
  def remove(preserveEmptyParent: Boolean): Unit = js.native
  def replace[N <: LexicalNode](replaceWith: N, includeChildren: Boolean): N = js.native
  def insertAfter(nodeToInsert: LexicalNode, restoreSelection: Boolean): LexicalNode = js.native
  def insertBefore(nodeToInsert: LexicalNode, restoreSelection: Boolean): LexicalNode = js.native
  def selectStart(): RangeSelection = js.native
  def selectEnd(): RangeSelection = js.native
  def selectPrevious(anchorOffset: Int, focusOffset: Int): RangeSelection = js.native
  def selectNext(anchorOffset: Int, focusOffset: Int): RangeSelection = js.native
  def markDirty(): Unit = js.native
  def createDOM(config: js.Dynamic, editor: LexicalEditor): dom.HTMLElement = js.native
  def updateDOM(prevNode: js.Any, domElement: dom.HTMLElement, config: js.Dynamic): Boolean = js.native
  def exportJSON(): js.Dynamic = js.native

@js.native
@JSGlobal("TextNode")
class TextNode(text: String = "", key: NodeKey = "") extends LexicalNode:
  def getText(): String = js.native
  def setText(text: String): TextNode = js.native
  def getVersion(): Int = js.native
  def toggleFormat(formatType: String): TextNode = js.native
  def format(formatType: String): TextNode = js.native
  def getFormat(): String = js.native
  def getStyle(): String = js.native
  def setStyle(style: String): TextNode = js.native
  def getDirection(): String | Null = js.native
  def setDirection(direction: String | Null): TextNode = js.native
  def select(start: Int, end: Int): RangeSelection = js.native
  def isToken(): Boolean = js.native
  def isSegmented(): Boolean = js.native
  def splitText(offset: Int): js.Array[TextNode] = js.native
  def mergeWithSibling(sibling: TextNode): TextNode = js.native

@js.native
@JSGlobal("TextNode")
object TextNode extends TextNodeStatic

@js.native
trait TextNodeStatic extends js.Object:
  def getType(): String = js.native
  def clone(node: TextNode): TextNode = js.native
  def importJSON(serialized: js.Dynamic): TextNode = js.native

@js.native
@JSGlobal("ParagraphNode")
class ParagraphNode(key: NodeKey = "") extends ElementNode(key):
  override def getChildAtIndex(index: Int): LexicalNode | Null = js.native
  override def getChildrenSize(): Int = js.native
  override def getChildren(): js.Array[LexicalNode] = js.native
  override def getFirstChild(): LexicalNode | Null = js.native
  override def getLastChild(): LexicalNode | Null = js.native
  override def append(child: LexicalNode): ParagraphNode = js.native
  override def prepend(child: LexicalNode): ParagraphNode = js.native
  override def remove(child: LexicalNode): LexicalNode = js.native
  override def getDescendantByKey(key: NodeKey): LexicalNode | Null = js.native
  override def clear(): ParagraphNode = js.native
  override def getDirection(): String | Null = js.native
  override def setDirection(direction: String | Null): ParagraphNode = js.native
  def getIndent(): Int = js.native
  def setIndent(indent: Int): ParagraphNode = js.native
  def getFormat(): String = js.native
  def setFormat(format: String): ParagraphNode = js.native

@js.native
@JSGlobal("ParagraphNode")
object ParagraphNode extends ParagraphNodeStatic

@js.native
trait ParagraphNodeStatic extends js.Object:
  def getType(): String = js.native
  def clone(node: ParagraphNode): ParagraphNode = js.native
  def importJSON(serialized: js.Dynamic): ParagraphNode = js.native

@js.native
@JSGlobal("RootNode")
class RootNode(key: NodeKey = "") extends ElementNode(key):
  override def getChildAtIndex(index: Int): LexicalNode | Null = js.native
  override def getChildrenSize(): Int = js.native
  override def getChildren(): js.Array[LexicalNode] = js.native
  override def getFirstChild(): LexicalNode | Null = js.native
  override def getLastChild(): LexicalNode | Null = js.native
  override def append(child: LexicalNode): RootNode = js.native
  override def prepend(child: LexicalNode): RootNode = js.native

@js.native
@JSGlobal("RootNode")
object RootNode extends RootNodeStatic

@js.native
trait RootNodeStatic extends js.Object:
  def getType(): String = js.native
  def clone(node: RootNode): RootNode = js.native
  def importJSON(serialized: js.Dynamic): RootNode = js.native

@js.native
@JSGlobal("ElementNode")
class ElementNode(key: NodeKey = "") extends LexicalNode:
  def getChildAtIndex(index: Int): LexicalNode | Null = js.native
  def getChildrenSize(): Int = js.native
  def getChildren(): js.Array[LexicalNode] = js.native
  def getFirstChild(): LexicalNode | Null = js.native
  def getLastChild(): LexicalNode | Null = js.native
  def getChildrenKeys(): js.Array[NodeKey] = js.native
  def append(child: LexicalNode): ElementNode = js.native
  def prepend(child: LexicalNode): ElementNode = js.native
  def insertBefore(nodeToInsert: LexicalNode): LexicalNode = js.native
  def insertAfter(nodeToInsert: LexicalNode): LexicalNode = js.native
  def remove(child: LexicalNode): LexicalNode = js.native
  def replace(replaceWith: LexicalNode): LexicalNode = js.native
  def getDescendantByKey(key: NodeKey): LexicalNode | Null = js.native
  def clear(): ElementNode = js.native
  def getDirection(): String | Null = js.native
  def setDirection(direction: String | Null): ElementNode = js.native
  def canBeEmpty(): Boolean = js.native
  def isEmpty(): Boolean = js.native
  def canInsertTextAfter(): Boolean = js.native
  def canInsertTextBefore(): Boolean = js.native

@js.native
@JSGlobal("ElementNode")
object ElementNode extends ElementNodeStatic

@js.native
trait ElementNodeStatic extends js.Object:
  def getType(): String = js.native
  def clone(node: ElementNode): ElementNode = js.native
  def importJSON(serialized: js.Dynamic): ElementNode = js.native

@js.native
@JSGlobal("LineBreakNode")
class LineBreakNode(key: NodeKey = "") extends LexicalNode:
  def isLineBreak(): Boolean = js.native

@js.native
@JSGlobal("LineBreakNode")
object LineBreakNode extends LineBreakNodeStatic

@js.native
trait LineBreakNodeStatic extends js.Object:
  def getType(): String = js.native
  def clone(node: LineBreakNode): LineBreakNode = js.native
  def importJSON(serialized: js.Dynamic): LineBreakNode = js.native

@js.native
@JSGlobal("DecoratorNode")
class DecoratorNode[T](key: NodeKey = "") extends LexicalNode:
  def decorate(editor: LexicalEditor): T = js.native
  def getDecorator(): T = js.native

@js.native
@JSGlobal("DecoratorNode")
object DecoratorNode extends DecoratorNodeStatic

@js.native
trait DecoratorNodeStatic extends js.Object:
  def getType(): String = js.native
  def clone(node: DecoratorNode[Any]): DecoratorNode[Any] = js.native
  def importJSON(serialized: js.Dynamic): DecoratorNode[Any] = js.native

@js.native
@JSGlobal("TabNode")
class TabNode(key: NodeKey = "") extends LexicalNode

@js.native
@JSGlobal("TabNode")
object TabNode extends TabNodeStatic

@js.native
trait TabNodeStatic extends js.Object:
  def getType(): String = js.native
  def clone(node: TabNode): TabNode = js.native
  def importJSON(serialized: js.Dynamic): TabNode = js.native
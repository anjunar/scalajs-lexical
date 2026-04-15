package lexical

import scala.scalajs.js

class SelectionWrapper(val selection: BaseSelection | Null):
  def isRangeSelection: Boolean = 
    if (selection == null) false
    else {
      val s = selection.asInstanceOf[js.Dynamic]
      !js.isUndefined(s.anchor) && !js.isUndefined(s.focus)
    }

  def rangeSelection: Option[RangeSelection] = 
    if (isRangeSelection) Some(selection.asInstanceOf[RangeSelection])
    else None

  def isBold: Boolean = rangeSelection.exists(s => (s.format & 1) != 0)
  def isItalic: Boolean = rangeSelection.exists(s => (s.format & 2) != 0)
  def isUnderline: Boolean = rangeSelection.exists(s => (s.format & 4) != 0)
  def isCode: Boolean = rangeSelection.exists(s => (s.format & 16) != 0)

  def getSelectedText: String = rangeSelection.map(_.getNativeText()).getOrElse("")
  
  def isCollapsed: Boolean = 
    if (selection == null) true
    else selection.asInstanceOf[BaseSelection].isCollapsed()

  def getNodes: js.Array[LexicalNode] = 
    if (selection == null) js.Array()
    else selection.asInstanceOf[BaseSelection].getNodes()

object SelectionWrapper:
  def apply(editor: LexicalEditor): SelectionWrapper =
    val selection = editor.getEditorState().read(() => {
      Lexical.$getSelection()
    }).asInstanceOf[BaseSelection | Null]
    new SelectionWrapper(selection)

package lexical

import scala.scalajs.js

class SelectionWrapper(
    val isRangeSelection: Boolean,
    val isCollapsed: Boolean,
    val format: Int,
    val nodes: js.Array[LexicalNode],
    val selectedText: String
):
  def isBold: Boolean = (format & 1) != 0
  def isItalic: Boolean = (format & 2) != 0
  def isUnderline: Boolean = (format & 4) != 0
  def isCode: Boolean = (format & 16) != 0

  def getSelectedText: String = selectedText
  
  def getNodes: js.Array[LexicalNode] = nodes

object SelectionWrapper:
  def apply(editor: LexicalEditor): SelectionWrapper =
    editor.read(() => {
      val selection = Lexical.$getSelection()
      if (selection == null) {
        new SelectionWrapper(false, true, 0, js.Array(), "")
      } else {
        val baseSelection = selection.asInstanceOf[BaseSelection]
        
        // Check if it's a RangeSelection by checking for anchor/focus
        val selectionDyn = selection.asInstanceOf[js.Dynamic]
        val isRange = !js.isUndefined(selectionDyn.anchor) && !js.isUndefined(selectionDyn.focus)
        
        val nodes = baseSelection.getNodes()
        val isCollapsed = baseSelection.isCollapsed()
        
        var format = 0
        var selectedText = ""
        
        if (isRange) {
          val rs = selection.asInstanceOf[RangeSelection]
          format = rs.format
          selectedText = rs.getTextContent()
        }
        
        new SelectionWrapper(isRange, isCollapsed, format, nodes, selectedText)
      }
    })

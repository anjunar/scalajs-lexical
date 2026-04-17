package lexical

import scala.scalajs.js
import org.scalajs.dom

class FloatingToolbarManager(editor: LexicalEditor, modules: Seq[EditorModule]):
  private val toolbarDiv = dom.document.createElement("div").asInstanceOf[dom.HTMLElement]
  toolbarDiv.className = "floating-toolbar"
  toolbarDiv.style.position = "absolute"
  toolbarDiv.style.display = "none"
  toolbarDiv.style.zIndex = "1000"
  
  private val registry = new ToolbarRegistry(modules.toList)
  private val renderer = new FloatingToolbarRenderer()
  private val innerManager = new ToolbarManager(editor, registry, renderer)
  innerManager.createToolbar(toolbarDiv)
  dom.document.body.appendChild(toolbarDiv)

  def register(): js.Function0[Unit] =
    // The most robust way: only position on mouseup
    val handleMouseUp = (_: dom.MouseEvent) => {
       updatePosition()
    }
    
    dom.window.addEventListener("mouseup", handleMouseUp)

    () => {
      dom.window.removeEventListener("mouseup", handleMouseUp)
      toolbarDiv.remove()
    }

  private def updatePosition(): Unit =
    // Small delay to ensure Lexical has updated its internal selection
    dom.window.setTimeout(() => {
      editor.read(() => {
        val selection = Lexical.$getSelection()
        val shouldShow = selection != null && Lexical.$isRangeSelection(selection) && !selection.isCollapsed()

        if (shouldShow) {
          val domSelection = dom.window.getSelection()
          if (domSelection.rangeCount > 0) {
            val range = domSelection.getRangeAt(0)
            val rect = range.getBoundingClientRect()
            
            if (rect.width > 0 && rect.height > 0) {
              toolbarDiv.style.display = "flex"
              val left = rect.left + dom.window.pageXOffset + (rect.width / 2) - (toolbarDiv.offsetWidth / 2)
              val top = rect.top + dom.window.pageYOffset - toolbarDiv.offsetHeight - 10
              
              toolbarDiv.style.left = s"${left}px"
              toolbarDiv.style.top = s"${top}px"
            } else {
              toolbarDiv.style.display = "none"
            }
          } else {
            toolbarDiv.style.display = "none"
          }
        } else {
          toolbarDiv.style.display = "none"
        }
      })
    }, 20)

package lexical

import scala.scalajs.js
import org.scalajs.dom

class ToolbarManager(
  editor: LexicalEditor, 
  registry: ToolbarRegistry,
  renderer: ToolbarRenderer
):
  def createToolbar(container: dom.HTMLElement): Unit =
    val model = registry.getModel
    val toolbarElement = renderer.render(model, editor)
    container.innerHTML = ""
    container.appendChild(toolbarElement)

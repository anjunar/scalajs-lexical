package lexical

import scala.scalajs.js
import org.scalajs.dom

class LexicalBuilder:
  private var _namespace: String = "Lexical Editor"
  private var _theme: js.Object = js.Dynamic.literal()
  private var _nodes: js.Array[js.Any] = js.Array()
  private var _modules: js.Array[EditorModule] = js.Array()
  private var _editable: Boolean = true

  def withNamespace(name: String): this.type =
    _namespace = name
    this

  def withTheme(theme: js.Object): this.type =
    _theme = theme
    this

  def withNodes(nodes: js.Array[js.Any]): this.type =
    _nodes = nodes
    this

  def withModules(modules: EditorModule*): this.type =
    _modules = js.Array(modules*)
    this

  def withEditable(editable: Boolean): this.type =
    _editable = editable
    this

  def build(container: dom.HTMLElement): LexicalEditor =
    val config = js.Dynamic.literal(
      namespace = _namespace,
      theme = _theme,
      nodes = _nodes,
      editable = _editable,
      onError = (error: js.Error) => dom.console.error(error)
    ).asInstanceOf[EditorConfig]

    val editor = Lexical.createEditor(config)
    editor.setRootElement(container)

    // Standard Registrations
    LexicalRichText.registerRichText(editor)
    LexicalHistory.registerHistory(editor, LexicalHistory.createEmptyHistoryState(), 300)

    // Module Registrations
    _modules.foreach(_.register(editor))

    // Generic Decorator Listener for Vanilla JS / DOM integration
    editor.registerDecoratorListener((decorators: js.Dynamic) => {
      val decoratorsDict = decorators.asInstanceOf[js.Dictionary[dom.Node]]
      js.Object.entries(decoratorsDict).foreach { entry =>
        val nodeKey = entry._1
        val decoratorElement = entry._2
        val nodeContainer = editor.getElementByKey(nodeKey)
        if (nodeContainer != null && !nodeContainer.contains(decoratorElement)) {
          nodeContainer.innerHTML = ""
          nodeContainer.appendChild(decoratorElement)
        }
      }
    })

    editor

  def getModules: js.Array[EditorModule] = _modules

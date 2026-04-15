package lexical

import scala.scalajs.js
import org.scalajs.dom

class LexicalBuilder:
  private var _namespace: String = "Lexical Editor"
  private var _theme: EditorTheme = js.Dynamic.literal().asInstanceOf[EditorTheme]
  private var _nodes: js.Array[js.Any] = js.Array()
  private var _modules: js.Array[EditorModule] = js.Array()
  private var _toolbarElements: js.Array[ToolbarElement] = js.Array()
  private var _floatingToolbarElements: js.Array[ToolbarElement] = js.Array()
  private var _editable: Boolean = true
  private var _initialState: Option[String] = None
  private var _onStateChange: Option[String => Unit] = None

  def withNamespace(name: String): this.type =
    _namespace = name
    this

  def withTheme(theme: EditorTheme): this.type =
    _theme = theme
    this

  def withNodes(nodes: js.Array[js.Any]): this.type =
    _nodes = nodes
    this

  def withModules(modules: EditorModule*): this.type =
    _modules = js.Array(modules*)
    this

  private def extractModulesFromElements(elements: js.Array[ToolbarElement]): Seq[EditorModule] =
    def extract(el: ToolbarElement): Seq[EditorModule] = el match
      case m: EditorModule => Seq(m)
      case ToolbarGroup(children @ _*) => children.flatMap(extract)
      case _ => Seq.empty
    elements.flatMap(extract).toSeq

  def withToolbar(elements: ToolbarElement*): this.type =
    _toolbarElements = js.Array(elements*)
    _modules = js.Array((_modules.toSeq ++ extractModulesFromElements(_toolbarElements))*)
    this

  def withFloatingToolbar(elements: ToolbarElement*): this.type =
    _floatingToolbarElements = js.Array(elements*)
    _modules = js.Array((_modules.toSeq ++ extractModulesFromElements(_floatingToolbarElements))*)
    this

  def withEditable(editable: Boolean): this.type =
    _editable = editable
    this

  def withInitialState(json: String): this.type =
    _initialState = Some(json)
    this

  def onStateChange(callback: String => Unit): this.type =
    _onStateChange = Some(callback)
    this

  def buildHeadless(): LexicalEditor = buildInternal(None)
  def build(container: dom.HTMLElement): LexicalEditor = buildInternal(Some(container))

  private def buildInternal(container: Option[dom.HTMLElement]): LexicalEditor =
    val config = js.Dynamic.literal(
      namespace = _namespace,
      theme = _theme,
      nodes = _nodes,
      editable = _editable,
      onError = (error: js.Error) => dom.console.error(error)
    ).asInstanceOf[EditorConfig]

    val editor = Lexical.createEditor(config)
    container.foreach(editor.setRootElement)

    // Standard Registrations
    LexicalRichText.registerRichText(editor)
    LexicalHistory.registerHistory(editor, LexicalHistory.createEmptyHistoryState(), 300)

    // Initial State
    _initialState.foreach { json =>
      val state = editor.parseEditorState(json)
      editor.setEditorState(state, js.Dynamic.literal())
    }

    // State Change Listener
    _onStateChange.foreach { callback =>
      editor.registerUpdateListener((update: js.Dynamic) => {
        val editorState = editor.getEditorState()
        val json = js.JSON.stringify(editorState.toJSON())
        callback(json)
      })
    }

    // Module Registrations
    _modules.distinct.foreach(_.register(editor))

    // Floating Toolbar
    if (_floatingToolbarElements.nonEmpty) {
      new FloatingToolbarManager(editor, _floatingToolbarElements).register()
    }

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
  def getToolbarElements: js.Array[ToolbarElement] = _toolbarElements

package lexical

import scala.scalajs.js
import org.scalajs.dom

class LexicalBuilder:
  private var _namespace: String = "Lexical Editor"
  private var _theme: EditorTheme = js.Dynamic.literal().asInstanceOf[EditorTheme]
  private var _nodes: js.Array[js.Any] = js.Array()
  private var _modules: Seq[EditorModule] = Seq.empty
  private var _ribbonElements: Seq[ToolbarElement] = Seq.empty
  private var _floatingToolbarModules: Seq[EditorModule] = Seq.empty
  private var _toolbarElements: js.Array[ToolbarElement] = js.Array()
  private var _editable: Boolean = true
  private var _initialState: Option[String] = None
  private var _onStateChange: Option[String => Unit] = None
  private var _placeholder: String = ""

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
    _modules = _modules ++ modules
    this

  def withPlaceholder(text: String): this.type =
    _placeholder = text
    this

  private def extractToolbarElements(elements: js.Array[ToolbarElement]): Seq[ToolbarElement] =
    def extract(el: ToolbarElement): Seq[ToolbarElement] = el match
      case m: EditorModule => Seq(m)
      case d: ToolbarDropdown => Seq(d)
      case ToolbarGroup(children @ _*) => children.flatMap(extract)
      case _ => Seq.empty
    elements.flatMap(extract).toSeq

  private def extractModulesFromElements(elements: js.Array[ToolbarElement]): Seq[EditorModule] =
    def extract(el: ToolbarElement): Seq[EditorModule] = el match
      case m: EditorModule => Seq(m)
      case ToolbarGroup(children @ _*) => children.flatMap(extract)
      case _ => Seq.empty
    elements.flatMap(extract).toSeq

  def withToolbar(elements: ToolbarElement*): this.type =
    _ribbonElements = extractToolbarElements(js.Array(elements*))
    _toolbarElements = js.Array(_ribbonElements*)
    _modules = _modules ++ _ribbonElements.collect { case m: EditorModule => m }
    this


  def withFloatingToolbar(elements: ToolbarElement*): this.type =
    _floatingToolbarModules = extractModulesFromElements(js.Array(elements*)).collect { case m: EditorModule => m }
    _modules = _modules ++ _floatingToolbarModules
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
    ).asInstanceOf[CreateEditorArgs]

    val editor = Lexical.createEditor(config)
    
    container.foreach { c =>
      c.classList.add("lexical-editor-container")
      c.setAttribute("contenteditable", _editable.toString)
      c.setAttribute("spellcheck", "true")
      
      // If we have a placeholder, create it
      if (_placeholder.nonEmpty) {
        val placeholderDiv = dom.document.createElement("div").asInstanceOf[dom.HTMLElement]
        placeholderDiv.className = "lexical-placeholder"
        placeholderDiv.textContent = _placeholder
        c.appendChild(placeholderDiv)
        
        // Show/hide placeholder based on editor state
        editor.registerUpdateListener(_ => {
          editor.getEditorState().read(() => {
            val root = Lexical.$getRoot()
            val isEmpty = root.isEmpty()
            placeholderDiv.style.display = if (isEmpty) "block" else "none"
          })
        })
      }
      
      val rootElement = editor.getRootElement()
      if (rootElement != null) {
        rootElement.classList.add("lexical-editor-input")
        rootElement.setAttribute("contenteditable", _editable.toString)
        rootElement.setAttribute("spellcheck", "true")
      } else {
        // We might need to set the root element first
        // In this case, we'll hook into setRootElement if possible or just use the container if it's the root
        c.classList.add("lexical-editor-input") // Fallback
        editor.setRootElement(c)
      }
    }

    // Standard Registrations
    LexicalRichText.registerRichText(editor)
    if !_modules.exists(_.isInstanceOf[HistoryModule]) then
      LexicalHistory.registerHistory(editor, LexicalHistory.createEmptyHistoryState(), 300)
    if hasTableSupportNodes then
      LexicalTable.registerTablePlugin(editor)

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

    // Ribbon Toolbar
    if (_ribbonElements.nonEmpty) {
       // logic will use _ribbonElements
    }

    // Floating Toolbar
    if (_floatingToolbarModules.nonEmpty) {
      new FloatingToolbarManager(editor, _floatingToolbarModules).register()
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

  private def hasTableSupportNodes: Boolean =
    Seq(
      LexicalTable.TableNode,
      LexicalTable.TableRowNode,
      LexicalTable.TableCellNode
    ).forall(node => _nodes.exists(_ == node))

  def getModules: js.Array[EditorModule] = js.Array(_modules*)
  def getRibbonModules: Seq[ToolbarElement] = _ribbonElements
  def getToolbarElements: js.Array[ToolbarElement] = _toolbarElements

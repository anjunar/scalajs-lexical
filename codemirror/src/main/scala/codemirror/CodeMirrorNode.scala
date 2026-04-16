package codemirror

import scala.scalajs.js
import scala.scalajs.js.annotation.*
import org.scalajs.dom
import lexical.*

import scala.compiletime.uninitialized

class CodeMirrorComponent(
  val nodeKey: NodeKey,
  val lexicalEditor: LexicalEditor,
  var code: String,
  var language: String
):
  val container: dom.HTMLElement = dom.document.createElement("div").asInstanceOf[dom.HTMLElement]
  container.className = "codemirror-wrapper"
  
  var view: EditorView = uninitialized

  def initView(): Unit =
    val extensions = js.Array[js.Object](
      CodeMirrorKeymap.of(js.Array(indentWithTab) ++ defaultKeymap),
      syntaxHighlighting(defaultHighlightStyle),
      EditorView.lineWrapping,
      getLanguageExtension(language),
      EditorView.updateListener.of((update: EditorViewUpdate) => {
        if (update.docChanged) {
          val newCode = update.state.doc.toString()
          if (newCode != code) {
            code = newCode
            lexicalEditor.update(() => {
              val node = Lexical.$getNodeByKey(nodeKey)
              if (node != null && $isCodeMirrorNode(node)) {
                node.asInstanceOf[CodeMirrorNode].setCode(newCode)
              }
            }, js.Dynamic.literal().asInstanceOf[EditorUpdateOptions])
          }
        }
      })
    )

    val state = CodeMirrorEditorState.create(js.Dynamic.literal(
      doc = code,
      extensions = extensions
    ))

    view = new EditorView(js.Dynamic.literal(
      state = state,
      parent = container
    ).asInstanceOf[EditorViewConfig])

  initView()

  def update(newCode: String, newLanguage: String): Unit =
    var needsReinit = false
    if (language != newLanguage) {
      language = newLanguage
      needsReinit = true
    }
    
    if (needsReinit) {
      view.destroy()
      initView()
    } else if (code != newCode && view.state.doc.toString() != newCode) {
      code = newCode
      view.dispatch(js.Dynamic.literal(
        changes = js.Dynamic.literal(from = 0, to = view.state.doc.length.asInstanceOf[js.Any], insert = newCode)
      ))
    }

  def destroy(): Unit =
    if (view != null) {
      view.destroy()
    }
    container.remove()

  private def getLanguageExtension(language: String): js.Object =
    language match
      case "javascript" | "typescript" => javascript()
      case "python" => python()
      case "java" => java()
      case "cpp" => cpp()
      case "rust" => rust()
      case "sql" => sql()
      case "html" => html()
      case "css" => css()
      case "markdown" => markdown()
      case "json" => json()
      case "xml" => xml()
      case "yaml" => yaml()
      case "php" => php()
      case "scala" => StreamLanguage.define(scalaMode)
      case "kotlin" => StreamLanguage.define(kotlinMode)
      case "csharp" => StreamLanguage.define(csharpMode)
      case "shell" => StreamLanguage.define(shellMode)
      case "lua" => StreamLanguage.define(luaMode)
      case "haskell" => StreamLanguage.define(haskellMode)
      case "perl" => StreamLanguage.define(perlMode)
      case "r" => StreamLanguage.define(rMode)
      case "clojure" => StreamLanguage.define(clojureMode)
      case "erlang" => StreamLanguage.define(erlangMode)
      case "ruby" => StreamLanguage.define(rubyMode)
      case "swift" => StreamLanguage.define(swiftMode)
      case "objective-c" => StreamLanguage.define(objectiveCMode)
      case "dart" => StreamLanguage.define(dartMode)
      case _ => js.Array().asInstanceOf[js.Object]

@JSExportTopLevel("CodeMirrorNode")
class CodeMirrorNode(var _code: String = "", var _language: String = "javascript", key: NodeKey = "")
  extends DecoratorNode[dom.HTMLElement](key):
  
  def getCode(): String = _code
  
  def setCode(code: String): Unit =
    val writable = getWritable().asInstanceOf[CodeMirrorNode]
    writable._code = code

  def getLanguage(): String = _language
  
  def setLanguage(language: String): Unit =
    val writable = getWritable().asInstanceOf[CodeMirrorNode]
    writable._language = language

  override def getTextContent(): String = _code

  override def createDOM(config: js.Dynamic, editor: LexicalEditor): dom.HTMLElement =
    val element = dom.document.createElement("div").asInstanceOf[dom.HTMLElement]
    element.className = "codemirror-container"
    element

  override def updateDOM(prevNode: js.Any, domElement: dom.HTMLElement, config: js.Dynamic): Boolean = false

  override def decorate(editor: LexicalEditor): dom.HTMLElement =
    val key = getKey()
    var cmComponent = CodeMirrorNode.activeEditors.get(key).orNull
    if (cmComponent == null) {
      cmComponent = new CodeMirrorComponent(key, editor, _code, _language)
      CodeMirrorNode.activeEditors.put(key, cmComponent)
      dom.window.setTimeout(() => cmComponent.view.focus(), 0)
    } else {
      cmComponent.update(_code, _language)
    }
    cmComponent.container

  override def exportJSON(): js.Dynamic =
    js.Dynamic.literal(
      code = _code,
      language = _language,
      `type` = "codemirror",
      version = 1
    )

object CodeMirrorNode:
  val activeEditors = scala.collection.mutable.Map[NodeKey, CodeMirrorComponent]()

  @JSExportStatic
  def getType(): String = "codemirror"

  @JSExportStatic
  def clone(node: CodeMirrorNode): CodeMirrorNode =
    new CodeMirrorNode(node._code, node._language, node.getKey())

  @JSExportStatic
  def importJSON(serializedNode: js.Dynamic): CodeMirrorNode =
    val node = $createCodeMirrorNode(
      serializedNode.code.asInstanceOf[String],
      serializedNode.language.asInstanceOf[String]
    )
    node

@JSExportTopLevel("$createCodeMirrorNode")
def $createCodeMirrorNode(code: String = "", language: String = "javascript"): CodeMirrorNode =
  new CodeMirrorNode(code, language)

@JSExportTopLevel("$isCodeMirrorNode")
def $isCodeMirrorNode(node: LexicalNode | Null): Boolean =
  if (node == null) false
  else node.isInstanceOf[CodeMirrorNode]

@JSExportTopLevel("handleCodeMirrorMutation")
def handleCodeMirrorMutation(nodeKey: NodeKey, mutation: String, editor: LexicalEditor): Unit =
  if (mutation == "destroyed") {
    CodeMirrorNode.activeEditors.get(nodeKey).foreach { cmComponent =>
      cmComponent.destroy()
      CodeMirrorNode.activeEditors.remove(nodeKey)
    }
  }

object CodeMirrorPlugin:
  trait CodeMirrorPayload extends js.Object:
    var code: String
    var language: String

  val INSERT_CODEMIRROR_COMMAND: LexicalCommand[CodeMirrorPayload] = Lexical.createCommand[CodeMirrorPayload]("INSERT_CODEMIRROR")

  def register(editor: LexicalEditor): js.Function0[Unit] =
    val removeCommand = editor.registerCommand(
      INSERT_CODEMIRROR_COMMAND,
      (payload: CodeMirrorPayload, lexicalEditor: LexicalEditor) => {
        lexicalEditor.update(() => {
          val codeMirrorNode = $createCodeMirrorNode(payload.code, payload.language)
          lexical.Lexical.$insertNodes(js.Array(codeMirrorNode))
          lexicalEditor.focus(() => {}, js.Dynamic.literal().asInstanceOf[EditorFocusOptions])
        }, js.Dynamic.literal().asInstanceOf[EditorUpdateOptions])
        true
      },
      lexical.COMMAND_PRIORITY.EDITOR
    )

    val removeMutation = editor.registerMutationListener(
      js.constructorOf[CodeMirrorNode],
      (mutations: js.Dynamic, options: js.Dynamic) => {
        mutations.forEach((mutation: String, nodeKey: String) => {
          if (mutation == "destroyed") {
             handleCodeMirrorMutation(nodeKey, mutation, editor)
          }
        })
      },
      js.Dynamic.literal()
    )

    () => {
      removeCommand()
      removeMutation()
    }

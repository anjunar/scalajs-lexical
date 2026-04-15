package codemirror

import scala.scalajs.js
import scala.scalajs.js.annotation.*
import org.scalajs.dom
import lexical.*

@js.native
@JSImport("/src/codemirror/CodeMirrorNode.js", "CodeMirrorNode")
class CodeMirrorNode extends DecoratorNode[dom.HTMLElement]:
  def getCode(): String = js.native
  def setCode(code: String): Unit = js.native
  def getLanguage(): String = js.native
  def setLanguage(language: String): Unit = js.native
  override def getTextContent(): String = js.native

@js.native
@JSImport("/src/codemirror/CodeMirrorNode.js", "$createCodeMirrorNode")
def $createCodeMirrorNode(code: String, language: String): CodeMirrorNode = js.native

@js.native
@JSImport("/src/codemirror/CodeMirrorNode.js", "$isCodeMirrorNode")
def $isCodeMirrorNode(node: LexicalNode | Null): Boolean = js.native

@js.native
@JSImport("/src/codemirror/CodeMirrorNode.js", "handleCodeMirrorMutation")
def handleCodeMirrorMutation(nodeKey: NodeKey, mutation: String, editor: LexicalEditor): Unit = js.native

object CodeMirrorPlugin:
  trait CodeMirrorPayload extends js.Object:
    var code: String
    var language: String

  val INSERT_CODEMIRROR_COMMAND: LexicalCommand[CodeMirrorPayload] = lexical.createCommand[CodeMirrorPayload]("INSERT_CODEMIRROR")

  def register(editor: LexicalEditor): js.Function0[Unit] =
    val removeCommand = editor.registerCommand(
      INSERT_CODEMIRROR_COMMAND,
      (payload: CodeMirrorPayload, lexicalEditor: LexicalEditor) => {
        val codeMirrorNode = $createCodeMirrorNode(payload.code, payload.language)
        lexical.Lexical.$insertNodes(js.Array(codeMirrorNode))
        true
      },
      lexical.COMMAND_PRIORITY.CRITICAL
    )

    // Using registerMutationListener to clean up CodeMirror instances when nodes are destroyed
    val removeMutation = editor.registerMutationListener(
      js.constructorOf[CodeMirrorNode],
      (mutations: js.Dynamic, options: js.Dynamic) => {
        // mutations is a js.Map<NodeKey, "created" | "updated" | "destroyed">
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

class CodeMirrorModule extends EditorModule:
  override def name: String = "Code Block"
  override def iconName: Option[String] = Some("code")
  override val keyBinding: Option[String] = Some("Alt+C")

  override def execute(editor: LexicalEditor): Unit =
    editor.dispatchCommand(CodeMirrorPlugin.INSERT_CODEMIRROR_COMMAND, new CodeMirrorPlugin.CodeMirrorPayload:
      var code = ""
      var language = "javascript"
    )

  override def register(editor: LexicalEditor): js.Function0[Unit] =
    CodeMirrorPlugin.register(editor)

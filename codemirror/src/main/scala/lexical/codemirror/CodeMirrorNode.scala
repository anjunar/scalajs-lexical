package lexical.codemirror

import lexical.*
import org.scalajs.dom as dom
import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSGlobal("CodeMirrorNode")
class CodeMirrorNode extends lexical.DecoratorNode:
  def getCode(): String = js.native
  def setCode(code: String): Unit = js.native
  def getLanguage(): String = js.native
  def setLanguage(language: String): Unit = js.native
  override def getTextContent(): String = js.native

@js.native
@JSGlobal("$createCodeMirrorNode")
def $createCodeMirrorNode(code: String, language: String): CodeMirrorNode = js.native

object CodeMirrorPlugin:
  private trait CodeMirrorPayload extends js.Object:
    var code: String
    var language: String

  private val INSERT_CODEMIRROR_COMMAND: LexicalCommand[CodeMirrorPayload] = lexical.createCommand[CodeMirrorPayload]("INSERT_CODEMIRROR")

  def register(editor: LexicalEditor): js.Function0[Unit] =
    editor.registerCommand(
      INSERT_CODEMIRROR_COMMAND,
      (payload: CodeMirrorPayload, lexicalEditor: LexicalEditor) => {
        val codeMirrorNode = $createCodeMirrorNode(payload.code, payload.language)
        lexical.Lexical.$insertNodes(js.Array(codeMirrorNode))
        true
      },
      lexical.COMMAND_PRIORITY.CRITICAL
    )

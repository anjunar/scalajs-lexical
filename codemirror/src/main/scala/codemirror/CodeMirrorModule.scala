package codemirror

import scala.scalajs.js
import lexical.*

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

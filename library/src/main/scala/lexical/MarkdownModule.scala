package lexical

import scala.scalajs.js

class MarkdownModule(transformers: js.Array[js.Any] = LexicalMarkdown.TRANSFORMERS) extends EditorModule:
  override def name: String = "Markdown"
  override def execute(editor: LexicalEditor): Unit = () // No UI action needed

  override def register(editor: LexicalEditor): js.Function0[Unit] =
    LexicalMarkdown.registerMarkdownShortcuts(editor, transformers)

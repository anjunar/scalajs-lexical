package lexical

import scala.scalajs.js

abstract class CommandModule[P](
    override val name: String,
    val command: LexicalCommand[P],
    val payload: P,
    override val iconName: Option[String] = None,
    override val keyBinding: Option[String] = None,
    override val metadata: ToolbarMetadata
) extends EditorModule:
  override def execute(editor: LexicalEditor): Unit =
    editor.dispatchCommand(command, payload)

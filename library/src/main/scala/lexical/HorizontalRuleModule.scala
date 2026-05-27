package lexical

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("@lexical/extension", JSImport.Namespace)
private object LexicalHorizontalRule extends js.Object:
  def INSERT_HORIZONTAL_RULE_COMMAND: LexicalCommand[Unit] = js.native
  def $createHorizontalRuleNode(): HorizontalRuleNode = js.native

class HorizontalRuleModule extends EditorModule:
  override def name: String = "Horizontal Rule"
  override def iconName: Option[String] = Some("horizontal_rule")
  override def metadata: ToolbarMetadata = new ToolbarMetadata:
    val tabName = "Insert"
    val sectionName = "Layout"
    val order = 1

  override def execute(editor: LexicalEditor): Unit =
    editor.dispatchCommand(LexicalHorizontalRule.INSERT_HORIZONTAL_RULE_COMMAND, ())

  override def register(editor: LexicalEditor): js.Function0[Unit] =
    editor.registerCommand(
      LexicalHorizontalRule.INSERT_HORIZONTAL_RULE_COMMAND,
      (_: Unit, lexicalEditor: LexicalEditor) => {
        lexicalEditor.update(() => {
          val selection = Lexical.$getSelection()
          if (selection != null && Lexical.$isRangeSelection(selection)) {
            Lexical.$insertNodes(js.Array(LexicalHorizontalRule.$createHorizontalRuleNode()))
          }
        }, js.Dynamic.literal().asInstanceOf[EditorUpdateOptions])
        true
      },
      COMMAND_PRIORITY.EDITOR
    )

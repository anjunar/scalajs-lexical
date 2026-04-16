package lexical

import org.scalajs.dom
import scala.scalajs.js

trait ToolbarDropdown extends ToolbarElement:
  def name: String
  def options: Seq[ToolbarDropdownOption]
  def getSelectedValue(editor: LexicalEditor): String
  def onSelect(editor: LexicalEditor, value: String): Unit

case class ToolbarDropdownOption(label: String, value: String)

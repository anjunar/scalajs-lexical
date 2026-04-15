package lexical

import scala.scalajs.js

trait ToolbarElement

case class ToolbarGroup(elements: ToolbarElement*) extends ToolbarElement
case class ToolbarSeparator() extends ToolbarElement

// Update EditorModule to extend ToolbarElement
// I will do this in the EditorModule.scala later

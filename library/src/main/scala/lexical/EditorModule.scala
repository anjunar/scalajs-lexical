package lexical

import scala.scalajs.js

trait ToolbarMetadata:
  def tabName: String
  def sectionName: String
  def order: Int

trait EditorModule extends ToolbarElement:
  def name: String
  def iconName: Option[String] = None
  def metadata: ToolbarMetadata
  
  def canActivate(editor: LexicalEditor): Boolean = true
  def isActive(editor: LexicalEditor): Boolean = false
  
  def execute(editor: LexicalEditor): Unit
  
  def keyBinding: Option[String] = None
  
  def register(editor: LexicalEditor): js.Function0[Unit] = () => ()

  override def equals(other: Any): Boolean = other match
    case that: EditorModule => this.name == that.name
    case _ => false

  override def hashCode(): Int = name.hashCode()

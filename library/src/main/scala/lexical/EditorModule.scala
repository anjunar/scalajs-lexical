package lexical

import scala.scalajs.js

trait EditorModule:
  def name: String
  def iconName: Option[String] = None
  
  def canActivate(editor: LexicalEditor): Boolean = true
  def isActive(editor: LexicalEditor): Boolean = false
  
  def execute(editor: LexicalEditor): Unit
  
  def keyBinding: Option[String] = None
  
  def register(editor: LexicalEditor): js.Function0[Unit] = () => ()

package lexical.codemirror

import scala.scalajs.js
import scala.scalajs.js.annotation.*
import org.scalajs.dom

@JSImport("@codemirror/state", "EditorState")
@js.native
object CodeMirrorEditorState extends js.Object:
  def create(config: js.Object): EditorState = js.native

@JSImport("@codemirror/view", "EditorView")
@js.native
class EditorView(config: EditorViewConfig) extends js.Object:
  def state: EditorState = js.native
  @JSName("dom")
  def domElement: dom.HTMLElement = js.native
  def dispatch(config: js.Object): Unit = js.native
  def destroy(): Unit = js.native
  def focus(): Unit = js.native

@JSImport("@codemirror/view", "EditorView")
@js.native
object EditorView extends js.Object:
  val lineWrapping: js.Object = js.native
  val updateListener: CodeMirrorUpdateListener = js.native

@js.native
trait CodeMirrorUpdateListener extends js.Object:
  def of(callback: js.Function1[EditorViewUpdate, Unit]): js.Object = js.native

@js.native
trait EditorState extends js.Object:
  def doc: js.Dynamic = js.native

@js.native
trait EditorViewConfig extends js.Object:
  var state: EditorState = js.native
  var parent: dom.HTMLElement = js.native

@js.native
trait EditorViewUpdate extends js.Object:
  def docChanged: Boolean = js.native
  def state: EditorState = js.native

@JSImport("@codemirror/view", "keymap")
@js.native
object CodeMirrorKeymap extends js.Object:
  def of(keyBindings: js.Array[js.Object]): js.Object = js.native

@JSImport("@codemirror/commands", "defaultKeymap")
@js.native
val defaultKeymap: js.Array[js.Object] = js.native

@JSImport("@codemirror/commands", "indentWithTab")
@js.native
val indentWithTab: js.Object = js.native

@JSImport("@codemirror/language", "syntaxHighlighting")
@js.native
def syntaxHighlighting(style: js.Object): js.Object = js.native

@JSImport("@codemirror/language", "defaultHighlightStyle")
@js.native
val defaultHighlightStyle: js.Object = js.native

@JSImport("@codemirror/language", "HighlightStyle")
@js.native
object HighlightStyle extends js.Object:
  def define(specs: js.Array[js.Object]): js.Object = js.native

@JSImport("@codemirror/language", "StreamLanguage")
@js.native
object StreamLanguage extends js.Object:
  def define(mode: js.Object): js.Object = js.native

@JSImport("@lezer/highlight", "tags")
@js.native
object HighlightTags extends js.Object:
  val keyword: js.Object = js.native
  val atom: js.Object = js.native
  val bool: js.Object = js.native
  val number: js.Object = js.native
  val string: js.Object = js.native
  val regexp: js.Object = js.native
  val escape: js.Object = js.native
  val comment: js.Object = js.native
  val lineComment: js.Object = js.native
  val blockComment: js.Object = js.native
  val docComment: js.Object = js.native
  val name: js.Object = js.native
  val variableName: js.Object = js.native
  val propertyName: js.Object = js.native
  val typeName: js.Object = js.native
  val className: js.Object = js.native
  val namespace: js.Object = js.native
  val operator: js.Object = js.native
  val operatorKeyword: js.Object = js.native
  val controlKeyword: js.Object = js.native
  val definitionKeyword: js.Object = js.native
  val moduleKeyword: js.Object = js.native
  val punctuation: js.Object = js.native
  val bracket: js.Object = js.native
  val angleBracket: js.Object = js.native
  val tagName: js.Object = js.native
  val attributeName: js.Object = js.native
  val meta: js.Object = js.native
  val annotation: js.Object = js.native
  val definition: js.Function1[js.Object, js.Object] = js.native
  val constant: js.Function1[js.Object, js.Object] = js.native
  @JSName("function")
  val functionTag: js.Function1[js.Object, js.Object] = js.native
  val special: js.Function1[js.Object, js.Object] = js.native

// Languages
@JSImport("@codemirror/lang-javascript", "javascript")
@js.native
def javascript(): js.Object = js.native

@JSImport("@codemirror/lang-python", "python")
@js.native
def python(): js.Object = js.native

@JSImport("@codemirror/lang-java", "java")
@js.native
def java(): js.Object = js.native

@JSImport("@codemirror/lang-cpp", "cpp")
@js.native
def cpp(): js.Object = js.native

@JSImport("@codemirror/lang-rust", "rust")
@js.native
def rust(): js.Object = js.native

@JSImport("@codemirror/lang-sql", "sql")
@js.native
def sql(): js.Object = js.native

@JSImport("@codemirror/lang-html", "html")
@js.native
def html(): js.Object = js.native

@JSImport("@codemirror/lang-css", "css")
@js.native
def css(): js.Object = js.native

@JSImport("@codemirror/lang-markdown", "markdown")
@js.native
def markdown(): js.Object = js.native

@JSImport("@codemirror/lang-json", "json")
@js.native
def json(): js.Object = js.native

@JSImport("@codemirror/lang-xml", "xml")
@js.native
def xml(): js.Object = js.native

@JSImport("@codemirror/lang-yaml", "yaml")
@js.native
def yaml(): js.Object = js.native

@JSImport("@codemirror/lang-php", "php")
@js.native
def php(): js.Object = js.native

// Legacy modes
@JSImport("@codemirror/legacy-modes/mode/clike", "scala")
@js.native
val scalaMode: js.Object = js.native

@JSImport("@codemirror/legacy-modes/mode/clike", "kotlin")
@js.native
val kotlinMode: js.Object = js.native

@JSImport("@codemirror/legacy-modes/mode/clike", "csharp")
@js.native
val csharpMode: js.Object = js.native

@JSImport("@codemirror/legacy-modes/mode/clike", "objectiveC")
@js.native
val objectiveCMode: js.Object = js.native

@JSImport("@codemirror/legacy-modes/mode/clike", "dart")
@js.native
val dartMode: js.Object = js.native

@JSImport("@codemirror/legacy-modes/mode/shell", "shell")
@js.native
val shellMode: js.Object = js.native

@JSImport("@codemirror/legacy-modes/mode/lua", "lua")
@js.native
val luaMode: js.Object = js.native

@JSImport("@codemirror/legacy-modes/mode/haskell", "haskell")
@js.native
val haskellMode: js.Object = js.native

@JSImport("@codemirror/legacy-modes/mode/perl", "perl")
@js.native
val perlMode: js.Object = js.native

@JSImport("@codemirror/legacy-modes/mode/r", "r")
@js.native
val rMode: js.Object = js.native

@JSImport("@codemirror/legacy-modes/mode/clojure", "clojure")
@js.native
val clojureMode: js.Object = js.native

@JSImport("@codemirror/legacy-modes/mode/erlang", "erlang")
@js.native
val erlangMode: js.Object = js.native

@JSImport("@codemirror/legacy-modes/mode/ruby", "ruby")
@js.native
val rubyMode: js.Object = js.native

@JSImport("@codemirror/legacy-modes/mode/swift", "swift")
@js.native
val swiftMode: js.Object = js.native

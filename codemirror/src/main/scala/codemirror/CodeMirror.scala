package codemirror

import scala.scalajs.js
import scala.scalajs.js.annotation.*
import org.scalajs.dom

@JSImport("@codemirror/state", JSImport.Namespace)
@js.native
object CodeMirrorEditorState extends js.Object:
  def create(config: js.Object): EditorState = js.native

@JSImport("@codemirror/view", "EditorView")
@js.native
object CodeMirrorEditorView extends js.Object:
  def from(state: EditorState, config: EditorViewConfig): EditorView = js.native

@JSImport("@codemirror/view", JSImport.Namespace)
@js.native
object CodeMirrorKeymap extends js.Object:
  def of(keyBindings: js.Array[KeyBinding]): js.Object = js.native

@js.native
trait EditorState extends js.Object:
  def doc: String = js.native

@js.native
trait EditorView extends js.Object:
  def state: EditorState = js.native
  def domElement: dom.HTMLElement = js.native
  def dispatch(config: js.Object): Unit = js.native
  def destroy(): Unit = js.native

@js.native
trait EditorViewConfig extends js.Object:
  var parent: dom.HTMLElement
  var state: js.Object
  var extensions: js.Array[js.Object]

@js.native
trait EditorViewUpdate extends js.Object:
  def docChanged: Boolean = js.native

@js.native
trait KeyBinding extends js.Object:
  var key: String
  var run: js.Function2[EditorView, js.Object, Boolean]

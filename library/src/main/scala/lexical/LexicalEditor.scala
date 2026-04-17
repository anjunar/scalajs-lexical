package lexical

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal
import org.scalajs.dom as dom

type UpdateTag = String

type EditorThemeClassName = String

trait TextNodeThemeClasses extends js.Object:
  var base: EditorThemeClassName
  var bold: EditorThemeClassName
  var code: EditorThemeClassName
  var highlight: EditorThemeClassName
  var italic: EditorThemeClassName
  var lowercase: EditorThemeClassName
  var uppercase: EditorThemeClassName
  var capitalize: EditorThemeClassName
  var strikethrough: EditorThemeClassName
  var subscript: EditorThemeClassName
  var superscript: EditorThemeClassName
  var underline: EditorThemeClassName
  var underlineStrikethrough: EditorThemeClassName

trait EditorThemeClasses extends js.Object:
  var blockCursor: EditorThemeClassName
  var characterLimit: EditorThemeClassName
  var code: EditorThemeClassName
  var codeHighlight: js.Dictionary[EditorThemeClassName]
  var hashtag: EditorThemeClassName
  var specialText: EditorThemeClassName
  var heading: js.Dynamic
  var hr: EditorThemeClassName
  var hrSelected: EditorThemeClassName
  var image: EditorThemeClassName
  var link: EditorThemeClassName
  var list: js.Dynamic
  var ltr: EditorThemeClassName
  var mark: EditorThemeClassName
  var paragraph: EditorThemeClassName
  var quote: EditorThemeClassName
  var root: EditorThemeClassName
  var rtl: EditorThemeClassName
  var tab: EditorThemeClassName
  var indent: EditorThemeClassName

trait EditorUpdateOptions extends js.Object:
  var onUpdate: js.UndefOr[js.Function0[Unit]]
  var skipTransforms: js.UndefOr[Boolean]
  var tag: js.UndefOr[UpdateTag | js.Array[UpdateTag]]
  var discrete: js.UndefOr[Boolean]

trait EditorFocusOptions extends js.Object:
  var defaultSelection: js.UndefOr["rootStart" | "rootEnd"]

@js.native
trait LexicalEditor extends js.Object:
  def isComposing(): Boolean = js.native
  def registerUpdateListener(listener: js.Function1[js.Dynamic, Unit]): js.Function0[Unit] = js.native
  def registerEditableListener(listener: js.Function1[Boolean, Unit]): js.Function0[Unit] = js.native
  def registerDecoratorListener[T](listener: js.Function1[js.Dynamic, Unit]): js.Function0[Unit] = js.native
  def registerTextContentListener(listener: js.Function1[String, Unit]): js.Function0[Unit] = js.native
  def registerRootListener(listener: js.Function2[dom.HTMLElement | Null, dom.HTMLElement | Null, Unit]): js.Function0[Unit] = js.native
  def registerCommand[P](command: LexicalCommand[P], listener: js.Function2[P, LexicalEditor, Boolean], priority: Int): js.Function0[Unit] = js.native
  def registerMutationListener(klass: js.Dynamic, listener: js.Function2[js.Dynamic, js.Dynamic, Unit], options: js.UndefOr[js.Object] = js.undefined): js.Function0[Unit] = js.native
  def registerNodeTransform[T <: LexicalNode](klass: js.Dynamic, listener: js.Function1[T, Unit]): js.Function0[Unit] = js.native
  def hasNode(node: js.Dynamic): Boolean = js.native
  def dispatchCommand[P](command: LexicalCommand[P], payload: P): Boolean = js.native
  def getDecorators(): js.Dictionary[js.Any] = js.native
  def getRootElement(): dom.HTMLElement | Null = js.native
  def getKey(): String = js.native
  def setRootElement(element: dom.HTMLElement | Null): Unit = js.native
  def getElementByKey(key: NodeKey): dom.HTMLElement | Null = js.native
  def getEditorState(): js.Dynamic = js.native
  def setEditorState(state: js.Dynamic, options: js.Dynamic): Unit = js.native
  def parseEditorState(state: String | js.Dynamic): js.Dynamic = js.native
  def read[T](callback: js.Function0[T]): T = js.native
  def update(callback: js.Function0[Unit], options: EditorUpdateOptions): Unit = js.native
  def focus(callback: js.Function0[Unit], options: EditorFocusOptions): Unit = js.native
  def blur(): Unit = js.native
  def isEditable(): Boolean = js.native
  def setEditable(editable: Boolean): Unit = js.native
  def toJSON(): js.Dynamic = js.native

extension (editor: LexicalEditor)
  def getSelectionWrapper(): SelectionWrapper = SelectionWrapper(editor)
  def getDialogService: DialogService = editor.asInstanceOf[js.Dynamic].dialogService.asInstanceOf[DialogService]
  def setDialogService(service: DialogService): Unit = editor.asInstanceOf[js.Dynamic].dialogService = service.asInstanceOf[js.Any]

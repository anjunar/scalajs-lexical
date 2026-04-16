package lexical

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport("@lexical/table", JSImport.Namespace)
@js.native
object LexicalTable extends js.Object:
  def TableNode: js.Dynamic = js.native
  def TableRowNode: js.Dynamic = js.native
  def TableCellNode: js.Dynamic = js.native
  def INSERT_TABLE_COMMAND: LexicalCommand[js.Dynamic] = js.native
  def registerTablePlugin(editor: LexicalEditor): js.Function0[Unit] = js.native

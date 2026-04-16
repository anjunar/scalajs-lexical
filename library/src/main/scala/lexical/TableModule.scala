package lexical

import org.scalajs.dom
import scala.scalajs.js

class TableModule extends EditorModule:
  override def name: String = "Table"
  override def iconName: Option[String] = Some("table_chart")
  override def metadata: ToolbarMetadata = new ToolbarMetadata:
    val tabName = "Insert"
    val sectionName = "Table"
    val order = 1

  override def execute(editor: LexicalEditor): Unit =
    editor.getDialogService.show("Insert Table", () => {
      val content = dom.document.createElement("div").asInstanceOf[dom.HTMLElement]
      
      val rowsLabel = dom.document.createElement("label").asInstanceOf[dom.HTMLElement]
      rowsLabel.textContent = "Rows"
      val rowsInput = dom.document.createElement("input").asInstanceOf[dom.HTMLInputElement]
      rowsInput.setAttribute("type", "number")
      rowsInput.value = "3"
      rowsInput.id = "table-rows"
      
      val colsLabel = dom.document.createElement("label").asInstanceOf[dom.HTMLElement]
      colsLabel.textContent = "Columns"
      val colsInput = dom.document.createElement("input").asInstanceOf[dom.HTMLInputElement]
      colsInput.setAttribute("type", "number")
      colsInput.value = "3"
      colsInput.id = "table-cols"
      
      content.appendChild(rowsLabel)
      content.appendChild(rowsInput)
      content.appendChild(colsLabel)
      content.appendChild(colsInput)
      content
    }, (content) => {
      val rows = content.querySelector("#table-rows").asInstanceOf[dom.HTMLInputElement].value.toInt
      val cols = content.querySelector("#table-cols").asInstanceOf[dom.HTMLInputElement].value.toInt
      
      val payload = js.Dynamic.literal(columns = cols.toString, rows = rows.toString, includeHeaders = false)
      editor.dispatchCommand(LexicalTable.INSERT_TABLE_COMMAND, payload)
    })

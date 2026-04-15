package codemirror

import scala.scalajs.js
import org.scalajs.dom
import lexical.*

class CodeMirrorModule extends EditorModule:
  override def name: String = "Code Block"
  override def iconName: Option[String] = Some("code")
  override val keyBinding: Option[String] = Some("Alt+C")
  override def metadata: ToolbarMetadata = new ToolbarMetadata:
    val tabName = "Insert"
    val sectionName = "Code"
    val order = 1

  private val languages = Seq(
    "javascript", "typescript", "html", "css", "python", "java", "cpp", "csharp",
    "php", "ruby", "rust", "go", "scala", "kotlin", "swift", "markdown",
    "json", "xml", "yaml", "sql", "shell", "lua", "haskell", "objective-c",
    "perl", "r", "dart", "clojure", "erlang"
  )

  override def execute(editor: LexicalEditor): Unit =
    val dialogService = editor.getDialogService
    
    val contentProvider = () => {
      val container = dom.document.createElement("div").asInstanceOf[dom.HTMLElement]
      
      val label = dom.document.createElement("label").asInstanceOf[dom.HTMLLabelElement]
      label.textContent = "Language:"
      label.style.display = "block"
      label.style.marginBottom = "8px"
      container.appendChild(label)
      
      val select = dom.document.createElement("select").asInstanceOf[dom.HTMLSelectElement]
      select.style.width = "100%"
      select.style.padding = "8px"
      select.style.borderRadius = "4px"
      select.style.border = "1px solid #ccc"
      
      languages.sorted.foreach { lang =>
        val option = dom.document.createElement("option").asInstanceOf[dom.HTMLOptionElement]
        option.value = lang
        option.textContent = lang match {
          case "cpp" => "C++"
          case "csharp" => "C#"
          case "php" => "PHP"
          case "sql" => "SQL"
          case "yaml" => "YAML"
          case "json" => "JSON"
          case "xml" => "XML"
          case "html" => "HTML"
          case "css" => "CSS"
          case "objective-c" => "Objective-C"
          case "r" => "R"
          case _ => lang.capitalize
        }
        if (lang == "javascript") option.selected = true
        select.appendChild(option)
      }
      container.appendChild(select)
      container
    }
    
    dialogService.show("Insert Code Block", contentProvider, (content: dom.HTMLElement) => {
      val select = content.querySelector("select").asInstanceOf[dom.HTMLSelectElement]
      val selectedLanguage = select.value
      
      editor.dispatchCommand(CodeMirrorPlugin.INSERT_CODEMIRROR_COMMAND, new CodeMirrorPlugin.CodeMirrorPayload:
        var code = ""
        var language = selectedLanguage
      )
    })

  override def register(editor: LexicalEditor): js.Function0[Unit] =
    CodeMirrorPlugin.register(editor)

package lexical

import org.scalajs.dom

private object ToolbarRefresh:
  def bind(editor: LexicalEditor)(refresh: () => Unit): Unit =
    def update(): Unit = refresh()

    editor.registerUpdateListener(_ => update())
    editor.registerCommand(
      Lexical.SELECTION_CHANGE_COMMAND,
      (_: Unit, _: LexicalEditor) => {
        update()
        false
      },
      COMMAND_PRIORITY.LOW
    )
    update()

  def bindActiveState(button: dom.HTMLButtonElement, editor: LexicalEditor, module: EditorModule): Unit =
    bind(editor) { () =>
      editor.read(() =>
        button.classList.toggle("active", module.isActive(editor))
        button.disabled = !module.canActivate(editor)
      )
    }

  def bindDropdownLabel(button: dom.HTMLButtonElement, editor: LexicalEditor, dropdown: ToolbarDropdown): Unit =
    bind(editor) { () =>
      val selectedValue = editor.read(() => dropdown.getSelectedValue(editor))
      val selectedLabel =
        dropdown.options.find(_.value == selectedValue).map(_.label).getOrElse(selectedValue)
      button.textContent = s"${dropdown.name}: $selectedLabel"
      button.title = s"${dropdown.name} - $selectedLabel"
    }

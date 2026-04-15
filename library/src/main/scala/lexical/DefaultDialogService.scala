package lexical

import org.scalajs.dom
import org.scalajs.dom.HTMLElement

object DefaultDialogService extends DialogService:
  override def show(title: String, content: HTMLElement, onConfirm: () => Unit): Unit =
    // Keeping the original implementation here temporarily as the default
    Dialog.show(title, content, onConfirm)

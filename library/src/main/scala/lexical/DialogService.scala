package lexical

import org.scalajs.dom.HTMLElement

trait DialogService:
  def show(title: String, content: HTMLElement, onConfirm: () => Unit): Unit

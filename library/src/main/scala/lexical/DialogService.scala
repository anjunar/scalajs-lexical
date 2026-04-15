package lexical

import org.scalajs.dom.HTMLElement

trait DialogService:
  def show(title: String, contentProvider: () => HTMLElement, onConfirm: HTMLElement => Unit): Unit

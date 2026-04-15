package lexical

import org.scalajs.dom
import org.scalajs.dom.HTMLElement

class DemoDialogService extends DialogService:
  override def show(title: String, contentProvider: () => HTMLElement, onConfirm: HTMLElement => Unit): Unit =
    val backdrop = dom.document.createElement("div").asInstanceOf[dom.HTMLElement]
    backdrop.className = "modal-backdrop"
    
    val modal = dom.document.createElement("div").asInstanceOf[dom.HTMLElement]
    modal.className = "modal-content"
    
    val titleEl = dom.document.createElement("h3").asInstanceOf[dom.HTMLElement]
    titleEl.textContent = title
    modal.appendChild(titleEl)
    
    val content = contentProvider()
    modal.appendChild(content)
    
    val actions = dom.document.createElement("div").asInstanceOf[dom.HTMLElement]
    actions.className = "modal-actions"
    
    def close(): Unit =
      if (dom.document.body.contains(backdrop)) {
        dom.document.body.removeChild(backdrop)
      }

    val cancelBtn = dom.document.createElement("button").asInstanceOf[dom.HTMLButtonElement]
    cancelBtn.textContent = "Cancel"
    cancelBtn.onclick = (_: dom.MouseEvent) => close()
    
    val confirmBtn = dom.document.createElement("button").asInstanceOf[dom.HTMLButtonElement]
    confirmBtn.textContent = "Confirm"
    confirmBtn.className = "confirm-btn"
    confirmBtn.onclick = (_: dom.MouseEvent) =>
      onConfirm(content)
      close()
    
    actions.appendChild(cancelBtn)
    actions.appendChild(confirmBtn)
    modal.appendChild(actions)
    
    backdrop.appendChild(modal)
    dom.document.body.appendChild(backdrop)
    
    backdrop.onclick = (e: dom.MouseEvent) =>
      if (e.target == backdrop) close()

import { writeFile } from "node:fs/promises";
import { pathToFileURL } from "node:url";
import { Window } from "happy-dom";

const [, , entryPath, routePath, outputPath] = process.argv;

if (!entryPath || !outputPath) {
  console.error("Usage: node scripts/render-ssr-route.mjs <entry.js> <route> <output.html>");
  process.exit(2);
}

const window = new Window({
  url: "http://localhost/",
  width: 1280,
  height: 900,
});

window.document.documentElement.innerHTML = `<!doctype html>
<html>
  <body>
    <div id="toolbar"></div>
    <div id="editor" contenteditable="true"></div>
    <pre id="state"></pre>
  </body>
</html>`;

installBrowserGlobals(window);

try {
  // Import the built Scala.js application
  // We use a query param to ensure it's loaded as a fresh module if needed
  const entryUrl = `${pathToFileURL(entryPath).href}?t=${Date.now()}`;
  await import(entryUrl);
  
  // Wait for Scala.js and Lexical to finish their initial updates
  await waitForRender(window);

  const editor = window.document.getElementById("editor");
  const toolbar = window.document.getElementById("toolbar");
  const state = window.document.getElementById("state");

  // We export the innerHTML of our main components
  const output = `${editor?.innerHTML ?? ""}<!-- SPLIT -->${toolbar?.innerHTML ?? ""}<!-- SPLIT -->${state?.innerHTML ?? ""}`;
  await writeFile(outputPath, output, "utf8");
  
  process.exit(0);
} catch (error) {
  console.error("SSR Rendering failed:", error);
  process.exit(1);
}

function installBrowserGlobals(window) {
  const fallbackResizeObserver = class {
    observe() {}
    unobserve() {}
    disconnect() {}
  };

  const exposed = {
    window,
    document: window.document,
    navigator: window.navigator,
    location: window.location,
    history: window.history,
    localStorage: window.localStorage,
    sessionStorage: window.sessionStorage,
    Node: window.Node,
    Comment: window.Comment,
    Element: window.Element,
    HTMLElement: window.HTMLElement,
    HTMLAnchorElement: window.HTMLAnchorElement,
    HTMLBaseElement: window.HTMLBaseElement,
    HTMLButtonElement: window.HTMLButtonElement,
    HTMLCanvasElement: window.HTMLCanvasElement,
    HTMLDivElement: window.HTMLDivElement,
    HTMLFieldSetElement: window.HTMLFieldSetElement,
    HTMLFormElement: window.HTMLFormElement,
    HTMLHeadingElement: window.HTMLHeadingElement,
    HTMLHRElement: window.HTMLHRElement,
    HTMLImageElement: window.HTMLImageElement,
    HTMLInputElement: window.HTMLInputElement,
    HTMLLinkElement: window.HTMLLinkElement,
    HTMLMetaElement: window.HTMLMetaElement,
    HTMLOptionElement: window.HTMLOptionElement,
    HTMLScriptElement: window.HTMLScriptElement,
    HTMLSelectElement: window.HTMLSelectElement,
    HTMLSpanElement: window.HTMLSpanElement,
    Event: window.Event,
    EventTarget: window.EventTarget,
    KeyboardEvent: window.KeyboardEvent,
    MouseEvent: window.MouseEvent,
    PointerEvent: window.PointerEvent ?? window.MouseEvent,
    CustomEvent: window.CustomEvent,
    File: window.File,
    FileReader: window.FileReader,
    URL: window.URL,
    URLSearchParams: window.URLSearchParams,
    getComputedStyle: window.getComputedStyle.bind(window),
    requestAnimationFrame: window.requestAnimationFrame.bind(window),
    cancelAnimationFrame: window.cancelAnimationFrame.bind(window),
    ResizeObserver: window.ResizeObserver ?? fallbackResizeObserver,
    // Lexical often needs these:
    Selection: window.Selection,
    Range: window.Range,
    CharacterData: window.CharacterData,
    Text: window.Text,
    DocumentFragment: window.DocumentFragment,
    MutationObserver: window.MutationObserver,
  };

  for (const [name, value] of Object.entries(exposed)) {
    if (value !== undefined) {
      Object.defineProperty(globalThis, name, {
        value,
        configurable: true,
        writable: true,
      });
    }
  }

  window.ResizeObserver ??= fallbackResizeObserver;
}

async function waitForRender(window) {
  // Happy-DOM specific wait
  if (window.happyDOM?.whenAsyncComplete) {
    await window.happyDOM.whenAsyncComplete();
  }

  // Give it a bit more time for Scala.js initialization and Lexical updates
  await new Promise(resolve => setTimeout(resolve, 500));

  if (window.happyDOM?.whenAsyncComplete) {
    await window.happyDOM.whenAsyncComplete();
  }
}

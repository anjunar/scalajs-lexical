import { DecoratorNode, $getNodeByKey } from 'lexical';
import { EditorView } from '@codemirror/view';
import { EditorState } from '@codemirror/state';
import { javascript } from '@codemirror/lang-javascript';
import { python } from '@codemirror/lang-python';
import { java } from '@codemirror/lang-java';
import { cpp } from '@codemirror/lang-cpp';
import { rust } from '@codemirror/lang-rust';
import { sql } from '@codemirror/lang-sql';
import { html } from '@codemirror/lang-html';
import { css } from '@codemirror/lang-css';
import { markdown } from '@codemirror/lang-markdown';
import { json } from '@codemirror/lang-json';
import { xml } from '@codemirror/lang-xml';
import { yaml } from '@codemirror/lang-yaml';
import { php } from '@codemirror/lang-php';
import { StreamLanguage } from '@codemirror/language';
import { scala, kotlin, csharp, objectiveC, dart } from '@codemirror/legacy-modes/mode/clike';
import { shell } from '@codemirror/legacy-modes/mode/shell';
import { lua } from '@codemirror/legacy-modes/mode/lua';
import { haskell } from '@codemirror/legacy-modes/mode/haskell';
import { perl } from '@codemirror/legacy-modes/mode/perl';
import { r } from '@codemirror/legacy-modes/mode/r';
import { clojure } from '@codemirror/legacy-modes/mode/clojure';
import { erlang } from '@codemirror/legacy-modes/mode/erlang';
import { ruby } from '@codemirror/legacy-modes/mode/ruby';
import { swift } from '@codemirror/legacy-modes/mode/swift';
import { keymap } from '@codemirror/view';
import { defaultKeymap, indentWithTab } from '@codemirror/commands';
import { syntaxHighlighting, defaultHighlightStyle } from '@codemirror/language';

/**
 * A Map to track active CodeMirror instances by their Lexical node key.
 * This ensures we don't recreate the editor on every render, preserving focus and state.
 */
const activeEditors = new Map();

function getLanguageExtension(language) {
  switch (language) {
    case 'javascript':
    case 'typescript': // JS extension handles TS reasonably well for syntax
      return javascript();
    case 'python':
      return python();
    case 'java':
      return java();
    case 'cpp':
      return cpp();
    case 'rust':
      return rust();
    case 'sql':
      return sql();
    case 'html':
      return html();
    case 'css':
      return css();
    case 'markdown':
      return markdown();
    case 'json':
      return json();
    case 'xml':
      return xml();
    case 'yaml':
      return yaml();
    case 'php':
      return php();
    case 'scala':
      return StreamLanguage.define(scala);
    case 'kotlin':
      return StreamLanguage.define(kotlin);
    case 'csharp':
      return StreamLanguage.define(csharp);
    case 'shell':
      return StreamLanguage.define(shell);
    case 'lua':
      return StreamLanguage.define(lua);
    case 'haskell':
      return StreamLanguage.define(haskell);
    case 'perl':
      return StreamLanguage.define(perl);
    case 'r':
      return StreamLanguage.define(r);
    case 'clojure':
      return StreamLanguage.define(clojure);
    case 'erlang':
      return StreamLanguage.define(erlang);
    case 'ruby':
      return StreamLanguage.define(ruby);
    case 'swift':
      return StreamLanguage.define(swift);
    case 'objective-c':
      return StreamLanguage.define(objectiveC);
    case 'dart':
      return StreamLanguage.define(dart);
    default:
      return [];
  }
}

class CodeMirrorComponent {
  constructor(nodeKey, editor, initialCode, initialLanguage) {
    this.nodeKey = nodeKey;
    this.lexicalEditor = editor;
    this.code = initialCode;
    this.language = initialLanguage;
    this.dom = document.createElement('div');
    this.dom.className = 'codemirror-wrapper';
    
    this.initView();
  }

  initView() {
    const extensions = [
      keymap.of([indentWithTab, ...defaultKeymap]),
      syntaxHighlighting(defaultHighlightStyle),
      EditorView.lineWrapping,
      getLanguageExtension(this.language),
      EditorView.updateListener.of((update) => {
        if (update.docChanged) {
          const newCode = update.state.doc.toString();
          if (newCode !== this.code) {
            this.code = newCode;
            // Update the Lexical node when the code changes
            this.lexicalEditor.update(() => {
              const node = $getNodeByKey(this.nodeKey);
              if (node && $isCodeMirrorNode(node)) {
                node.setCode(newCode);
              }
            });
          }
        }
      }),
    ];

    const state = EditorState.create({
      doc: this.code,
      extensions,
    });

    this.view = new EditorView({
      state,
      parent: this.dom,
    });
  }

  update(newCode, newLanguage) {
    let needsReinit = false;
    if (this.language !== newLanguage) {
      this.language = newLanguage;
      needsReinit = true;
    }
    
    if (needsReinit) {
      this.view.destroy();
      this.initView();
    } else if (this.code !== newCode && this.view.state.doc.toString() !== newCode) {
      this.code = newCode;
      // Sync Lexical -> CodeMirror
      this.view.dispatch({
        changes: {from: 0, to: this.view.state.doc.length, insert: newCode}
      });
    }
  }

  destroy() {
    if (this.view) {
      this.view.destroy();
    }
    this.dom.remove();
  }
}

export class CodeMirrorNode extends DecoratorNode {
  static getType() {
    return 'codemirror';
  }

  static clone(node) {
    return new CodeMirrorNode(node.__code, node.__language, node.__key);
  }

  static importJSON(serializedNode) {
    const node = $createCodeMirrorNode(serializedNode.code, serializedNode.language);
    return node;
  }

  exportJSON() {
    return {
      code: this.__code,
      language: this.__language,
      type: 'codemirror',
      version: 1,
    };
  }

  constructor(code = '', language = 'javascript', key) {
    super(key);
    this.__code = code;
    this.__language = language;
  }

  getCode() {
    return this.__code;
  }

  setCode(code) {
    const writable = this.getWritable();
    writable.__code = code;
  }

  getLanguage() {
    return this.__language;
  }

  setLanguage(language) {
    const writable = this.getWritable();
    writable.__language = language;
  }

  getTextContent() {
    return this.__code;
  }

  createDOM(editorConfig, editor) {
    const dom = document.createElement('div');
    dom.className = 'codemirror-container';
    return dom;
  }

  updateDOM(prevNode, dom, config) {
    return false; // The decorator listener handles updates
  }

  decorate(editor, config) {
    const key = this.getKey();
    let cmComponent = activeEditors.get(key);
    if (!cmComponent) {
      cmComponent = new CodeMirrorComponent(key, editor, this.__code, this.__language);
      activeEditors.set(key, cmComponent);
      // Focus when created
      setTimeout(() => cmComponent.view.focus(), 0);
    } else {
      cmComponent.update(this.__code, this.__language);
    }
    return cmComponent.dom;
  }
}

export function $createCodeMirrorNode(code = '', language = 'javascript') {
  return new CodeMirrorNode(code, language);
}

export function $isCodeMirrorNode(node) {
  return node instanceof CodeMirrorNode;
}

/**
 * Cleans up the CodeMirror instance when the node is destroyed in Lexical.
 */
export function handleCodeMirrorMutation(nodeKey, mutation, editor) {
  if (mutation === 'destroyed') {
    const cmComponent = activeEditors.get(nodeKey);
    if (cmComponent) {
      cmComponent.destroy();
      activeEditors.delete(nodeKey);
    }
  }
}

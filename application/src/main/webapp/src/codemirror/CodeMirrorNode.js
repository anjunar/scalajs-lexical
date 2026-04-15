import { DecoratorNode, $getNodeByKey } from 'lexical';
import { EditorView } from '@codemirror/view';
import { EditorState } from '@codemirror/state';
import { javascript } from '@codemirror/lang-javascript';
import { python } from '@codemirror/lang-python';
import { keymap } from '@codemirror/view';
import { defaultKeymap, indentWithTab } from '@codemirror/commands';
import { syntaxHighlighting, defaultHighlightStyle } from '@codemirror/language';

/**
 * A Map to track active CodeMirror instances by their Lexical node key.
 * This ensures we don't recreate the editor on every render, preserving focus and state.
 */
const activeEditors = new Map();

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

    if (this.language === 'javascript') {
      extensions.push(javascript());
    } else if (this.language === 'python') {
      extensions.push(python());
    }

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

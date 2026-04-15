import { DecoratorNode } from 'lexical';
import { EditorView } from '@codemirror/view';
import { EditorState } from '@codemirror/state';
import { javascript } from '@codemirror/lang-javascript';
import { python } from '@codemirror/lang-python';
import { oneDark } from '@codemirror/theme-one-dark';
import { keymap } from '@codemirror/view';
import { defaultKeymap, indentWithTab } from '@codemirror/commands';
import { syntaxHighlighting, defaultHighlightStyle } from '@codemirror/language';

export class CodeMirrorNode extends DecoratorNode {
  static getType() {
    return 'codemirror';
  }

  static clone(node) {
    return new CodeMirrorNode(node.__code, node.__language);
  }

  constructor(code = '', language = 'javascript') {
    super();
    this.__code = code;
    this.__language = language;
  }

  getCode() {
    return this.__code;
  }

  setCode(code) {
    this.__code = code;
  }

  getLanguage() {
    return this.__language;
  }

  setLanguage(language) {
    this.__language = language;
  }

  getTextContent() {
    return this.__code;
  }

  createDOM(editorConfig, theme) {
    const dom = document.createElement('div');
    dom.className = 'codemirror-wrapper';
    return dom;
  }

  decorate(editor) {
    const dom = document.createElement('div');
    dom.className = 'codemirror-wrapper';

    const extensions = [
      keymap.of([indentWithTab, ...defaultKeymap]),
      syntaxHighlighting(defaultHighlightStyle),
      EditorView.lineWrapping,
      EditorView.updateListener.of((update) => {
        if (update.docChanged) {
          const newCode = update.state.doc.toString();
          editor.update(() => {
            this.setCode(newCode);
          });
        }
      }),
    ];

    if (this.__language === 'javascript') {
      extensions.push(javascript());
    } else if (this.__language === 'python') {
      extensions.push(python());
    }

    extensions.push(oneDark);

    const state = EditorState.create({
      doc: this.__code,
      extensions,
    });

    const view = new EditorView({
      state,
      parent: dom,
    });

    this.__view = view;

    return dom;
  }

  updateDOM(prevNode, dom) {
    if (prevNode.__language !== this.__language) {
      return true;
    }
    return false;
  }
}

export function $createCodeMirrorNode(code = '', language = 'javascript') {
  return new CodeMirrorNode(code, language);
}

window.CodeMirrorNode = CodeMirrorNode;
window.$createCodeMirrorNode = $createCodeMirrorNode;

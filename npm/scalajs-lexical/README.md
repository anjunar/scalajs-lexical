# @anjunar/scalajs-lexical

NPM companion package for the Maven artifact `com.anjunar::scalajs-lexical`.

This package provides:

- Lexical runtime packages used by the Scala.js facades
- CodeMirror runtime packages used by the optional code block integration
- the default `scalajs-lexical` CSS

Install it next to the Scala.js dependency:

```bash
npm install @anjunar/scalajs-lexical
```

Import the shared Anjunar UI grammar once in your application stylesheet, then import the Lexical package CSS from your Vite entrypoint:

```css
@import "tailwindcss";
@import "@anjunar/ui";
```

```javascript
import '@anjunar/scalajs-lexical/index.css'
import 'scalajs:main.js'
```

Or import the package root if you want the default CSS side effect:

```javascript
import '@anjunar/scalajs-lexical'
```

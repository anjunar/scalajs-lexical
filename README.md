# Scala.js Lexical

A Scala.js wrapper for the [Lexical](https://lexical.dev/) editor, providing a type-safe and idiomatic way to build rich text editors in Scala.

## Installation

Add the following to your `build.sbt`:

```scala
libraryDependencies += "com.anjunar" %%% "lexical-library" % "1.0.0"
// Optional: CodeMirror node support
libraryDependencies += "com.anjunar" %%% "lexical-codemirror" % "1.0.0"
```

## Features

- **Type-safe API**: Build Lexical editors using Scala classes and traits.
- **Idiomatic Builder**: Use `LexicalBuilder` to configure your editor with nodes, modules, and toolbars.
- **Ribbon Toolbar**: A modern, structured toolbar system out of the box.
- **CodeMirror Integration**: Seamlessly embed CodeMirror 6 blocks within your Lexical editor.
- **Floating Toolbar**: Context-aware floating toolbar for quick formatting.

## Showcase: Manifesto of Silence

The included demo application demonstrates the power of `scalajs-lexical` through a sophisticated "Manifesto of Silence" theme, featuring:
- Custom typography (Crimson Pro & Inter)
- Archival metadata view (JSON state resonance)
- Process-oriented sidebar navigation

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

package lexical

import scala.scalajs.js

trait EditorTheme extends js.Object:
  var ltr: js.UndefOr[String] = js.undefined
  var rtl: js.UndefOr[String] = js.undefined
  var placeholder: js.UndefOr[String] = js.undefined
  var paragraph: js.UndefOr[String] = js.undefined
  var quote: js.UndefOr[String] = js.undefined
  var heading: js.UndefOr[js.Dictionary[String]] = js.undefined
  var list: js.UndefOr[js.Dictionary[String]] = js.undefined
  var text: js.UndefOr[EditorTextTheme] = js.undefined
  var code: js.UndefOr[String] = js.undefined
  var codeHighlight: js.UndefOr[js.Dictionary[String]] = js.undefined
  var link: js.UndefOr[String] = js.undefined
  var image: js.UndefOr[String] = js.undefined

trait EditorTextTheme extends js.Object:
  var base: js.UndefOr[String] = js.undefined
  var bold: js.UndefOr[String] = js.undefined
  var italic: js.UndefOr[String] = js.undefined
  var underline: js.UndefOr[String] = js.undefined
  var strikethrough: js.UndefOr[String] = js.undefined
  var underlineStrikethrough: js.UndefOr[String] = js.undefined
  var code: js.UndefOr[String] = js.undefined

class EditorThemeBuilder:
  private val _theme = js.Dynamic.literal().asInstanceOf[EditorTheme]
  private val _textTheme = js.Dynamic.literal().asInstanceOf[EditorTextTheme]

  def withParagraph(className: String): this.type =
    _theme.paragraph = className
    this

  def withQuote(className: String): this.type =
    _theme.quote = className
    this

  def withHeading(level: Int, className: String): this.type =
    val headings = _theme.heading.getOrElse(js.Dictionary.empty[String])
    headings(s"h$level") = className
    _theme.heading = headings
    this

  def withList(listType: String, className: String): this.type =
    val lists = _theme.list.getOrElse(js.Dictionary.empty[String])
    lists(listType) = className
    _theme.list = lists
    this

  def withTextBase(className: String): this.type =
    _textTheme.base = className
    _theme.text = _textTheme
    this

  def withTextBold(className: String): this.type =
    _textTheme.bold = className
    _theme.text = _textTheme
    this

  def withTextItalic(className: String): this.type =
    _textTheme.italic = className
    _theme.text = _textTheme
    this

  def withTextUnderline(className: String): this.type =
    _textTheme.underline = className
    _theme.text = _textTheme
    this

  def withTextStrikethrough(className: String): this.type =
    _textTheme.strikethrough = className
    _theme.text = _textTheme
    this

  def withCode(className: String): this.type =
    _theme.code = className
    this

  def build(): EditorTheme = _theme

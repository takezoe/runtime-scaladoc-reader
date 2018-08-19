package com.github.takezoe.scaladoc

object JsonUtils {

  def serialize(results: Seq[WithComment]): String = {
    (results.map {
      case x: ClassWithComment  => serializeClass(x)
      case x: MethodWithComment => serializeMethod(x)
      case x: FieldWithComment  => serializeField(x)
    }).mkString("[", ",", "]")
  }

  private def serializeClass(x: ClassWithComment): String = {
    """{"name": "%s", "comment": "%s", "members": %s}""".format(escape(x.name), escape(x.comment.getOrElse("")), serialize(x.members))
  }

  private def serializeMethod(x: MethodWithComment): String = {
    """{"name": "%s", "comment": "%s"}""".format(escape(x.name), escape(x.comment.getOrElse("")))
  }

  private def serializeField(x: FieldWithComment): String = {
    """{"name": "%s", "comment": "%s"}""".format(escape(x.name), escape(x.comment.getOrElse("")))
  }

  private def escape(s: String): String = s
    .replaceAll("\"",   "\\\"")
    .replaceAll("\r",   "\n")
    .replaceAll("\r\n", "\n")
    .replaceAll("\n",   "\\\\n")

}

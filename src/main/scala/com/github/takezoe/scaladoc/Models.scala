package com.github.takezoe.scaladoc

sealed trait WithComment {
  val name: String
  val comment: Option[String]
}

case class ClassWithComment(name: String, comment: Option[String], members: Seq[WithComment]) extends WithComment
case class MethodWithComment(name: String, comment: Option[String]) extends WithComment
case class FieldWithComment(name: String, comment: Option[String]) extends WithComment

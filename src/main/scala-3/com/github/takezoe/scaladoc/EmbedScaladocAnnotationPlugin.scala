package com.github.takezoe.scaladoc

import dotty.tools.dotc.plugins.{StandardPlugin, PluginPhase}
import dotty.tools.dotc.ast.tpd.*
import dotty.tools.dotc.core.Contexts.Context
import dotty.tools.dotc.core.Constants.Constant
import dotty.tools.dotc.core.Symbols.requiredClass
import dotty.tools.dotc.core.StdNames.nme
import dotty.tools.dotc.core.Comments.{Comment, docCtx}
import dotty.tools.dotc.core.Annotations.Annotation

import dotty.tools.dotc.util.Spans

class EmbedScaladocAnnotationPlugin extends StandardPlugin:
  override val name: String = "EmbedScaladocAnnotation"
  override val description: String = "Embeds Scaladoc comments as runtime annotations"

  def init(options: List[String]): List[PluginPhase] =
    List(EmbedScaladocAnnotationPhase())

class EmbedScaladocAnnotationPhase extends PluginPhase:
  val phaseName = "EmbedScaladocAnnotation"
  override val runsAfter = Set("typer")
  override val runsBefore = Set("checkUnusedPostTyper")

  private def addScaladocAnnotation(tree: DefTree)(using ctx: Context): Tree =
    ctx.docCtx.getOrElse:
      throw RuntimeException("Internal error: DocCtx could not be found and documentations are unavailable. Please report this issue to the maintainers.")
    .docstring(tree.symbol).foreach:
      case Comment(span, comment, _, _, _) =>
        val annotationSymbol = requiredClass("com.github.takezoe.scaladoc.Scaladoc")
        tree.symbol.addAnnotation(Annotation(annotationSymbol, List(Literal(Constant(comment))), span))
    tree

  override def transformValDef(tree: ValDef)(using Context): Tree =
    addScaladocAnnotation(tree)

  override def transformDefDef(tree: DefDef)(using Context): Tree =
    addScaladocAnnotation(tree)

  override def transformTypeDef(tree: TypeDef)(using Context): Tree =
    if tree.isClassDef then addScaladocAnnotation(tree) else tree

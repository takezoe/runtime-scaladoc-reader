package com.github.takezoe.scaladoc

import scala.tools.nsc
import nsc.{Global, Phase}
import nsc.plugins.Plugin
import nsc.plugins.PluginComponent
import scala.collection.mutable.ListBuffer
import scala.tools.nsc.doc.ScaladocSyntaxAnalyzer
import scala.tools.nsc.transform.Transform

class EmbedScaladocAnnotationPlugin(val global: Global) extends Plugin {
  override val name: String = "EmbedScaladocAnnotation"
  override val description: String = ""
  override val components: List[PluginComponent] = List[PluginComponent](MyComponent)

  private object MyComponent extends PluginComponent with Transform {
    type GT = EmbedScaladocAnnotationPlugin.this.global.type
    override val global: GT = EmbedScaladocAnnotationPlugin.this.global
    override val phaseName: String = "EmbedScaladocAnnotation"
    override val runsAfter: List[String] = List("parser")
    override def newTransformer(unit: global.CompilationUnit): global.Transformer = new ScaladocTransformer
    import global._


    class ScaladocTransformer extends global.Transformer {

      val comments = new Comments()

      override def transformUnit(unit: CompilationUnit)= {
        if(unit.source.file.name.endsWith(".scala")){
          comments.parseComments(unit)
          super.transformUnit(unit)
        }
      }

      @Scaladoc("asdkaopdkpakpoakspo")
      override def transform(tree: global.Tree): global.Tree = {
        tree match {
          case x @ PackageDef(_, _) => {
            x.copy(x.pid, List(insertImport) ++ x.stats.map(transform))
          }
          case x @ ClassDef(_, _, _, _) => {
            comments.getComment(x.pos) match {
              case Some(comment) =>
                val newAnnotations = createAnnotation(comment) :: x.mods.annotations
                val newMods = x.mods.copy(annotations = newAnnotations)
                val newBody = x.impl.body.map(transform)
                val newImpl = global.treeCopy.Template(x.impl, x.impl.parents, x.impl.self, newBody)
                global.treeCopy.ClassDef(tree, newMods, x.name, x.tparams, newImpl)
              case None =>
                tree
            }
          }
          case x @ DefDef(_, _, _, _, _, _) => {
            comments.getComment(x.pos) match {
              case Some(comment) =>
                val newAnnotations = createAnnotation(comment) :: x.mods.annotations
                val newMods = x.mods.copy(annotations = newAnnotations)
                global.treeCopy.DefDef(tree, newMods, x.name, x.tparams, x.vparamss, x.tpt, x.rhs)
              case None =>
                tree
            }
          }
          case x @ ValDef(_, _, _, _) => {
            comments.getComment(x.pos) match {
              case Some(comment) =>
                val newAnnotations = createAnnotation(comment) :: x.mods.annotations
                val newMods = x.mods.copy(annotations = newAnnotations)
                global.treeCopy.ValDef(tree, newMods, x.name, x.tpt, x.rhs)
              case None =>
                tree
            }
          }
          case x => super.transform(tree)
        }
      }

      private def createAnnotation(comment: String): global.Tree =
        global.Apply(
          global.Select(global.New(global.Ident(global.newTypeName("Scaladoc"))),
          global.nme.CONSTRUCTOR),
          List(Literal(Constant(comment))))

      def insertImport: global.Tree = {
        val importSelectors = global.ImportSelector(
          global.newTermName("Scaladoc"), -1, global.newTermName("Scaladoc"), -1)

        global.Import(
          global.Select(global.Select(global.Select(global.Ident(
            global.newTermName("com")),
            global.newTermName("github")),
            global.newTermName("takezoe")),
            global.newTermName("scaladoc")), List(importSelectors))
      }
    }

    class Comments extends ScaladocSyntaxAnalyzer[global.type](global){
      val comments = ListBuffer[(Position, String)]()

      def getComment(pos: Position): Option[String] = {
        val tookComments = comments.takeWhile { case (x, _) => x.end < pos.start }
        comments --= (tookComments)
        tookComments.lastOption.map(_._2)
      }

      def parseComments(unit: CompilationUnit): Unit = {
        comments.clear()

        new ScaladocUnitParser(unit, Nil) {
          override def newScanner = new ScaladocUnitScanner(unit, Nil) {
            override def registerDocComment(str: String, pos: Position) = {
              comments += ((pos, str))
            }
          }
        }.parse()
      }

      override val runsAfter: List[String] = Nil
      override val runsRightAfter: Option[String] = None
    }
  }
}

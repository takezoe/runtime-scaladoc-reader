package com.github.takezoe.scaladoc

import scala.tools.nsc
import nsc.{Global, Phase}
import nsc.plugins.Plugin
import nsc.plugins.PluginComponent
import scala.collection.mutable.ListBuffer
import scala.tools.nsc.doc.ScaladocSyntaxAnalyzer
import scala.tools.nsc.transform.Transform

class ReadScaladocPlugin(val global: Global) extends Plugin {
  override val name: String = "readscaladoc"
  override val description: String = ""
  override val components: List[PluginComponent] = List[PluginComponent](MyComponent)

  private object MyComponent extends PluginComponent with Transform {
    type GT = ReadScaladocPlugin.this.global.type
    override val global: GT = ReadScaladocPlugin.this.global
    override val phaseName: String = "ReadScaladoc"
    override val runsAfter: List[String] = List("parser")
    override def newTransformer(unit: global.CompilationUnit): global.Transformer = new ScaladocTransformer
    import global._


    class ScaladocTransformer extends global.Transformer {

      private def getComment(comments: ListBuffer[(Position, String)], pos: Position): Option[String] = {
        val tookComments = comments.takeWhile { case (x, _) => x.end < pos.start }
        comments --= (tookComments)
        tookComments.lastOption.map(_._2)
      }

      val comments = new Comments()

      override def transformUnit(unit: CompilationUnit)= {
        if(unit.source.file.name.endsWith(".scala")){
          comments.parseComments(unit)
          println(unit.getClass)
          super.transformUnit(unit)
        }
      }

      @Scaladoc("asdkaopdkpakpoakspo")
      override def transform(tree: global.Tree): global.Tree = {
        tree match {
          case x @ PackageDef(pid, stats) => {
            x.copy(x.pid, x.stats :+ insertImport)
          }
          case x @ ClassDef(_, _, _, _) => {
            val newAnnotations = createAnnotation("Scaladoc") :: x.mods.annotations
            val newMods = x.mods.copy(annotations = newAnnotations)

//            val comment = getComment(comments, x.pos)
//            val members = traverse(packageName, x.impl.body, comments)
//            val className = (if(packageName.isEmpty) x.name.toString else packageName + "." + x.name.toString)


            val newBody = x.impl.body.map(transform)
            val newImpl = global.treeCopy.Template(x.impl, x.impl.parents, x.impl.self, newBody)
            global.treeCopy.ClassDef(tree, newMods, x.name, x.tparams, newImpl)
          }
          case x @ DefDef(_, _, _, _, _, _) => {
            val newAnnotations = createAnnotation("com.github.takezoe.scaladoc.Scaladoc") :: x.mods.annotations
            val newMods = x.mods.copy(annotations = newAnnotations)
            global.treeCopy.DefDef(tree, newMods, x.name, x.tparams, x.vparamss, x.tpt, x.rhs)
            tree
          }
          case x @ ValDef(_, _, _, _) => {
            tree
          }
          case x => super.transform(tree)
        }
      }

      private def createAnnotation(annotationName: String): global.Tree = global.Apply(
        global.Select(
          global.New(global.Ident(global.newTypeName(annotationName))),
          global.nme.CONSTRUCTOR), Nil)

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

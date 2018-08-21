package com.github.takezoe.scaladoc

import java.io.{File, FileOutputStream}
import java.nio.charset.StandardCharsets

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
    override val runsAfter: List[String] = List("fields")
    //override def newPhase(prev: Phase): Phase = new ReadScaladocPhase(prev)
    override def newTransformer(unit: global.CompilationUnit): global.Transformer = new ScaladocTransformer
    import global._


    class ScaladocTransformer extends global.Transformer {

      private def getComment(comments: ListBuffer[(Position, String)], pos: Position): Option[String] = {
        val tookComments = comments.takeWhile { case (x, _) => x.end < pos.start }
        comments --= (tookComments)
        tookComments.lastOption.map(_._2)
      }

      val comments = new Comments()

      override def transformUnit(unit: CompilationUnit) {
        comments.parseComments(unit)
        super.transformUnit(unit)
      }

      override def transform(tree: global.Tree): global.Tree = {
        tree match {
//          case x @ PackageDef(pid, stats) => {
//            traverse(x.pid.qualifier.toString, x.children, comments)
//          }
          case x @ ClassDef(_, _, _, _) => {
            val newAnnotations = createAnnotation("BooleanBeanProperty") :: x.mods.annotations
            val newMods = x.mods.copy(annotations = newAnnotations)

//            val comment = getComment(comments, x.pos)
//            val members = traverse(packageName, x.impl.body, comments)
//            val className = (if(packageName.isEmpty) x.name.toString else packageName + "." + x.name.toString)
            global.treeCopy.ClassDef(tree, newMods, x.name, x.tparams, x.impl)
            tree
            //Seq(ClassWithComment(className, comment, members))
          }
          case x @ DefDef(_, _, _, _, _, _) => {
            tree
            //Seq(MethodWithComment(x.name.toString, getComment(comments, x.pos)))
          }
          case x @ ValDef(_, _, _, _) => {
            tree
            //Seq(FieldWithComment(x.name.toString, getComment(comments, x.pos)))
          }
          case x => super.transform(tree)
        }
      }

      private def createAnnotation(annotationName: String): global.Tree = global.Apply(
        global.Select(
          global.New(global.Ident(global.newTypeName(annotationName))),
          global.nme.CONSTRUCTOR), Nil)
    }

//    class ReadScaladocPhase(prev: Phase) extends StdPhase(prev) {
//
//      private def traverse(packageName: String, trees: List[Tree], comments: ListBuffer[(Position, String)]): Seq[WithComment] = {
//        trees.flatMap { tree =>
//          tree match {
//            case x @ PackageDef(pid, stats) => {
//              traverse(x.pid.qualifier.toString, x.children, comments)
//            }
//            case x @ ClassDef(_, _, _, _) => {
//              val comment = getComment(comments, x.pos)
//              val members = traverse(packageName, x.impl.body, comments)
//              val className = (if(packageName.isEmpty) x.name.toString else packageName + "." + x.name.toString)
//              Seq(ClassWithComment(className, comment, members))
//            }
//            case x @ DefDef(_, _, _, _, _, _) => {
//              Seq(MethodWithComment(x.name.toString, getComment(comments, x.pos)))
//            }
//            case x @ ValDef(_, _, _, _) => {
//              Seq(FieldWithComment(x.name.toString, getComment(comments, x.pos)))
//            }
//            case x => traverse(packageName, x.children, comments)
//          }
//        }
//      }
//
//      def outputBase = new File("src/main/resources")
//
//      override def apply(unit: CompilationUnit): Unit = {
//        val comments = new Comments()
//        comments.parseComments(unit)
//
//        val results = traverse("", List(unit.body), comments.comments)
//
//        val file = new File(outputBase, unit.source.file.name.replaceFirst("\\.scala$", "") + "-comments.json")
//        val out = new FileOutputStream(file)
//        out.write(JsonUtils.serialize(results).getBytes(StandardCharsets.UTF_8))
//        out.close()
//      }
//
//      private def getComment(comments: ListBuffer[(Position, String)], pos: Position): Option[String] = {
//        val tookComments = comments.takeWhile { case (x, _) => x.end < pos.start }
//        comments --= (tookComments)
//        tookComments.lastOption.map(_._2)
//      }
//    }

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

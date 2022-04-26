import HelloWorld.InnerObject
import org.scalatest.funsuite.AnyFunSuite
import com.github.takezoe.scaladoc.Scaladoc

class SetSuite extends AnyFunSuite {

  test("class scaladoc") {
    val clazz = classOf[HelloWorld]
    val scaladoc = clazz.getAnnotation(classOf[Scaladoc])
    val comment: String = scaladoc.value()
    assert(comment == """/**
                        |  * Hello, World!
                        |  */""".stripMargin)
  }

  test("object scaladoc") {
    val clazz = HelloWorld.getClass
    val scaladoc = clazz.getAnnotation(classOf[Scaladoc])
    val comment: String = scaladoc.value()
    assert(comment == """/**
                        |  * Hello, Companion!
                        |  */""".stripMargin)
  }

  test("field scaladoc") {
    val clazz = classOf[HelloWorld]
    val field = clazz.getDeclaredField("field")
    val scaladoc = field.getAnnotation(classOf[Scaladoc])
    val comment: String = scaladoc.value()
    assert(comment == """/**
                        |    * field
                        |    */""".stripMargin)
  }

  test("method scaladoc") {
    val clazz = classOf[HelloWorld]
    val method = clazz.getDeclaredMethod("method")
    val scaladoc = method.getAnnotation(classOf[Scaladoc])
    val comment: String = scaladoc.value()
    assert(comment == """/**
                        |    * method
                        |    */""".stripMargin)
  }

  test("inner class scaladoc") {
    val clazz = classOf[HelloWorld.InnerClass]
    val scaladoc = clazz.getAnnotation(classOf[Scaladoc])
    val comment: String = scaladoc.value()
    assert(comment == """/** Inner class comment */""".stripMargin)
  }

  test("inner object scaladoc") {
    val clazz = HelloWorld.InnerObject.getClass
    val scaladoc = clazz.getAnnotation(classOf[Scaladoc])
    val comment: String = scaladoc.value()
    assert(comment == """/** Inner object comment */""".stripMargin)
  }

}

/**
  * Hello, World!
  */
class HelloWorld {

  /**
    * field
    */
  val field: String = ""

  /**
    * method
    */
  def method(): Unit = {
  }

}

/**
  * Hello, Companion!
  */
object HelloWorld {
  /** Inner class comment */
  class InnerClass()

  /** Inner object comment */
  case object InnerObject {}
}

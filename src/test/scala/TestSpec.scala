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

  test("field scaladoc") {
    val clazz = classOf[HelloWorld]
    val field = clazz.getDeclaredField("field")
    val scaladoc = field.getAnnotation(classOf[Scaladoc])
    val comment: String = scaladoc.value()
    println(comment)
    assert(comment == """/**
                        |    * field
                        |    */""".stripMargin)
  }

  test("method scaladoc") {
    val clazz = classOf[HelloWorld]
    val method = clazz.getDeclaredMethod("method")
    val scaladoc = method.getAnnotation(classOf[Scaladoc])
    val comment: String = scaladoc.value()
    println(comment)
    assert(comment == """/**
                        |    * method
                        |    */""".stripMargin)
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

# read-scaladoc-plugin

A Scala compiler plugin to embed Scaladoc as annotations to allow to read Scaladoc using reflection at runtime.

## Setup

This compiler plugins hasn't been released yet, so you need to publish this plugin to your local repository by yourself.

```
$ git clone https://github.com/takezoe/read-scaladoc-plugin.git
$ cd read-scaladoc-plugin
$ sbt publishLocal
```

Then add following configuration to your `build.sbt`:

```scala
libraryDependencies += "com.github.takezoe" %% "read-scaladoc-plugin" % "0.0.1-SNAPSHOT"

addCompilerPlugin("com.github.takezoe" %% "read-scaladoc-plugin" % "0.0.1-SNAPSHOT")
```

## Usage

Assuming you have a below class which has Scaladoc:

```scala
package com.github.takezoe

/**
 * Hello, World!
 */
class HelloWorld {
  ...
}
```

You can get Scaladoc at runtime as follows:

```scala
import com.github.takezoe.HelloWorld
import com.github.takezoe.scaladoc.Scaladoc

val clazz = classOf[HelloWorld]
val scaladoc = clazz.getAnnotation(classOf[Scaladoc])

if(scaladoc != null){
  val comment: String = scaladoc.value()
  println(comment)
}
```

You can also get Scaladoc from `Method` and `Field` as same as `Class`.
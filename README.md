# runtime-scaladoc-reader [![Build Status](https://travis-ci.org/takezoe/runtime-scaladoc-reader.svg?branch=master)](https://travis-ci.org/takezoe/runtime-scaladoc-reader) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/takezoe/runtime-scaladoc-reader/blob/master/LICENSE)

Allows to read Scaladoc at runtime by embedding as annotation by the compiler plugin.

## Setup

Add following configuration to your `build.sbt`:

```scala
libraryDependencies += "com.github.takezoe" %% "runtime-scaladoc-reader" % "1.0.1"

addCompilerPlugin("com.github.takezoe" %% "runtime-scaladoc-reader" % "1.0.1")
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

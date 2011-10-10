import sbt._
import Keys._

/*
object Shared {
    val buildOrganization = "com.github.gseitz.lensed"
    val buildVersion = "0.5"
    val buildScalaVersion = "2.9.1"

    val publishTo = Option(Resolver.file("gitpages-local", Path.userHome / "public-repos"))

    val buildSettings = Defaults.defaultSettings ++ Seq(
      organization := buildOrganization,
      version      := buildVersion,
      scalaVersion := buildScalaVersion,
      crossScalaVersions := Seq("2.9.0-1", "2.9.1"),
      publishTo := Some("Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/releases/")
//      publishTo    := Some("Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/releases/")
//      publishTo <<= publishTo
//      publishTo    := Option(Resolver.file("gitpages-local", Path.userHome / "public-repos"))
    )
}*/

object LensedBuild extends Build {

  object BuildSettings {
    val buildOrganization = "com.github.gseitz.lensed"
    val buildVersion = "0.5"
    val buildScalaVersion = "2.9.1"

//    val publishTo = Option(Resolver.file("gitpages-local", Path.userHome / "public-repos"))

    val buildSettings = Defaults.defaultSettings ++ Seq(
      organization := buildOrganization,
      version      := buildVersion,
      scalaVersion := buildScalaVersion,
      publishTo := Option(Resolver.file("gitpages-local", Path.userHome / "public-repos")),
      crossScalaVersions := Seq("2.9.0-1", "2.9.1")
    )
//      publishTo := Some("Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/releases/")
//      publishTo    := Some("Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/releases/")
//      publishTo <<= publishTo
//      publishTo    := Option(Resolver.file("gitpages-local", Path.userHome / "public-repos"))

  }

  object Dependencies {
    def scalaz   = "org.scalaz" %% "scalaz-core" % "6.0.2"
    def scalac   = "org.scala-lang" % "scala-compiler"
    def scalalib = "org.scala-lang" % "scala-library"
  }

  import BuildSettings.buildSettings
  import Dependencies._

  lazy val root = Project(
    id = "lensed",
    base = file("."),
    settings = buildSettings
  ) aggregate(annotation, plugin)

  lazy val annotation = Project(
    id = "annotation",
    base = file("annotation"),
    settings = buildSettings
  )

  lazy val plugin = Project(
    id = "plugin",
    base = file("plugin"),
    settings = buildSettings ++ Seq[Setting[_]](
      libraryDependencies += scalaz,
      libraryDependencies <++= scalaVersion { sv =>
        scalac % sv ::
        scalalib % sv ::
        Nil
      }
    )
  ) dependsOn (annotation)

    /*
  lazy val examples = Project(
    id = "examples",
    base = file("examples")
  ) aggregate(testCaseClasses, usage)
  */
//  val pluginArtifact =

  lazy val testCaseClasses = Project(
    id = "testCaseClasses",
    base  = file("examples/simple"),
    settings = buildSettings ++ Seq[Setting[_]](
      libraryDependencies += scalaz,
      scalacOptions <+= (packagedArtifact in Compile in plugin in packageBin) map (art => "-Xplugin:%s" format art._2.getAbsolutePath),
      scalacOptions += "-Xplugin-require:lensed"
    )
  ) dependsOn (plugin)

  lazy val usage = Project(
    id = "usage",
    base = file("examples/usage"),
    settings = buildSettings ++ Seq[Setting[_]](
      libraryDependencies += scalaz
    )
  ) dependsOn (testCaseClasses)



}

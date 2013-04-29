import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "movierecommender"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaEbean,
    "org.apache.mahout" % "mahout-core" % "0.6",
    "jfree" % "jfreechart" % "1.0.9"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here   
  )

}

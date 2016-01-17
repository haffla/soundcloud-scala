name := "Soundcloud Scala"

organization := "com.github.haffla"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.7"

resolvers += "Typesafe Repo" at "http://dl.bintray.com/typesafe/maven-releases"

libraryDependencies ++= Seq(
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.3",
  "com.typesafe.play" %% "play-json" % "2.3.0",
  "net.liftweb" %% "lift-json" % "[2.5+,2.6+]" % "test",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra :=
  <url>https://github.com/haffla/soundcloud-scala</url>
    <licenses>
      <license>
        <name>BSD-style</name>
        <url>http://www.opensource.org/licenses/bsd-license.php</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:haffla/soundcloud-scala.git</url>
      <connection>scm:git@github.com:haffla/soundcloud-scala.git</connection>
    </scm>
    <developers>
      <developer>
        <id>haffla</id>
        <name>Jakob Pupke</name>
        <url>https://github.com/haffla</url>
      </developer>
    </developers>
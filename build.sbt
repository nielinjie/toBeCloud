organization := "nielinjie"

name := "toBeCloud"

version := "0.1-SNAPSHOT"

resolvers += ScalaToolsSnapshots

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq(
	"org.scalaz" %% "scalaz-core" % "6.0.3",
	"org.apache.mina" % "mina-core" % "2.0.4",
	"commons-net" % "commons-net" % "20030805.205232",
	"nielinjie" %%  "util.io" % "1.0",
	"nielinjie" %%  "util.data" % "1.0"
)

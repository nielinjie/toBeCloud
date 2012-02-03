organization := "nielinjie"

name := "toBeCloud"

version := "0.1-SNAPSHOT"

resolvers += ScalaToolsSnapshots

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq(
	"org.scalaz" %% "scalaz-core" % "6.0.3",
	"net.databinder" %% "unfiltered-filter" % "0.5.3",
	"net.databinder" %% "unfiltered-jetty" % "0.5.3",
	"net.databinder" %% "unfiltered-json" % "0.5.3",
	"net.databinder" %% "dispatch-http" % "0.8.7",
	"nielinjie" %%  "util.io" % "1.0",
	"nielinjie" %%  "util.data" % "1.0"
)

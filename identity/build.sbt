resolvers += "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases"

addCommandAlias("idrun", ";run 9009")

testOptions in Test += Tests.Argument("-oF")

libraryDependencies ++= Seq("com.typesafe.play" %% "anorm" % "2.4.0", jdbc, evolutions)

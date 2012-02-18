package nielinjie.app.toBeCloud
package domain

import config.Config
import nielinjie.util.io.FileUtil._
import java.io.File
import scala.collection.JavaConversions._
import scalaz._
import Scalaz._
import org.ini4j.Ini
import org.ini4j.Profile.Section

class Define(config: Config) {
  import nielinjie.util.data.Params._

  lazy val file: Option[Ini] = {
    needFile(new File(config.configFilePath, "define.ini")).toOption.join.map {
      f: File =>
        new Ini(f)
    }
  }
  val mountDefineConfigLookUp = {
    for {
      name <- lookUpFor[String]("name").required
      point <- lookUpFor[String]("point").required
    } yield (Mount(name, new File(point)))
  }
  val globalLookUp = {
    for {
      name <- lookUpFor[String]("name").required
    } yield (Global(name))
  }
  implicit def mapProjectFunction: (Section, String) => Option[String] = {
    (m, k) => Option(m.get(k))
  }
  lazy val global: Option[Global] = {
    file.map({
      ini: Ini =>
        Option(ini.get("global")).map {
          section =>
            globalLookUp(section)(mapProjectFunction).toOption
        }.join
    }).join
  }

  lazy val mounts: List[Mount] = {
    file.map({
      ini: Ini =>
        ini.values().toList.map({
          section =>
            mountDefineConfigLookUp(section)(mapProjectFunction).toOption
        }).flatten
    }).getOrElse(List())
  }
  case class Global(name: String)
}
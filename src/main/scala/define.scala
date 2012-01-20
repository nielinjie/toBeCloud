package nielinjie.app.toBeCloud
package domain

import config.Configs
import nielinjie.util.io.FileUtil._
import java.io.File
import scala.collection.JavaConversions._
import scalaz._
import Scalaz._
import org.ini4j.Ini
import org.ini4j.Profile.Section

object Define {
  import nielinjie.util.data.Params._

  lazy val file = needFile(new File(Configs.default.configFilePath, "define.ini"))
  val mountDefineConfigLookUp = {
    for {
      name <- lookUpFor[String]("name").required
      point <- lookUpFor[String]("point").required
    } yield (Mount(name, new File(point)))
  }
  lazy val mounts: List[Mount] = {
    implicit def mapProjectFunction: (Section, String) => Option[String] = {
      (m, k) => Option(m.get(k))
    }
    file.toOption.join.map({
      f: File =>
        val ini = new Ini(f)
        ini.values().toList.map({
          section =>
            mountDefineConfigLookUp(section).toOption
        }).flatten
    }).getOrElse(List())
  }
}
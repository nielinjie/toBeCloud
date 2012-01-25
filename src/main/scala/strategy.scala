package nielinjie.app.toBeCloud
package domain

import scalaz._
import Scalaz._
import java.io.File

trait Strategy{
  def need(source:List[RemoteItem],mount:Mount):List[Transform]
}

class NewStrategy extends Strategy{
  override def need(source: List[RemoteItem], mount:Mount)={
    val dist=mount.ls
    source.filter{
      reItem=>
        !dist.exists(_.relativePath === reItem.relativePath)
    }.map{
      reItem=>
      Transform(reItem, Item(mount, new File(mount.point, reItem.relativePath)))
    }
  }
}
package components
import util.GridPos
import scala.collection.mutable.Buffer

class Tile(val pos: GridPos ){
  var contents: Option[gameObject] = None
}

class GrassTile(pos: GridPos) extends Tile(pos)

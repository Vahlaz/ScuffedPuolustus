package util
import util.CompassDir.*

import scala.annotation.targetName

class GridPos(val x: Int, val y: Int){
  def getImagePos =
    (80*(x-1), 80*(y-1))

  def equals(another: GridPos): Boolean =
    ((this.x == another.x) && (this.y == another.y))

  def getXdiff(another: GridPos) =
    another.x - this.x

  def getYdiff(another:GridPos) =
    another.y - this.y

  def getDirection(another: GridPos): (CompassDir, CompassDir) =
    val x =
      if this.getXdiff(another) <= 0 then
        CompassDir.East
      else
        CompassDir.West
    val y =
      if this.getYdiff(another) <= 0  then
        CompassDir.North
      else
        CompassDir.South
    (x,y)

  def neighbor(dir: CompassDir): GridPos =
    dir match
      case North => GridPos(this.x,(this.y-1))
      case South => GridPos(this.x,(this.y+1))
      case East => GridPos((this.x+1), this.y)
      case _ => GridPos((this.x-1),this.y)


  override def toString: String =
    "x: " + this.x + " y: " + this.y
}



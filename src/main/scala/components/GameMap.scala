package components
import scala.collection.mutable.Buffer
import util.GridPos
import util.CompassDir.*

import java.awt.geom.{Path2D, PathIterator, Point2D}

class GameMap(val width: Int, val height: Int, val startPos: GridPos, game:Game, stringMap: String) {
  val contents = Array.ofDim[Tile](width,height)
  var path = Buffer[Tile]()
  var path2D = Array[Point2D]()


  private def parseTextMap(string: String) =
    val textArray = Array.ofDim[Char](width, height)
    val textBuffer = Buffer[Buffer[Char]](Buffer[Char]())

    var temp = string.head
    for j <- 0 until height do
      for i <- 0 until width do
        textArray(i)(j) = string(i+j*width)
    textArray.filter(_.nonEmpty)

  def initialize() =
    val textMap = parseTextMap(stringMap)
    for (y <- 0 until height) do
      for (x <- 0 until width) do
        contents(x)(y) = new Tile(GridPos(x+1,y+1))
        textMap(x)(y) match
          case 'S' => {
            contents(x)(y).contents = Some(Path(South))
            path += contents(x)(y)
          }
          case 'N' => {
            contents(x)(y).contents = Some(Path(North))
            path += contents(x)(y)
          }
          case 'E' => {
            contents(x)(y).contents = Some(Path(East))
            path += contents(x)(y)
          }
          case 'W' => {
            contents(x)(y).contents = Some(Path(West))
            path += contents(x)(y)
          }
          case 'G' => {
            val newTower = GunTower(game, this, GridPos(x+1,y+1))
            contents(x)(y).contents = Some(newTower)
            game.towers = game.towers :+ newTower
          }
          case 'F' => {
            val newTower = FlameTower(game, this, GridPos(x+1,y+1))
            contents(x)(y).contents = Some(newTower)
            game.towers = game.towers :+ newTower
          }
          case 'R' => {
            contents(x)(y).contents = Some(Rock())
          }
          case _ =>
    orderPath()

  private def orderPath():Unit =
    val start = getElement(startPos)
    val orderedPath = Buffer[Tile](start)
    var next = start
    var counter = 0
    while counter < path.length-1 do
      next.contents.get match
        case p: Path => p.dirToNext match
          case North =>
            next = getElement(next.pos.neighbor(North))
            orderedPath += next
          case South =>
            next = getElement(next.pos.neighbor(South))
            orderedPath += next
          case East =>
            next = getElement(next.pos.neighbor(East))
            orderedPath += next
          case _ =>
            next = getElement(next.pos.neighbor(West))
            orderedPath += next
        case _ =>
      counter += 1
    path = orderedPath
    makeEnemyPath2D()

  private def makeEnemyPath2D() =
    val newPath2D = new Path2D.Double()
    newPath2D.moveTo(path.head.pos.x * 80, path.head.pos.y * 80)
    for tile <- path do
      tile.contents.get match
        case p: Path => p.dirToNext match
          case East =>
            for i <- 0 until 80 do
              newPath2D.lineTo(tile.pos.x * 80 + i , tile.pos.y * 80)
          case West =>
            for i <- 0 until 80 do
              newPath2D.lineTo(tile.pos.x * 80 - i , tile.pos.y * 80)
          case South =>
            for i <- 0 until 80 do
              newPath2D.lineTo(tile.pos.x * 80 , tile.pos.y * 80 + i)
          case _ =>
            for i <- 0 until 80 do
              newPath2D.lineTo(tile.pos.x * 80 , tile.pos.y * 80 - i)
        case _ =>
    val pi = newPath2D.getPathIterator(null,0.0)
    val points = Buffer[Point2D]()
    while(!pi.isDone) do
      val coords = new Array[Double](6)
      pi.currentSegment(coords) match
        case PathIterator.SEG_MOVETO =>
        case PathIterator.SEG_LINETO =>
          points += new Point2D.Double(coords(0), coords(1))
      pi.next()
    path2D = points.toArray.dropRight(40)


  def getElement(pos: GridPos): Tile =
    contents((pos.x-1))((pos.y-1))

  def getTileGridPos(tile: Tile): Option[GridPos] =
    var found: Option[GridPos] = None
    contents.foreach(a => {
      if a.contains(tile) then
        found = Some(GridPos( a.indexOf(tile) + 1,contents.indexOf(a) + 1))
    })
    found

  def contains(pos: GridPos):Boolean =
    (pos.x <= this.width) && (pos.y <= this.height) && (pos.x > 0) && (pos.y > 0)

  def printMap() =
    var ascii = ""
    for j <- 0 until height do
      for i <- 0 until width do
        val b = contents(i)(j)
        b.contents match
          case Some(thing) => thing match
            case p: Path => p.dirToNext match
                case North => ascii += "N"
                case East => ascii += "E"
                case West => ascii += "W"
                case _ => ascii += "S"
            case g: GunTower => ascii += "G"
            case f: FlameTower => ascii += "F"
            case r: Rock => ascii += "R"
            case _ => ascii += "A"
          case _ => ascii += "A"
      ascii += "\n"
    ascii

}

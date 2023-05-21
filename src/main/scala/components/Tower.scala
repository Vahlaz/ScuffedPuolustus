package components

import util.*
import scala.collection.mutable.Buffer

trait Tower(val game: Game, val map: GameMap, val pos: GridPos, val name: String) extends gameObject {
  var cost: Int
  val range: Int
  def update(): Unit
  def updateInRange(): Unit
  def attack(enemy: Enemy): Unit
}
object obGunTower extends GunTower(testGame, testGame.map, GridPos(1,1))

class GunTower(game: Game, map: GameMap, pos: GridPos) extends Tower(game, map, pos, "GunTower"):
  var cost              = 200
  private var dmg       = 150
  val range             = 3
  private var cooldown  = 40
  private var prjSpeed  = 10
  private var inRange   = Array[GridPos]()

  def updateInRange() =
    inRange = getInRange()

  private def getInRange(): Array[GridPos] =
    val neighbors = Buffer[GridPos]()
    for i <- -(range) until (range+1) do
      for j <- -(range) until (range+1) do
        val checkpos = GridPos(this.pos.x + i, this.pos.y + j)
        if map.contains(checkpos) && !checkpos.equals(this.pos) then
          neighbors += checkpos

    val inRangePath = Buffer[GridPos]()
    for pos <- neighbors do
      map.path.find(a => a.pos.equals(pos)) match
        case Some(tile) => inRangePath += pos
        case _ =>
    inRangePath.toArray

  def attack(enemy: Enemy) =
    val startPoint = (pos.x * 80 - 10  , pos.y * 80 - 65)
    game.projectiles = game.projectiles :+ new GunProjectile(startPoint, enemy, prjSpeed, dmg)

  private var counter = 0
  def update() =
    if counter % cooldown == 0 then
      game.enemies.find(e => this.inRange.exists(_.equals(e.gridPos))) match
        case Some(enemy) =>
          attack(enemy)
          counter = 0
        case _ =>
      counter += 1
    else
      counter += 1

end GunTower


object obFlameTower extends FlameTower(testGame, testGame.map, GridPos(1,1))

class FlameTower(game: Game, map: GameMap, pos: GridPos) extends Tower( game, map, pos, "FlameTower"):
  var cost              = 100
  private var dmg       = 15
  val range             = 2
  private var cooldown  = 4
  private var prjSpeed  = 4
  private var inRange   = Array[GridPos]()

  def updateInRange() =
    inRange = getInRange()

  private def getInRange(): Array[GridPos] =
    val neighbors = Buffer[GridPos]()
    for i <- -(range) until (range+1) do
      for j <- -(range) until (range+1) do
        val checkpos = GridPos(this.pos.x + i, this.pos.y + j)
        if map.contains(checkpos) && !checkpos.equals(this.pos) then
          neighbors += checkpos

    val inRangePath = Buffer[GridPos]()
    for pos <- neighbors do
      map.path.find(a => a.pos.equals(pos)) match
        case Some(tile) => inRangePath += pos
        case _ =>
    inRangePath.toArray

  def attack(enemy: Enemy) =
    val startPoint = (pos.x * 80 - 10, pos.y * 80 - 75)
    game.projectiles = game.projectiles :+ new FlameProjectile(startPoint, enemy, prjSpeed, dmg)

  private var counter = 0
  def update() =
    if counter % cooldown == 0 then
      game.enemies.find(e => this.inRange.exists(_.equals(e.gridPos))) match
        case Some(enemy) =>
          attack(enemy)
        case _ =>
        counter += 1
    else
      counter += 1

end FlameTower

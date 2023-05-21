package components
import scala.collection.mutable.Buffer
import util.*

class Game(stringMap: String) {
  var hp          = 100
  var money       = 300
  var isLost      = false
  var (width,
      height,
      startPos) = getMapInfo()
  var towers      = Array[Tower]()

  var map         = GameMap(width,height, startPos, this, stringMap.takeWhile(_ != '#'))
  map.initialize()

  var enemies     = Buffer[Enemy]()
  var projectiles = Array[Projectile]()
  var toBeAdded   = Array[Enemy]()
  var pathEnd     = map.path.last.pos
  var counter     = 0
  towers.foreach(_.updateInRange())

  private def getMapInfo(): (Int, Int, GridPos) =
    val info      = stringMap.dropWhile(_ != '#').tail.sliding(2,2).toArray
    var startPos  = GridPos(info(2).toInt,info(3).toInt)
    (info.head.toInt, info(1).toInt, startPos)

  def spawnManager() =
    toBeAdded.headOption match
      case Some(enemy) =>
        enemies   += enemy
        toBeAdded = toBeAdded.tail.toArray
      case _ =>

  def addEnemy(hp: Int, speed: Int) =
    enemies += new Enemy(this, 255, map.path.head.pos, speed)

  def addTower(towerType: String, pos: GridPos): Boolean =
    val tile = map.getElement(pos)
    val newTower = towerType match
      case "GunTower" => GunTower(this,map,pos)
      case "FlameTower" => FlameTower(this,map,pos)
      case _ => GunTower(this,map,pos)

    if this.money < newTower.cost || isLost then
      false
    else
      tile.contents match
        case None =>
          tile.contents = Some(newTower)
          this.towers = this.towers :+ newTower
          newTower.updateInRange()
          towers.foreach(_.updateInRange())
          this.money -= newTower.cost
          true
        case _ =>
          false

  var currentWave             = 0
  private var waveCounter     = 0
  private var waitTimer       = 0
  private def enemiesNextWave = (50 +(currentWave * 2))
  var inWave                  = false
  var autoPlay                = false

  def waveManager() =
    if inWave then
      if waveCounter >= 8 && toBeAdded.isEmpty then
        if waitTimer <= 2  then
          waitTimer += 1
        else
          waveCounter = 0
          waitTimer = 0
          if !autoPlay then
            inWave = false
          else
            currentWave += 1
      else if waveCounter == 8 then
        toBeAdded = toBeAdded ++ Array.fill[Enemy]((enemiesNextWave* (2.toDouble/ 10)).toInt)(new Enemy(this,100*(1+currentWave),map.path.head.pos,2))
        waveCounter+=1
      else if waveCounter == 5 then
        toBeAdded = toBeAdded ++ Array.fill[Enemy]((enemiesNextWave * (3.toDouble / 5)).toInt)(new Enemy(this,50*(1+currentWave),map.path.head.pos,3))
        waveCounter+=1
      else if waveCounter == 0 then
        toBeAdded = toBeAdded ++ Array.fill[Enemy](enemiesNextWave / 2)(new Enemy(this,34*((1+currentWave)),map.path.head.pos,2))
        waveCounter+=1
      else
        waveCounter += 1

  def update() =
    if !isLost then
      enemies.foreach(e => {
        e.move()
      })

      towers.foreach(t => {
        t.update()
      })

      projectiles.foreach(_.update())
      projectiles = projectiles.filter(_.isInProgress)
      enemies     = enemies.filter(_.alive)
      enemies     = enemies.sortBy(_.moveCounter).reverse
      if hp <= 0 then
        this.inWave     = false
        this.toBeAdded  = Array()
        this.enemies    = this.enemies.empty
        this.isLost     = true
  end update
}

object testGame extends Game(FileIO.loadMap(FileIO.loadMapNames().head))

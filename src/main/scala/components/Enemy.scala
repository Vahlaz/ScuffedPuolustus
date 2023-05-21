package components

import util.*
import util.CompassDir.*
import java.awt.Color

class Enemy(val game: Game, hp: Int, var startPos: GridPos, var speed: Int):
  private var currentHP = hp
  var alive = true
  val gameMap = game.map
  val path = gameMap.path
  val path2D = gameMap.path2D
  var drawPos = (startPos.x * 80  , startPos.y * 80 )
  private var currentDirr =  East

  def color: Color =
    if this.currentHP > 510 then
      new Color(84,59,50)
    else if this.currentHP > 255 then
      new Color(255,0,0)
    else
      new Color(currentHP,0,255-currentHP)

  def gridPos: GridPos =
    GridPos(((drawPos._1.toDouble ) / 80).floor.toInt, (((drawPos._2.toDouble))/ 80).ceil.toInt)

  def currentTile: Tile =
    gameMap.getElement(gridPos)

  private def die(): Unit =
    this.game.money += 2
    this.alive = false

  def takeDmg(amount: Int): Unit =
    this.currentHP -= amount
    if this.currentHP <= 0 then
      this.die()

  var moveCounter = 0
  def move(): Unit =
    if moveCounter >= path2D.length then
      game.hp -= 10
      this.die()
    else
      drawPos = (path2D(moveCounter).getX.toInt,path2D(moveCounter).getY.toInt)
      moveCounter += speed

end Enemy
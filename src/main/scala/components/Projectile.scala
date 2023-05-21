package components

import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import images.*



trait Projectile(startPoint: (Int,Int), target: Enemy, speed: Int, dmg: Int) {
  var (x, y) = startPoint
  var isInProgress: Boolean
  def update(): Unit
  def hit(): Unit
}

class GunProjectile(startPoint: (Int, Int), target: Enemy, speed: Int, dmg: Int) extends Projectile(startPoint, target, speed, dmg):
  var isInProgress = true

  def hit() =
    this.target.takeDmg(dmg)
    this.isInProgress = false

  def update() =
    val dx = x - target.drawPos._1 + 60
    val dy = y - target.drawPos._2 + 60
    if (-20 to 20 contains dx) & (-20 to 20 contains dy) then
      hit()
    if target.alive then
      if dx > 10 then
        x -= speed
      else if dx < -10 then
        x += speed
      if dy > 10 then
        y -= speed
      else if dy < -10 then
        y += speed
    else
      isInProgress = false

end GunProjectile


class FlameProjectile(startPoint: (Int, Int), target: Enemy, speed: Int, dmg: Int) extends Projectile(startPoint, target, speed, dmg):
  var isInProgress = true

  def hit() =
    this.target.takeDmg(dmg)
    this.isInProgress = false

  def update() =
    val dx = x - target.drawPos._1 + 60
    val dy = y - target.drawPos._2 + 60
    if (-30 to 30 contains dx) & (-30 to 30 contains dy) then
      hit()
    if target.alive then
      if dx > 5 then
        x -= speed
      else if dx < -5 then
        x += speed
      if dy > 5 then
        y -= speed
      else if dy < -5 then
        y += speed
    else
      isInProgress = false

end FlameProjectile

package components

import scala.swing._
import java.awt.Graphics2D
import java.awt.geom.Rectangle2D
import javax.swing.JRadioButton

class MultilineRadioButton(Label: String, text: String ) extends RadioButton(Label) {
  override def paintComponent(g: Graphics2D): Unit = {
    super.paintComponent(g)

    val fm = g.getFontMetrics
    val rect = new Rectangle2D.Double(0, 0, size.width, size.height)
    val lines = text.split("\n")
    val lineHeight = fm.getHeight
    var y = (rect.getHeight - lineHeight * lines.length) / 2 + fm.getAscent + lineHeight + 5
    for (line <- lines) {
      val x = (rect.getWidth - fm.stringWidth(line)) / 2
      g.drawString(line, x.toFloat, y.toFloat)
      y += lineHeight
    }
  }
}
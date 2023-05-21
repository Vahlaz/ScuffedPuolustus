import components.*
import util.*

import scala.swing.*
import java.awt.{Color, Dimension, GridBagConstraints, GridBagLayout, LinearGradientPaint}
import javax.imageio.ImageIO
import java.io.File
import scala.collection.mutable.Buffer
import java.awt.event.{ActionListener, ComponentAdapter, ComponentEvent, ComponentListener}
import images.*

import scala.io.Source
import scala.swing.event.{ButtonClicked, Event, MouseClicked}

var grassTile                 =  ImageIO.read((this.getClass.getResource("images/GrassTile2.png")))
var pathTile                  =  ImageIO.read((this.getClass.getResource("images/PathTile.png")))
var rockTile                  =  ImageIO.read((this.getClass.getResource("images/RockTile.png")))
var gunTowerTile              =  ImageIO.read((this.getClass.getResource("images/GunTower.png")))
var FlameTowerTile            =  ImageIO.read((this.getClass.getResource("images/FlameTower.png")))
var gunProjectileGraphic      =  ImageIO.read((this.getClass.getResource("images/GunProjectile.png")))
var flameProjectileGraphic    =  ImageIO.read((this.getClass.getResource("images/FlameProjectile.png")))
var GameOver1                 =  ImageIO.read((this.getClass.getResource("images/GameOver1.png")))
var GameOver2                 =  ImageIO.read((this.getClass.getResource("images/GameOver2.png")))

val tileSize = 80
var updateTime = 10




object GameApp extends SimpleSwingApplication:
  val mapNames    = FileIO.loadMapNames()
  val stringMap   = FileIO.loadMap(mapNames.head)
  var game        = Game(stringMap)
  var map         = ""
  var gameDisplay = new GamePlayView(game)
  var chosenTower = "GunTower"

  object viewManager extends BoxPanel(Orientation.Vertical){
    contents += gameDisplay
    }

  var aTop = new MainFrame() {
    contents  = viewManager
    title     = "ScuffedPuolustus"
    resizable = false
    pack().centerOnScreen()
  }

  def top = aTop

  var count = 0
  var updater = new ActionListener {
    def actionPerformed(e: java.awt.event.ActionEvent) = {
      game.update()
      gameDisplay.repaint()
      gameDisplay.update()
      if (count % 24 == 0)  game.spawnManager()
      if (count % 100 == 0) game.waveManager()
      count += 1
    }
  }
  var timer = new javax.swing.Timer(updateTime, updater)
  timer.start()

end GameApp


def changeGame(newGame: Game) =
  val newView = new GamePlayView(newGame)
  GameApp.viewManager.contents -= GameApp.gameDisplay
  GameApp.viewManager.contents += newView
  GameApp.gameDisplay  = newView
  GameApp.top.visible  = false
  GameApp.chosenTower  = "GunTower"
  GameApp.aTop.close()
  val newTop = new MainFrame() {
    contents = GameApp.viewManager
    title = "ScuffedPuolustus"
    resizable = false
    pack().centerOnScreen()
  }
  GameApp.aTop = newTop
  GameApp.aTop.open()
  GameApp.game = newGame

  GameApp.updater = new ActionListener {
    def actionPerformed(e: java.awt.event.ActionEvent) = {
      newGame.update()
      newView.repaint()
      newView.update()
      if (GameApp.count % 24 == 0) newGame.spawnManager()
      if (GameApp.count % 100 == 0) newGame.waveManager()
      GameApp.count += 1
    }
  }
  GameApp.count = 0
  GameApp.timer.stop()
  GameApp.timer = new javax.swing.Timer(updateTime, GameApp.updater)
  GameApp.timer.start()


class GameView(game: Game) extends GridPanel(game.map.width,game.map.height):
  val gameMap = game.map

  preferredSize = new Dimension(gameMap.width*tileSize, gameMap.height*tileSize)
  listenTo(mouse.clicks)

  reactions += {
    case MouseClicked(source,point,_,_,_) =>
      val pos = GridPos(((point.x* 1.0 / tileSize).floor + 1).toInt, ((point.y* 1.0 / tileSize).ceil).toInt)
      game.addTower(GameApp.chosenTower, pos)
  }
  var rotater = true
  val (i,j) = (preferredSize.width/2 - tileSize ,preferredSize.height/2 - tileSize/2)
  override def paintComponent(g: Graphics2D): Unit =
    for (y<- 0 until gameMap.height) do
      for(x<- 0 until gameMap.width) do
        val texture = gameMap.contents(x)(y).contents match
          case Some(content) => content match
            case p: Path          => pathTile
            case gun: GunTower    => gunTowerTile
            case fire: FlameTower => FlameTowerTile
            case rock: Rock       => rockTile
            case _                => grassTile
          case _ => grassTile
        g.drawImage(texture,(x)*tileSize,(y)*tileSize,null)

    if !game.isLost then
      for (e <- game.enemies) do
        g.setPaint(e.color)
        g.fillOval((e.drawPos._1-60),(e.drawPos._2-60) ,40,40)

      for (p <- game.projectiles) do
        val texture = p match
          case g: GunProjectile => gunProjectileGraphic
          case f: FlameProjectile => flameProjectileGraphic
          case _ => gunProjectileGraphic

        g.drawImage(texture,p.x,p.y,null)
    else
      g.drawImage(GameOver2,i,j,null)

end GameView


class GamePlayView(game: Game) extends BoxPanel(Orientation.Horizontal):
  private val gameView = new GameView(game)
  private val statView = new StatView(game)
  def update() = statView.update()
  contents += gameView
  contents += statView

end GamePlayView


class StatView(game: Game) extends BoxPanel(Orientation.Horizontal):
  private val hpLabel = new Label("hp:  " + game.hp.toString)
  private val moneyLabel = new Label("money:  "  + game.money.toString)
  private val wavelabel = new Label("wave: " + game.currentWave.toString)
  private val towerButtons = new ButtonGroup{
    buttons +=  new TowerButton(obGunTower)
    buttons += new TowerButton(obFlameTower)
  }
  towerButtons.select(towerButtons.buttons.head)

  private val inWaveContents = new GridPanel(5,1) {
    contents += wavelabel
    contents += hpLabel
    contents += moneyLabel
    contents ++= towerButtons.buttons
  }
  private val settingButtons = new ButtonGroup{
    buttons += new Button("Next Wave"){
      reactions += {
        case e: ButtonClicked =>
          game.inWave = true
          game.currentWave += 1
      }
    }
    buttons += new Button("AutoPlay") {
      reactions += {
        case e: ButtonClicked => {
          game.autoPlay = !game.autoPlay
          this.background =
            if game.autoPlay then
              new Color(153, 204, 255)
            else
              javax.swing.plaf.ColorUIResource(238,238,238)
        }
      }
    }
    buttons += new SaveButton(game)
    buttons += new NewGameButton
    buttons += new LoadButton
  }
  private val betweenWavesContent = new GridPanel(5,1) {
    contents ++= settingButtons.buttons
  }
  contents += inWaveContents
  contents += betweenWavesContent

  def update() =
    val enabled = (!game.inWave && game.enemies.isEmpty && game.toBeAdded.isEmpty)
    settingButtons.buttons.filter(_.text != "AutoPlay").foreach(_.enabled = enabled)
    settingButtons.buttons.head.enabled = enabled && !game.isLost

    wavelabel.text = "wave:  " + game.currentWave.toString
    hpLabel.text = "hp:  " + game.hp.toString
    moneyLabel.text = "money:  " + game.money.toString

end StatView


class TowerButton(tower: Tower) extends MultilineRadioButton(tower.name, s"(${tower.cost})"):
  reactions += {
    case e: ButtonClicked =>
      GameApp.chosenTower = tower.name
  }

end TowerButton


class NewGameButton extends Button("New Game"):
  reactions += {
    case e: ButtonClicked => setupMenu.show(this,0,this.bounds.height)
  }

end NewGameButton


object setupMenu extends PopupMenu:
  var mapNames = FileIO.loadMapNames()
  def update() =
    mapNames = FileIO.loadMapNames()
  for name <- mapNames do
    contents += new ChooseMapButton(name)

end setupMenu


class ChooseMapButton(name: String) extends Button(name):
  private def chooseMap(map: String) =
    val newGame = Game(FileIO.loadMap(map))
    changeGame(newGame)
    GameApp.viewManager.revalidate()
  reactions += {
    case e : ButtonClicked => chooseMap(name)
  }

end ChooseMapButton


class SaveButton(game: Game) extends Button("Save"):
  private val filenameTextField = new TextField()
  private val saveButton = new Button("Save") {
    reactions += {
      case e: ButtonClicked =>
        if filenameTextField.text.nonEmpty then
          FileIO.writeSave(game,filenameTextField.text)
          filenameTextField.text = ""
      }
    }
  private val getTextDialog = new PopupMenu {

    contents += new Label("       Enter name for savefile      ")
    contents += filenameTextField
    contents += saveButton
    }

  reactions += {
    case e: ButtonClicked => getTextDialog.show(this,0,this.bounds.height)
  }

end SaveButton


class LoadButton extends Button("Load Game"):
  def saveNames = FileIO.loadSaveNames()
  reactions += {
    case e: ButtonClicked => loadMenu.show(this,0,this.bounds.height)
  }
  private def loadMenu = new PopupMenu {
    saveNames.foreach(n => contents += new ChooseSaveButton(n))
  }

end LoadButton


class ChooseSaveButton(name: String) extends Button(name):
  private val data = FileIO.loadSave(name)
  reactions += {
    case e: ButtonClicked => parseData()

  }
  private def parseData() =
    val round = data.dropWhile(_ != 'R').tail.takeWhile(_ != '\n').toInt
    val textMap = data.dropWhile(_ != 'S').drop(2).takeWhile(_ != 'M').filter(_ != '\n')
    val money = data.dropWhile(_ != 'M').tail.takeWhile(_!='\n').toInt
    val hp = data.dropWhile(_ != 'H').tail.takeWhile(_ != '\n').toInt
    val newGame = new Game(textMap)
    newGame.currentWave = round
    newGame.money = money
    newGame.hp = hp
    changeGame(newGame)
    GameApp.viewManager.revalidate()

end ChooseSaveButton
package util

import components.*

import java.io.{PrintWriter, File}
import scala.io.Source.*
import java.nio.file.{FileSystems, Files}
import scala.collection.mutable.Buffer


object FileIO:
  def loadMapNames() =
    val dir = FileSystems.getDefault.getPath("src/main/scala/files/maps")
    var fileIterator = Files.list(dir).iterator()
    var temp = fileIterator.next()
    var mapNames = Buffer[String]()
    while fileIterator.hasNext do
      mapNames += temp.getFileName.toString
      temp = fileIterator.next()
    mapNames += temp.getFileName.toString
    mapNames

  def loadMap(name: String) =
    var textBuffer = ""
    val a = scala.io.Source.fromFile("src/main/scala/files/maps/" + name)
    a.getLines().foreach(a => textBuffer = textBuffer + a)
    textBuffer

  def loadSaveNames() =
    val dir = FileSystems.getDefault.getPath("src/main/scala/files/saves")
    var fileIterator = Files.list(dir).iterator()
    var temp = fileIterator.next()
    var saveNames = Buffer[String]()
    while fileIterator.hasNext do
      saveNames += temp.getFileName.toString
      temp = fileIterator.next()
    saveNames += temp.getFileName.toString
    saveNames

  def loadSave(name: String) =
    var textBuffer = ""
    val a = scala.io.Source.fromFile("src/main/scala/files/saves/" + name)
    a.getLines().foreach( a => textBuffer = textBuffer + a + "\n")
    textBuffer

  def writeSave(game: Game, fileName: String) =
    var (x,y) = ("","")
    if game.startPos.x < 10 then
      x = s"0${game.startPos.x}"
    else
      x = s"${game.startPos.x}"
    if game.startPos.y < 10 then
      y = s"0${game.startPos.y}"
    else
      y = s"${game.startPos.y}"
    var textBuffer =
      s"R${game.currentWave}\n" +
      "S\n" +
      game.map.printMap() +
      s"#${game.map.width}${game.map.height}${x}${y}\n" +
      s"M${game.money}\n" +
      s"H${game.hp}\n"
    val newFile = new File("src/main/scala/files/saves/" + fileName)
    new PrintWriter(newFile){ write(textBuffer); flush(); close()}

end FileIO


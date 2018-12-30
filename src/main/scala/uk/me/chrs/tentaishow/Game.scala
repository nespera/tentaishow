package uk.me.chrs.tentaishow

import scala.collection.mutable.ListBuffer
import scala.io.StdIn

case class Game(board: Board, state: Map[Square, Option[Star]]) {

  import Board._

  override def toString: String = {
    val s = new StringBuilder
    for (r <- 0 until board.height) {
      s.append(gridLine())
      s.append(rowAsString(r))
    }
    s.append(gridLine())
    s.toString()
  }

  private def rowAsString(r: Int): String = {
    val s = new StringBuilder
    for (c <- 0 until board.width) {
      val fill = state(Square(r, c)).map(star => star.name.take(3)).getOrElse(" ")
      s.append(VERT + f"$fill%3s")
    }
    s.append(VERT + "\n")
    s.toString()
  }

  private def gridLine(): String = {
    (0 until board.width).map(_ => POINT + HORIZ + HORIZ + HORIZ).mkString("") + POINT + "\n"
  }
}

object Game extends App {

  val game: Game = {
    val board: Board = Board.parse(readLinesFromStdIn)
    init(board)
  }

  Console.println("Solving:\n")
  Console.println(game.board.toString)

  def init(board: Board): Game = {
    board.validate()
    val initState = board.squares.map(sq => (sq, None)).toMap
    Game(board, initState)
  }

  private def readLinesFromStdIn: Seq[String] = {
    val lines: ListBuffer[String] = ListBuffer()
    var line = ""
    while ( {line = StdIn.readLine(); line != null}) {
      lines.append(line)
    }
    lines
  }
}
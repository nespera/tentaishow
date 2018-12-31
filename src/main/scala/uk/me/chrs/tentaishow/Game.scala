package uk.me.chrs.tentaishow

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer
import scala.io.StdIn

case class Game(board: Board, state: Map[Square, Option[Star]]) {
  import Board._

  def isComplete: Boolean = state.forall(x => x._2.isDefined)

  def countComplete: Int = state.count(x => x._2.isDefined)

  def fillGimmes: Game = {
    val gimmes = board.squares.map(sq => sq -> board.adjacentStars(sq).headOption)
      .filter(x => x._2.isDefined)
    this.copy(state = this.state ++ gimmes)
  }

  override def toString: String = {
    val s = new StringBuilder
    for (r <- 0 until board.height) {
      s.append(gridLine())
      s.append(rowAsString(r))
    }
    s.append(gridLine())
    s.toString()
  }

  def asImage: String = {
    val s = new StringBuilder
    for (r <- 0 until board.height) {
      for (c <- 0 until board.width) {
        val fill = state(Square(r, c)).map(star => star.colour.fill).getOrElse(" ")
        s.append(fill + " ")
      }
      s.append("\n")
    }
    s.toString()
  }

  //Which stars are reachable from the given square, assuming gimmes are already filled?
  def reachable(square: Square): Set[Star] = {
    val expanded = expandReachable(Set(square))
    state.filter(s => expanded.contains(s._1)).values.flatten.toSet
  }

  @tailrec
  private def expandReachable(squares: Set[Square]): Set[Square] = {
    val empty = state.filter(s => squares.contains(s._1) && s._2.isEmpty).keys
    val besideEmpty = empty.flatMap(board.adjacentSquares)
    val expanded = squares ++ besideEmpty
    if (expanded.size == squares.size){
      squares
    } else {
      expandReachable(expanded)
    }
  }

  def mirrorIsEmpty(square: Square, star: Star): Boolean = {
    val mirror = square.rotate(star.coordinate)
    board.contains(mirror) && state(mirror).isEmpty
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
  Console.println(game.board.toString + "\n")
  val solved = Game.solve(game)

  Console.println("Result:\n")
  Console.println(solved.toString + "\n")
  Console.println(solved.asImage + "\n")

  Console.println(if (solved.isComplete) "Complete" else "FAILED TO COMPLETE")

  def solve(game: Game): Game = {
    solveSteps(game.fillGimmes)
  }

  @tailrec
  private def solveSteps(game: Game): Game = {
    val next = fillUnique(game)
    if (next.isComplete || next.countComplete == game.countComplete) next else solveSteps(next)
  }

  private def fillUnique(start: Game): Game = {
    var game: Game = start

    val empty = game.state.filter(_._2.isEmpty).keys
    for (square <- empty) {
      val possibleStars = game.reachable(square).filter(star => {
        game.mirrorIsEmpty(square, star)
      })
      if (possibleStars.size == 1){
        val star = possibleStars.head
        val mirror = square.rotate(star.coordinate)
        game = game.copy(state = game.state ++ Seq(square -> Some(star), mirror -> Some(star)))
      }
    }
    game
  }

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
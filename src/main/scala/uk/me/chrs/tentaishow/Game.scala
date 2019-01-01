package uk.me.chrs.tentaishow

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer
import scala.io.StdIn

case class Game(board: Board, state: Map[Square, Option[Star]]) {
  import Board._

  def isFilled: Boolean = state.forall(x => x._2.isDefined)

  def isSolved: Boolean = isFilled && isContiguous

  def countFilled: Int = state.count(x => x._2.isDefined)

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
        val fill = state(Square(r, c)).map(star => star.colour.fill).getOrElse("  ")
        s.append(fill)
      }
      s.append("\n")
    }
    s.toString()
  }

  private def isContiguous: Boolean = {
    board.stars.forall(star => isContiguous(star))
  }

  private def isContiguous(star: Star): Boolean = {
    val squares = state.filter(_._2.contains(star)).keys.toSet
    val joinedToFirst = expandJoined(Set(squares.head), star)
    joinedToFirst == squares
  }

  @tailrec
  private def expandJoined(squares: Set[Square], star: Star): Set[Square] = {
    val beside = squares.flatMap(board.adjacentSquares).filter(s => state(s).contains(star))
    val expanded = squares ++ beside
    if (expanded.size == squares.size){
      squares
    } else {
      expandJoined(expanded, star)
    }
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

  Console.println(if (solved.isSolved) "Solved" else "FAILED TO SOLVE")

  def solve(game: Game): Game = {
    val withUnique = fillUniqueSquares(game.fillGimmes)
    val solved = solveSteps(withUnique)
    solved.getOrElse(withUnique)
  }

  @tailrec
  private def fillUniqueSquares(game: Game): Game = {
    val next = fillUnique(game)
    if (next.isFilled || next.countFilled == game.countFilled) next else fillUniqueSquares(next)
  }

  private def fillUnique(game: Game): Game = {
    val emptySquares = game.state.filter(_._2.isEmpty).keys
    emptySquares.foldLeft(game)(fillIfUnique)
  }

  private def fillIfUnique(game: Game, square: Square): Game = {
    val possibleStars = possibleStarsFor(square, game)
    if (possibleStars.size == 1){
      val star = possibleStars.head
      fillSquare(game, square, star)
    } else {
      game
    }
  }

  private def possibleStarsFor(square: Square, game: Game) = {
    game.reachable(square).filter(star => {
      game.mirrorIsEmpty(square, star)
    })
  }

  private def solveSteps(game: Game): Option[Game] = {
    if (game.isFilled) {
      if (game.isSolved) Some(game) else None
    } else {
      val emptySquares = game.state.filter(_._2.isEmpty).keys
      fill(game, emptySquares.head)
    }
  }

  private def fill(game: Game, square: Square): Option[Game] = {
    val possibleStars = possibleStarsFor(square, game)
    if (possibleStars.isEmpty){
      None
    } else {
      possibleStars.view.flatMap(star => {
        solveSteps(fillSquare(game, square, star))
      }).headOption
    }
  }

  private def fillSquare(game: Game, square: Square, star: Star): Game = {
    val mirror = square.rotate(star.coordinate)
    game.copy(state = game.state ++ Seq(square -> Some(star), mirror -> Some(star)))
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
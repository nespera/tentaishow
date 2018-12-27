package uk.me.chrs.tentaishow

import scala.collection.mutable.ListBuffer
import scala.io.StdIn

case class Game(board: Board, stars: Set[Star], state: Map[Square, Option[Star]]) {

  private val POINT = "·"
  private val HORIZ = "‒"
  private val VERT  = "|"

  override def toString: String = {
    val s = new StringBuilder
    for (r <- 0 until board.height) {
      s.append(gridLine())
      s.append(rowAsString(r))
    }
    s.append(gridLine())
    s.toString()
  }

  def starMap: String = {
    ""
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
    (0 until board.width).map(i => POINT + HORIZ + HORIZ + HORIZ).mkString("") + POINT + "\n"
  }
}

object Game extends App {

  val game: Game = init(readLinesFromStdIn)
  Console.println(game)

  def init(lines: Seq[String]): Game = {
    val trimmed = lines.map(_.trim).filter(_.nonEmpty)
    val lengths = trimmed.map(_.trim.length).toSet
    if (lengths.size > 1) throw new IllegalArgumentException("Input has lines of mismatched lengths")
    val board = Board(trimmed.size, lengths.head)
    val stars = parseStars(trimmed)
    init(board, stars)
  }

  def init(board: Board, stars: Set[Star]): Game = {
    stars.find(s => !board.contains(s.coordinate))
      .foreach(s => throw new IllegalArgumentException("Initialized with Star not inside the board"))

    board.squares.find(square => {
      val adjacentStars = stars.filter(star => square.adjacentTo(star.coordinate))
      adjacentStars.size > 1
    }).foreach(s => throw new IllegalArgumentException("Initialized with multiple stars adjacent to a single square"))

    val initState = board.squares.map(sq => (sq, None)).toMap
    Game(board, stars, initState)
  }

  private def parseStars(lines: Seq[String]): Set[Star] = {
    val stars = new ListBuffer[Star]
    for((line,row) <- lines.view.zipWithIndex) {
      for((char, col) <- line.toCharArray.zipWithIndex) {
        val centre = Square(row, col).centre
        val optCoord = char.toLower match {
          case '.' => None
          case 'c' => Some(centre)
          case 'b' => Some(centre.copy(x = centre.x + 1))
          case 'r' => Some(centre.copy(y = centre.y + 1))
          case 'd' => Some(centre.copy(x = centre.x + 1, y = centre.y + 1))
          case _ => throw new IllegalArgumentException("Unrecognized character: " + char)
        }
        optCoord.foreach(coord => {
          val colour = if (char.isUpper) Black else White
          stars.append(Star("a", colour, coord))
        })
      }
    }
    stars.toSet
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
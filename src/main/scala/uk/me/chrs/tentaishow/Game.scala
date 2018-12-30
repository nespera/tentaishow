package uk.me.chrs.tentaishow

import scala.collection.mutable.ListBuffer
import scala.io.StdIn

case class Game(board: Board, state: Map[Square, Option[Star]]) {

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
    val s = new StringBuilder
    for (x <- 0 until board.height*2 + 1) {
      s.append(starLine(x))
      s.append("\n")
    }
    s.toString()
  }

  private def starLine(x: Int): String = {
    val s = new StringBuilder
    for (y <- 0 until board.width*2 + 1) {
      val matchingStar = board.stars.find(s => s.coordinate == Coordinate(x,y))
      s.append(matchingStar match {
        case Some(star) => star.colour.symbol
        case _ => starPoint(x,y)
      })
      s.append(if (x%2 == 0) HORIZ else " ")
    }
    s.toString().dropRight(1)
  }

  private def starPoint(x: Int, y: Int): String = {
    val xEven = x%2 == 0
    val yEven = y%2 == 0
    if (xEven) {
      if (yEven) POINT else HORIZ
    } else {
      if (yEven) VERT else " "
    }
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

  val game: Game = init(readLinesFromStdIn)
  Console.println("Solving:\n")
  Console.println(game.starMap)

  def init(lines: Seq[String]): Game = {
    val trimmed = lines.map(_.trim).filter(_.nonEmpty)
    val lengths = trimmed.map(_.trim.length).toSet
    if (lengths.size > 1) throw new IllegalArgumentException("Input has lines of mismatched lengths")
    val stars = parseStars(trimmed)
    val board = Board(trimmed.size, lengths.head, stars)
    init(board)
  }

  def init(board: Board): Game = {
    board.validate
    val initState = board.squares.map(sq => (sq, None)).toMap
    Game(board, initState)
  }


  private def parseStars(lines: Seq[String]): Set[Star] = {
    val labels = for (i <- 0 to 9; a <- 'a' to 'z') yield "" + a + i
    val stars = new ListBuffer[Star]
    for((line,row) <- lines.view.zipWithIndex) {
      for((char, col) <- line.toCharArray.zipWithIndex) {
        val centre = Square(row, col).centre
        offsetByChar(centre, char).foreach(coord => {
          stars.append(Star(labels(stars.size), colourFromChar(char), coord))
        })
      }
    }
    stars.toSet
  }

  private def colourFromChar(char: Char) = {
    if (char.isUpper) Black else White
  }

  private def offsetByChar(coord: Coordinate, char: Char): Option[Coordinate] = {
    char.toLower match {
      case '.' => None
      case 'c' => Some(coord)
      case 'b' => Some(coord.copy(x = coord.x + 1))
      case 'r' => Some(coord.copy(y = coord.y + 1))
      case 'd' => Some(coord.copy(x = coord.x + 1, y = coord.y + 1))
      case _ => throw new IllegalArgumentException("Unrecognized character: " + char)
    }
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
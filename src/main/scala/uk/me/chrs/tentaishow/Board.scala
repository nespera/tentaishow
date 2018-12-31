package uk.me.chrs.tentaishow

import MathUtil._
import scala.collection.mutable.ListBuffer

/*
 Coordinates on the board run as follows

 (0,0) ..... (0,m)
  .           .
  .           .
 (n,0) ..... (n,m)

 (0,0) being the top left of the board
 (1,1) being being in the centre of the square in row 0, col 0
 (1,2) being to the right of that square

 */

case class Board(height: Int, width: Int, stars: Set[Star]) {

  import Board._

  val squares: Seq[Square] = for(r <- 0 until height; c <-0 until width)
    yield Square(r,c)

  def contains(square: Square): Boolean = {
    squares.contains(square)
  }

  //Note coordinates on the edge are excluded
  def contains(coord: Coordinate): Boolean = {
    val adjacent: Seq[Square] = for (r <- coord.rows; c <- coord.cols) yield Square(r,c)
    adjacent.forall(square => this.contains(square))
  }

  def validate(): Unit = {
    stars.find(s => !contains(s.coordinate))
      .foreach(_ => throw new IllegalArgumentException("Initialized with Star not inside the board"))

    squares.find(adjacentStars(_).size > 1)
      .foreach(_ => throw new IllegalArgumentException("Initialized with multiple stars adjacent to a single square"))
  }

  def adjacentStars(square: Square): Set[Star] = {
    stars.filter(star => square.adjacentTo(star.coordinate))
  }

  override def toString: String = {
    val s = new StringBuilder
    for (x <- 0 until height*2 + 1) {
      s.append(stringLine(x))
      s.append("\n")
    }
    s.toString()
  }

  private def stringLine(x: Int): String = {
    val s = new StringBuilder
    for (y <- 0 until width*2 + 1) {
      val matchingStar = stars.find(s => s.coordinate == Coordinate(x,y))
      s.append(matchingStar match {
        case Some(star) => star.colour.symbol
        case _ => stringPoint(x,y)
      })
      s.append(if (x%2 == 0) HORIZ else " ")
    }
    s.toString().dropRight(1)
  }

  private def stringPoint(x: Int, y: Int): String = {
    val xEven = x%2 == 0
    val yEven = y%2 == 0
    if (xEven) {
      if (yEven) POINT else HORIZ
    } else {
      if (yEven) VERT else " "
    }
  }
}

object Board {

  val POINT = "·"
  val HORIZ = "‒"
  val VERT  = "|"

  def parse(lines: Seq[String]): Board = {
    val trimmed = lines.map(_.trim).filter(_.nonEmpty)
    val lengths = trimmed.map(_.trim.length).toSet
    if (lengths.size > 1) throw new IllegalArgumentException("Input has lines of mismatched lengths")
    val stars = parseStars(trimmed)
    Board(trimmed.size, lengths.head, stars)
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

  private def colourFromChar(char: Char): Colour = {
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
}

case class Square(row: Int, col: Int) {

  val centre : Coordinate = {
     Coordinate((row * 2) + 1, (col * 2) + 1)
  }

  def rotate(coord: Coordinate): Square = {
    val dx = coord.x - centre.x
    val dy = coord.y - centre.y
    Square.from(Coordinate(centre.x + (2 * dx), centre.y + (2 * dy)))
  }

  def adjacentTo(coord: Coordinate) : Boolean = {
    Math.abs(coord.x - centre.x) <= 1 && Math.abs(coord.y - centre.y) <= 1
  }
}
object Square {
  def from(coord: Coordinate): Square = {
    if(coord.x%2 == 0 || coord.y%2 == 0) throw new IllegalArgumentException("Not the centre of a square")
    Square(div2(coord.x-1), div2(coord.y-1))
  }
}

case class Coordinate(x: Int, y: Int) {

  val rows: Seq[Int] = {
    if (x%2 == 0) Seq(div2(x-2), div2(x)) else Seq(div2(x-1))
  }

  val cols: Seq[Int] = {
    if (y%2 == 0) Seq(div2(y-2), div2(y)) else Seq(div2(y-1))
  }
}

sealed trait Colour {
  val symbol: String
}

case object White extends Colour {
  override val symbol: String = "○"
}
case object Black extends Colour {
  override val symbol: String = "●"
}

case class Star(name: String, colour: Colour, coordinate: Coordinate)
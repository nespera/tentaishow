package uk.me.chrs.tentaishow

import MathUtil._

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

case class Board(height: Int, width: Int) {

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

sealed trait Colour

case object White extends Colour
case object Black extends Colour

case class Star(colour: Colour, coordinate: Coordinate)
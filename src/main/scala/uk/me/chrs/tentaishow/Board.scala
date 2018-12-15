package uk.me.chrs.tentaishow

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

  def isInside(square: Square): Boolean = {
    square.row >= 0 && square.row < height && square.col >= 0 && square.col < width
  }
}

case class Square(row: Int, col: Int) {
  def centre : Coordinate = {
     Coordinate((row * 2) + 1, (col * 2) + 1)
  }
}

case class Coordinate(x: Int, y: Int) {

  def rows: Seq[Int] = {
    if (x % 2 == 0) Seq(Math.floorDiv(x-2, 2), Math.floorDiv(x, 2)) else Seq(Math.floorDiv(x-1, 2))
  }
  def cols: Seq[Int] = {
    if (y % 2 == 0) Seq(Math.floorDiv(y-2, 2), Math.floorDiv(y, 2)) else Seq(Math.floorDiv(y-1, 2))
  }
}

sealed trait Colour

case object White extends Colour
case object Black extends Colour

case class Star(colour: Colour, coordinate: Coordinate)
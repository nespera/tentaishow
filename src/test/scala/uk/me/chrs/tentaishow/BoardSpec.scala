package uk.me.chrs.tentaishow

import org.specs2.mutable.Specification

class BoardSpec extends Specification {

  "A board" should {

    "detect squares inside the board" in {
      val board = Board(2, 6)
      board.isInside(Square(-1,0)) must beFalse
      board.isInside(Square(0,-1)) must beFalse
      board.isInside(Square(0,0)) must beTrue
      board.isInside(Square(1,5)) must beTrue
      board.isInside(Square(1,6)) must beFalse
      board.isInside(Square(2,5)) must beFalse
    }
  }

  "A coordinate" should {

    "know the rows adjacent to it" in {
      Coordinate(0,0).rows() mustEqual Seq(-1, 0)
      Coordinate(1,0).rows() mustEqual Seq(0)
      Coordinate(2,0).rows() mustEqual Seq(0, 1)
      Coordinate(3,0).rows() mustEqual Seq(1)
      Coordinate(4,0).rows() mustEqual Seq(1, 2)
    }

    "know the columns adjacent to it" in {
      Coordinate(0,0).cols() mustEqual Seq(-1, 0)
      Coordinate(0,1).cols() mustEqual Seq(0)
      Coordinate(0,2).cols() mustEqual Seq(0, 1)
      Coordinate(0,3).cols() mustEqual Seq(1)
      Coordinate(0,4).cols() mustEqual Seq(1, 2)
    }
  }

}

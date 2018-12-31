package uk.me.chrs.tentaishow

import org.specs2.mutable.Specification

class BoardSpec extends Specification {

  "A board" should {

    "detect squares inside the board" in {
      val board = Board(2, 6, Set())
      board.contains(Square(-1,0)) must beFalse
      board.contains(Square(0,-1)) must beFalse
      board.contains(Square(0,0)) must beTrue
      board.contains(Square(1,5)) must beTrue
      board.contains(Square(1,6)) must beFalse
      board.contains(Square(2,5)) must beFalse
    }

    "detect coordinates inside the board" in {
      val board = Board(2,2, Set())
      board.contains(Coordinate(0,0)) must beFalse
      board.contains(Coordinate(0,2)) must beFalse
      board.contains(Coordinate(1,3)) must beTrue
      board.contains(Coordinate(2,2)) must beTrue
      board.contains(Coordinate(4,2)) must beFalse
      board.contains(Coordinate(4,4)) must beFalse
    }

    "not be valid with stars not inside the board" in {
      Board(2, 2, Set()).validate()
      Board(2, 2, Set(Star("a", White, Coordinate(1,1)))).validate()
      Board(2, 2, Set(Star("a", White, Coordinate(2,2)))).validate()
      Board(2, 2, Set(Star("a", White, Coordinate(3,3)))).validate()
      Board(2, 2, Set(Star("a", White, Coordinate(0,2)))).validate() must throwAn[IllegalArgumentException]
      Board(2, 2, Set(Star("a", White, Coordinate(4,2)))).validate() must throwAn[IllegalArgumentException]
    }

    "not be valid with multiple stars next to single square" in {
      Board(2, 2, Set()).validate()
      Board(2, 2, Set(Star("a", White, Coordinate(1,1)), Star("a", White, Coordinate(1,3)))).validate()
      Board(2, 2, Set(Star("a", White, Coordinate(1,1)), Star("a", White, Coordinate(1,2)))).validate() must throwAn[IllegalArgumentException]
    }

    "not be initialized with lines of different lengths" in {
      Board.parse(Seq("..", "...")) must throwAn[IllegalArgumentException]
    }

    "be initialized with lines of equal lengths" in {
      val board = Board.parse(Seq("...", "..."))
      board mustEqual Board(2, 3, Set())
    }

    "ignore empty lines when initializing" in {
      val board = Board.parse(Seq("", "...", "...", ""))
      board mustEqual Board(2, 3, Set())
    }

    "ignore trailing whitespace when initializing" in {
      val board = Board.parse(Seq("  ", "...  ", "...", ""))
      board mustEqual Board(2, 3, Set())
    }

    "be initialized with letters" in {
      Board.parse(Seq("cC", "..")).stars.map(_.coordinate) mustEqual Set(Coordinate(1,1), Coordinate(1,3))
      Board.parse(Seq("bB", "..")).stars.map(_.coordinate) mustEqual Set(Coordinate(2,1), Coordinate(2,3))
      Board.parse(Seq("r.", "R.")).stars.map(_.coordinate) mustEqual Set(Coordinate(1,2), Coordinate(3,2))
      Board.parse(Seq("d.", "..")).stars.map(_.coordinate) mustEqual Set(Coordinate(2,2))
      Board.parse(Seq("D.", "..")).stars.map(_.coordinate) mustEqual Set(Coordinate(2,2))
    }

    "be initialized with stars with names starting a0,b0" in {
      val stars: Set[Star] = Board.parse(Seq("cC")).stars
      val names: Set[String] = stars.map(_.name)
      names mustEqual Set("a0", "b0")
    }

    "be initialized with stars that have different names" in {
      val stars: Set[Star] = Board.parse(Seq("cCcCcC","cCcCcC","cCcCcC","cCcCcC","cCcCcC")).stars
      val names: Set[String] = stars.map(_.name)
      names.size mustEqual stars.size
    }

    "be convertible to string" in {
      Board.parse(Seq("cC", "..")).toString mustEqual "·‒‒‒·‒‒‒·\n| ○ | ● |\n·‒‒‒·‒‒‒·\n|   |   |\n·‒‒‒·‒‒‒·\n"
      Board.parse(Seq("bB", "..")).toString mustEqual "·‒‒‒·‒‒‒·\n|   |   |\n·‒○‒·‒●‒·\n|   |   |\n·‒‒‒·‒‒‒·\n"
      Board.parse(Seq("r.", "R.")).toString mustEqual "·‒‒‒·‒‒‒·\n|   ○   |\n·‒‒‒·‒‒‒·\n|   ●   |\n·‒‒‒·‒‒‒·\n"
      Board.parse(Seq("d.", "..")).toString mustEqual "·‒‒‒·‒‒‒·\n|   |   |\n·‒‒‒○‒‒‒·\n|   |   |\n·‒‒‒·‒‒‒·\n"
      Board.parse(Seq("D.", "..")).toString mustEqual "·‒‒‒·‒‒‒·\n|   |   |\n·‒‒‒●‒‒‒·\n|   |   |\n·‒‒‒·‒‒‒·\n"
    }

    "be able to find squares adjacent to another" in {
      val board = Board.parse(Seq("..", ".."))
      board.adjacentSquares(Square(0,0)) mustEqual Set(Square(0,1), Square(1, 0))

      val tiny = Board.parse(Seq("."))
      tiny.adjacentSquares(Square(0,0)) mustEqual Set()
    }
  }

  "A square" should {
    "calculate its centre" in {
      Square(0, 0).centre mustEqual Coordinate(1,1)
      Square(0, 1).centre mustEqual Coordinate(1,3)
      Square(1, 0).centre mustEqual Coordinate(3,1)
      Square(1, 1).centre mustEqual Coordinate(3,3)
    }

    "be derived from a coordinate" in {
      Square.from(Square(0, 0).centre) mustEqual Square(0,0)
      Square.from(Square(0, 1).centre) mustEqual Square(0,1)
      Square.from(Square(1, 0).centre) mustEqual Square(1,0)
      Square.from(Square(1, 1).centre) mustEqual Square(1,1)

      Square.from(Coordinate(0,0)) must throwAn[IllegalArgumentException]
      Square.from(Coordinate(1,0)) must throwAn[IllegalArgumentException]
      Square.from(Coordinate(0,1)) must throwAn[IllegalArgumentException]
    }

    "know if it is adjacent to a coordinate" in {
      Square(1, 1).adjacentTo(Coordinate(3,3)) must beTrue
      Square(1, 1).adjacentTo(Coordinate(2,2)) must beTrue
      Square(1, 1).adjacentTo(Coordinate(4,4)) must beTrue
      Square(1, 1).adjacentTo(Coordinate(2,4)) must beTrue

      Square(1, 1).adjacentTo(Coordinate(1,3)) must beFalse
      Square(1, 1).adjacentTo(Coordinate(2,5)) must beFalse
      Square(1, 1).adjacentTo(Coordinate(1,4)) must beFalse
      Square(1, 1).adjacentTo(Coordinate(2,1)) must beFalse
    }

    "know if it is adjacent to another square" in {
      Square(1, 1).adjacentTo(Square(1,1)) must beTrue
      Square(1, 1).adjacentTo(Square(0,1)) must beTrue
      Square(1, 1).adjacentTo(Square(1,0)) must beTrue
      Square(1, 1).adjacentTo(Square(2,1)) must beTrue
      Square(1, 1).adjacentTo(Square(1,2)) must beTrue

      Square(1, 1).adjacentTo(Square(2,2)) must beFalse
      Square(1, 1).adjacentTo(Square(0,0)) must beFalse
      Square(1, 1).adjacentTo(Square(1,4)) must beFalse
      Square(1, 1).adjacentTo(Square(5,1)) must beFalse
    }

    "be able to be rotated about a coordinate" in {
      Square(0,0).rotate(Coordinate(1,1)) mustEqual Square(0,0)
      Square(0,0).rotate(Coordinate(1,2)) mustEqual Square(0,1)
      Square(0,0).rotate(Coordinate(2,2)) mustEqual Square(1,1)
      Square(0,0).rotate(Coordinate(3,1)) mustEqual Square(2,0)
    }
  }

  "A coordinate" should {

    "know the rows adjacent to it" in {
      Coordinate(0,0).rows mustEqual Seq(-1, 0)
      Coordinate(1,0).rows mustEqual Seq(0)
      Coordinate(2,0).rows mustEqual Seq(0, 1)
      Coordinate(3,0).rows mustEqual Seq(1)
      Coordinate(4,0).rows mustEqual Seq(1, 2)
    }

    "know the columns adjacent to it" in {
      Coordinate(0,0).cols mustEqual Seq(-1, 0)
      Coordinate(0,1).cols mustEqual Seq(0)
      Coordinate(0,2).cols mustEqual Seq(0, 1)
      Coordinate(0,3).cols mustEqual Seq(1)
      Coordinate(0,4).cols mustEqual Seq(1, 2)
    }
  }

}

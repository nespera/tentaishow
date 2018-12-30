package uk.me.chrs.tentaishow

import org.specs2.mutable.Specification

class GameSpec extends Specification {

  "A game" should {

    "not be initialized with stars not inside the board" in {
      val board = Board(2, 2)
      Game.init(board, Set())
      Game.init(board, Set(Star("a", White, Coordinate(1,1))))
      Game.init(board, Set(Star("a", White, Coordinate(2,2))))
      Game.init(board, Set(Star("a", White, Coordinate(3,3))))
      Game.init(board, Set(Star("a", White, Coordinate(0,2)))) must throwAn[IllegalArgumentException]
      Game.init(board, Set(Star("a", White, Coordinate(4,2)))) must throwAn[IllegalArgumentException]
    }

    "not be initialized with multiple stars next to single square" in {
      val board = Board(2, 2)
      Game.init(board, Set())
      Game.init(board, Set(Star("a", White, Coordinate(1,1)), Star("a", White, Coordinate(1,3))))
      Game.init(board, Set(Star("a", White, Coordinate(1,1)), Star("a", White, Coordinate(1,2)))) must throwAn[IllegalArgumentException]
    }

    "not be initialized with lines of different lengths" in {
      Game.init(Seq("..", "...")) must throwAn[IllegalArgumentException]
    }

    "be initialized with lines of equal lengths" in {
      val game = Game.init(Seq("...", "..."))
      game.board mustEqual Board(2, 3)
    }

    "ignore empty lines when initializing" in {
      val game = Game.init(Seq("", "...", "...", ""))
      game.board mustEqual Board(2, 3)
    }

    "ignore trailing whitespace when initializing" in {
      val game = Game.init(Seq("  ", "...  ", "...", ""))
      game.board mustEqual Board(2, 3)
    }

    "be initialized with letters" in {
      Game.init(Seq("cC", "..")).stars.map(_.coordinate) mustEqual Set(Coordinate(1,1), Coordinate(1,3))
      Game.init(Seq("bB", "..")).stars.map(_.coordinate) mustEqual Set(Coordinate(2,1), Coordinate(2,3))
      Game.init(Seq("r.", "R.")).stars.map(_.coordinate) mustEqual Set(Coordinate(1,2), Coordinate(3,2))
      Game.init(Seq("d.", "..")).stars.map(_.coordinate) mustEqual Set(Coordinate(2,2))
      Game.init(Seq("D.", "..")).stars.map(_.coordinate) mustEqual Set(Coordinate(2,2))
    }

    "be initialized with stars with names starting a0,b0" in {
      val stars: Set[Star] = Game.init(Seq("cC")).stars
      val names: Set[String] = stars.map(_.name)
      names mustEqual Set("a0", "b0")
    }

    "be initialized with stars that have different names" in {
      val stars: Set[Star] = Game.init(Seq("cCcCcC","cCcCcC","cCcCcC","cCcCcC","cCcCcC")).stars
      val names: Set[String] = stars.map(_.name)
      names.size mustEqual stars.size
    }
    
    "be convertible to a star map" in {
      Game.init(Seq("cC", "..")).starMap mustEqual "·‒‒‒·‒‒‒·\n| ○ | ● |\n·‒‒‒·‒‒‒·\n|   |   |\n·‒‒‒·‒‒‒·\n"
      Game.init(Seq("bB", "..")).starMap mustEqual "·‒‒‒·‒‒‒·\n|   |   |\n·‒○‒·‒●‒·\n|   |   |\n·‒‒‒·‒‒‒·\n"
      Game.init(Seq("r.", "R.")).starMap mustEqual "·‒‒‒·‒‒‒·\n|   ○   |\n·‒‒‒·‒‒‒·\n|   ●   |\n·‒‒‒·‒‒‒·\n"
      Game.init(Seq("d.", "..")).starMap mustEqual "·‒‒‒·‒‒‒·\n|   |   |\n·‒‒‒○‒‒‒·\n|   |   |\n·‒‒‒·‒‒‒·\n"
      Game.init(Seq("D.", "..")).starMap mustEqual "·‒‒‒·‒‒‒·\n|   |   |\n·‒‒‒●‒‒‒·\n|   |   |\n·‒‒‒·‒‒‒·\n"
    }

    "be convertible to string with empty squares" in {
      val game = Game.init(Board(2, 2), Set())
      val asString = "·‒‒‒·‒‒‒·\n|   |   |\n·‒‒‒·‒‒‒·\n|   |   |\n·‒‒‒·‒‒‒·\n"
      game.toString mustEqual  asString
    }

    "be convertible to string with filled squares" in {
      val starA = Star("aaaAA", White, Coordinate(1,1))
      val starB = Star("b", Black, Coordinate(3,3))

      val game = Game.init(Board(2, 2), Set(starA, starB))
      val newState = (game.state + (Square(0,0) -> Some(starA))) + (Square(1,1) -> Some(starB))
      val newGame = game.copy(state = newState)

      val asString = "·‒‒‒·‒‒‒·\n|aaa|   |\n·‒‒‒·‒‒‒·\n|   |  b|\n·‒‒‒·‒‒‒·\n"
      newGame.toString mustEqual  asString
    }
  }
}

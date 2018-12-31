package uk.me.chrs.tentaishow

import org.specs2.mutable.Specification

class GameSpec extends Specification {

  "A game" should {

    "cannot be initialized with an invalid board" in {
      val board = Board(2, 2, Set(Star("a", White, Coordinate(0,0))))
      board.validate() must throwAn[IllegalArgumentException]
      Game.init(board) must throwAn[IllegalArgumentException]
    }

    "is initialized with empty state" in {
      val game = Game.init(Board(2, 2, Set()))
      game.state.keySet mustEqual game.board.squares.toSet
      game.state.values.toSet mustEqual Set(None)
    }

    "be convertible to string with empty squares" in {
      val game = Game.init(Board(2, 2, Set()))
      val asString = "·‒‒‒·‒‒‒·\n|   |   |\n·‒‒‒·‒‒‒·\n|   |   |\n·‒‒‒·‒‒‒·\n"
      game.toString mustEqual asString
    }

    "be convertible to string with filled squares" in {
      val starA = Star("aaaAA", White, Coordinate(1,1))
      val starB = Star("b", Black, Coordinate(3,3))

      val game = Game.init(Board(2, 2, Set(starA, starB)))
      val newState = (game.state + (Square(0,0) -> Some(starA))) + (Square(1,1) -> Some(starB))
      val newGame = game.copy(state = newState)

      val asString = "·‒‒‒·‒‒‒·\n|aaa|   |\n·‒‒‒·‒‒‒·\n|   |  b|\n·‒‒‒·‒‒‒·\n"
      newGame.toString mustEqual  asString
    }

    "Know when it is not complete" in {
      val starA = Star("a", White, Coordinate(1,1))
      val starB = Star("b", Black, Coordinate(3,3))

      val game = Game.init(Board(2, 2, Set(starA, starB)))

      game.isComplete must beFalse
    }

    "Know when it is complete" in {
      val starA = Star("a", White, Coordinate(2,2))

      val game = Game.init(Board(2, 2, Set(starA)))
      val newState = game.state.map(x => x._1 -> Some(starA))
      val newGame = game.copy(state = newState)

      newGame.isComplete must beTrue
    }

  }
}

package uk.me.chrs.tentaishow

import org.specs2.mutable.Specification

class GameSpec extends Specification {

  "A game" should {

    "be convertible to string with empty squares" in {
      val game = Game.init(Board(2, 2, Set()))
      val asString = "·‒‒‒·‒‒‒·\n|   |   |\n·‒‒‒·‒‒‒·\n|   |   |\n·‒‒‒·‒‒‒·\n"
      game.toString mustEqual  asString
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
  }
}

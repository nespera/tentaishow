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

    "be convertible to string with empty squares" in {
      val game = Game.init(Board(2, 2), Set())
      val asString = "•‒‒•‒‒•\n|  |  |\n•‒‒•‒‒•\n|  |  |\n•‒‒•‒‒•\n"
      game.toString mustEqual  asString
    }

    "be convertible to string with filled squares" in {
      val starA = Star("aaAA", White, Coordinate(1,1))
      val starB = Star("b", Black, Coordinate(3,3))

      val game = Game.init(Board(2, 2), Set(starA, starB))
      val newState = (game.state + (Square(0,0) -> Some(starA))) + (Square(1,1) -> Some(starB))
      val newGame = game.copy(state = newState)

      val asString = "•‒‒•‒‒•\n|aa|  |\n•‒‒•‒‒•\n|  | b|\n•‒‒•‒‒•\n"
      newGame.toString mustEqual  asString
    }
  }
}

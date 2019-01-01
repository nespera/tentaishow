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

    "be convertible to image" in {
      val starA = Star("a", White, Coordinate(1,2))
      val starB = Star("b", Black, Coordinate(3,2))

      val game = Game.init(Board(2, 2, Set(starA, starB)))
      val newState = (game.state + (Square(0,0) -> Some(starA))) +
        (Square(1,0) -> Some(starB)) +
        (Square(1,1) -> Some(starB))
      val newGame = game.copy(state = newState)

      val asString = s"${White.fill}  \n${Black.fill}${Black.fill}\n"
      newGame.asImage mustEqual  asString
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

    "Be able to fill in the initial gimmes (squares adjacent to a star)" in {
      val game = Game.init(Board.parse(Seq("d.", "..")))
      game.isComplete must beFalse
      val withGimmes = game.fillGimmes
      withGimmes.isComplete must beTrue
    }

    "Be able to solve a board that is just gimmes" in {
      val game = Game.init(Board.parse(Seq("cC", "Cc")))
      game.isComplete must beFalse
      val solved = Game.solve(game).get
      solved.isComplete must beTrue
    }

    "Be able to solve a board that is gimmes and unique squares" in {
      val game = Game.init(Board.parse(Seq(".C.", ".c.")))
      game.isComplete must beFalse
      val solved = Game.solve(game).get
      solved.isComplete must beTrue
    }

    "Be able to calculate reachable stars from a square" in {
      val game = Game.init(Board.parse(Seq("cc.", ".c."))).fillGimmes
      val starA = game.board.adjacentStars(Square(0, 0)).head
      val starB = game.board.adjacentStars(Square(0, 1)).head
      val starC = game.board.adjacentStars(Square(1, 1)).head
      game.reachable(Square(0,0)) mustEqual Set(starA)
      game.reachable(Square(0,2)) mustEqual Set(starB, starC)
      game.reachable(Square(1,0)) mustEqual Set(starA, starC)
    }
  }
}

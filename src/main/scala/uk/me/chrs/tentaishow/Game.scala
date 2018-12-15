package uk.me.chrs.tentaishow

case class Game(board: Board, stars: Set[Star], state: Map[Square, Option[Star]])

object Game {

  def init(board: Board, stars: Set[Star]): Game = {
    stars.find(s => !board.contains(s.coordinate))
      .foreach(s => throw new IllegalArgumentException("Initialized with Star not inside the board"))

    board.squares.find(square => {
      val adjacentStars = stars.filter(star => square.adjacentTo(star.coordinate))
      adjacentStars.size > 1
    }).foreach(s => throw new IllegalArgumentException("Initialized with multiple stars adjacent to a single square"))

    val initState = board.squares.map(sq => (sq, Option.empty)).toMap
    Game(board, stars, initState)
  }
}
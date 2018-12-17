package uk.me.chrs.tentaishow

case class Game(board: Board, stars: Set[Star], state: Map[Square, Option[Star]]) {

  private val POINT = "•"
  private val HORIZ = "‒"
  private val VERT  = "|"

  def print(): Unit = {
    for(r <- 0 until board.height) {
      println(gridLine())
      for (c <- 0 until board.width) {
        printf(VERT)
        val fill = state(Square(r, c)).map(star => star.name).getOrElse(" ")
        printf("%2s", fill)
      }
      println(VERT)
    }
    println(gridLine())
  }

  private def gridLine(): String = {
    (0 until board.width).map(i => POINT + HORIZ + HORIZ).mkString("") + POINT
  }
}

object Game extends App {

  val game = init(Board(2,2), Set())
  game.print()

  def init(board: Board, stars: Set[Star]): Game = {
    stars.find(s => !board.contains(s.coordinate))
      .foreach(s => throw new IllegalArgumentException("Initialized with Star not inside the board"))

    board.squares.find(square => {
      val adjacentStars = stars.filter(star => square.adjacentTo(star.coordinate))
      adjacentStars.size > 1
    }).foreach(s => throw new IllegalArgumentException("Initialized with multiple stars adjacent to a single square"))

    val initState = board.squares.map(sq => (sq, None)).toMap
    Game(board, stars, initState)
  }
}
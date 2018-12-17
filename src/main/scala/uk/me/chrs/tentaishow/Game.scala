package uk.me.chrs.tentaishow

case class Game(board: Board, stars: Set[Star], state: Map[Square, Option[Star]]) {

  private val POINT = "•"
  private val HORIZ = "‒"
  private val VERT  = "|"

   override def toString: String = {
    val s = new StringBuilder
    for (r <- 0 until board.height) {
      s.append(gridLine())
      s.append(rowAsString(r))
    }
    s.append(gridLine())
    s.toString()
   }

  private def rowAsString(r: Int): String = {
    val s = new StringBuilder
    for (c <- 0 until board.width) {
      val fill = state(Square(r, c)).map(star => star.name.take(2)).getOrElse(" ")
      s.append(VERT + f"$fill%2s")
    }
    s.append(VERT + "\n")
    s.toString()
  }

  private def gridLine(): String = {
    (0 until board.width).map(i => POINT + HORIZ + HORIZ).mkString("") + POINT + "\n"
  }
}

object Game extends App {

  val game = init(Board(2,2), Set())
  Console.print(game)

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
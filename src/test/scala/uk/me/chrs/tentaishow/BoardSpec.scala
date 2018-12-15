package uk.me.chrs.tentaishow

import org.specs2.mutable.Specification

class BoardSpec extends Specification {

  "A board" should {

    "do something" in {
      new Board(1, 1) must not(beEqualTo(0))
    }
  }

}

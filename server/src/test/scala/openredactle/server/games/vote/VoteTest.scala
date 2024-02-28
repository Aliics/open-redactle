package openredactle.server.games.vote

import openredactle.shared.vote.VoteStatus.*
import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.matchers.should.Matchers

import java.util.UUID

class VoteTest extends AnyFunSuiteLike with Matchers:
  test("should have inactive status if no voting has taken place"):
    val vote = Vote()
    vote.currentStatus shouldBe InActive()

  test("should have requirements when vote has started"):
    val vote = Vote()

    vote startVoting Seq(UUID.randomUUID)

    vote.currentStatus shouldBe Active(0, 1)

  test("should succeed when voting goes over threshold"):
    val vote = Vote()
    val player1Id = UUID.randomUUID

    vote startVoting Seq(player1Id, UUID.randomUUID)
    vote.addPlayerVote(player1Id, true)

    vote.currentStatus shouldBe Success()

  test("should fail when voting goes over threshold"):
    val vote = Vote()
    val player1Id = UUID.randomUUID

    vote startVoting Seq(player1Id, UUID.randomUUID)
    vote.addPlayerVote(player1Id, false)

    vote.currentStatus shouldBe Failure()

  test("should change needed to be higher when more players are added"):
    val vote = Vote()
    val player1Id = UUID.randomUUID

    vote startVoting Seq(player1Id, UUID.randomUUID, UUID.randomUUID)
    vote.addPlayerVote(player1Id, false)
    vote recalculateRequirements Seq(player1Id, UUID.randomUUID, UUID.randomUUID, UUID.randomUUID, UUID.randomUUID)

    vote.currentStatus shouldBe Active(1, 3)

  test("should change needed to be lower when more players are removed"):
    val vote = Vote()
    val player1Id = UUID.randomUUID

    vote startVoting Seq(player1Id, UUID.randomUUID, UUID.randomUUID)
    vote.addPlayerVote(player1Id, false)
    vote recalculateRequirements Seq(player1Id, UUID.randomUUID)

    vote.currentStatus shouldBe Failure() // Failure in this case, because player1 already voted against the give up.

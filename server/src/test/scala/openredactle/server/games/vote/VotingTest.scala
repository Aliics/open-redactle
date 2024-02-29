package openredactle.server.games.vote

import openredactle.shared.vote.VoteStatus.*
import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.matchers.should.Matchers

import java.util.UUID

class VotingTest extends AnyFunSuiteLike with Matchers:
  test("should have inactive status if no voting has taken place"):
    val voting = Voting()
    voting.currentStatus shouldBe InActive()

  test("should have requirements when vote has started"):
    val voting = Voting()

    voting startVoting Seq(UUID.randomUUID)

    voting.currentStatus shouldBe Active(0, 1)

  test("should succeed when voting goes over threshold"):
    val voting = Voting()
    val player1Id = UUID.randomUUID

    voting startVoting Seq(player1Id)
    voting.addPlayerVote(player1Id, true)

    voting.currentStatus shouldBe Success()

  test("should fail when voting goes over threshold"):
    val voting = Voting()
    val player1Id = UUID.randomUUID

    voting startVoting Seq(player1Id)
    voting.addPlayerVote(player1Id, false)

    voting.currentStatus shouldBe Failure()

  test("should change needed to be higher when more players are added"):
    val voting = Voting()
    val player1Id = UUID.randomUUID

    voting startVoting Seq(player1Id, UUID.randomUUID)
    voting.addPlayerVote(player1Id, false)
    voting recalculateRequirements Seq(player1Id, UUID.randomUUID, UUID.randomUUID)

    voting.currentStatus shouldBe Active(1, 3)

  test("should change needed to be lower when more players are removed"):
    val voting = Voting()
    val player1Id = UUID.randomUUID

    voting startVoting Seq(player1Id, UUID.randomUUID, UUID.randomUUID)
    voting.addPlayerVote(player1Id, false)
    voting recalculateRequirements Seq(player1Id)

    voting.currentStatus shouldBe Failure() // Failure in this case, because player1 already voted against the give up.

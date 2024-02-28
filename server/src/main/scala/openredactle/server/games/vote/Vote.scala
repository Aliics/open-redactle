package openredactle.server.games.vote

import openredactle.shared.vote.VoteStatus
import openredactle.shared.vote.VoteStatus.*

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger}
import scala.jdk.CollectionConverters.*

class Vote:
  private val voting = AtomicBoolean(false)
  private val votesNeededCount = AtomicInteger(0)
  private val votes = ConcurrentHashMap[UUID, Boolean]()

  def startVoting(playerIds: Seq[UUID]): VoteStatus =
    calculateNeeded(playerIds.size)
    voting.set(true)

    Active(votes.size, votesNeededCount.get)

  def stopVoting: VoteStatus =
    voting.set(false)
    votesNeededCount set 0
    votes.clear()
    InActive()

  def addPlayerVote(playerId: UUID, want: Boolean): VoteStatus =
    votes.put(playerId, want)
    currentStatus

  def recalculateRequirements(playerIds: Seq[UUID]): VoteStatus =
    val filtered = votes.asScala.filter(playerIds contains _._1)
    votes.clear()
    votes.putAll(filtered.asJava)
    calculateNeeded(playerIds.size)
    currentStatus

  def currentStatus: VoteStatus =
    if !voting.get then InActive()
    else
      val needed = votesNeededCount.get

      if votes.size < needed then
        VoteStatus.Active(votes.size, needed)
      else if votes.asScala.count(_._2) >= needed then
        Success()
      else
        Failure()

  private def calculateNeeded(playerCount: Int): Unit =
    votesNeededCount set math.ceil(playerCount.toDouble / 2).toInt

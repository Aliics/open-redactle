package openredactle.server.games.vote

import openredactle.shared.vote.VoteStatus
import openredactle.shared.vote.VoteStatus.*

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger}
import scala.jdk.CollectionConverters.*

class Voting:
  private val voting = AtomicBoolean(false)
  private val votesNeededCount = AtomicInteger(0)
  private val votes = ConcurrentHashMap[UUID, Boolean]()
  private val locked = AtomicBoolean(false)

  def startVoting(playerIds: Seq[UUID]): VoteStatus =
    if locked.get || voting.get() then currentStatus
    else
      votesNeededCount.compareAndSet(0, playerIds.size)
      voting.compareAndSet(false, true)

      Active(votes.size, votesNeededCount.get)

  def stopVoting(): VoteStatus =
    if locked.get() then currentStatus
    else
      voting.set(false)
      votesNeededCount set 0
      votes.clear()
      InActive()

  def addPlayerVote(playerId: UUID, want: Boolean): VoteStatus =
    if locked.get() then currentStatus
    else
      votes.put(playerId, want)
      currentStatus

  def recalculateRequirements(playerIds: Seq[UUID]): VoteStatus =
    if locked.get() then currentStatus
    else
      val filtered = votes.asScala.filter(playerIds contains _._1)
      votes.clear()
      votes.putAll(filtered.asJava)
      votesNeededCount set playerIds.size
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
        
  def lock(): Unit = locked.compareAndSet(false, true)

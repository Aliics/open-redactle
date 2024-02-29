package openredactle.webapp.settings.vote

import com.raquo.laminar.api.L.{*, given}
import openredactle.shared.vote.VoteStatus
import openredactle.shared.vote.VoteStatus.*
import openredactle.webapp.element.RenderableElement
import openredactle.webapp.game.Game
import openredactle.webapp.{Colors, colored, layoutFlex, solidBorder}

object GiveUpBanner extends RenderableElement:
  val status: Var[VoteStatus] = Var(InActive())

  override lazy val renderElement: Element =
    div(
      borderTop := solidBorder(),
      display <-- status.signal.map:
        case InActive() => "none"
        case _ => "block"
      ,
      backgroundColor <-- status.signal.map:
        case Success() => Colors.danger
        case _ => Colors.secondary
      ,
      padding := "1rem",
      paddingTop := "0",
      paddingBottom := "0.5rem",

      h3("Would you like to give up on solving this article?"),

      p(
        child.text <-- status.signal.map:
          case InActive() => "Voting isn't going"
          case Active(votes, needed) =>
            s"$votes of $needed players have voted. ${needed - votes} more votes needed to determine if we give up."
          case Success() => "We have given up all hope!"
          case Failure() => "We will prevail!"
      ),

      child <-- status.signal.map:
        case Success() =>
          emptyNode
        case _ =>
          div(
            layoutFlex(),
            columnGap := "0.5rem",

            button(
              colored(bgColor = Colors.action),
              onClick --> (_ => Game.addGiveUpVote(true)),
              "Yes",
            ),
            button(
              colored(bgColor = Colors.danger),
              onClick --> (_ => Game.addGiveUpVote(false)),
              "No",
            ),
          )
    )

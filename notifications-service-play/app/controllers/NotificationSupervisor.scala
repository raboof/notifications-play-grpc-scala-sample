package controllers

import akka.actor.{Actor, Props}
import NotificationSupervisor.CreateNotificationTarget

object NotificationSupervisor {
  case class CreateNotificationTarget(id: Long)

  def props(): Props = Props(new NotificationSupervisor)
}

class NotificationSupervisor extends Actor {

  def receive = {
    case CreateNotificationTarget(id) =>
      val name = id.toString
      if (context.child(name).isEmpty) {
        context.actorOf(NotificationTarget.props(), id.toString)
      }
  }

}

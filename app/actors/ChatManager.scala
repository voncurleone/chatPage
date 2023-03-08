package actors

import actors.ChatActor.Msg
import actors.ChatManager.{AddUser, SendMessage, UserDropped}
import akka.actor.{Actor, ActorRef, Props}
import controllers.WebProtocol

class ChatManager() extends Actor {
  private var users = List.empty[ActorRef]
  private var usernames = List.empty[String]
  //todo: watch users for actor termination, then remove from list

  override def receive: Receive = {
    case AddUser(user, username) =>
      users = user :: users
      usernames = username :: usernames
      context.watchWith(user, UserDropped(user))

    case SendMessage(un, text) => for( u <- users ) {
      u ! Msg(un + ": " + text)
    }

    case UserDropped(user) =>
      val index = users.indexOf(user)
      users = users.filter(_ != user)
      for (u <- users) {
        u ! Msg(usernames(index) + ": disconnected!")
      }

      usernames = usernames.filter(_ != usernames(index))

    case m => println("Unhandled by chatManager: " + m)
  }
}

object ChatManager {
  trait Protocol extends WebProtocol
  case class AddUser(user: ActorRef, username: String) extends Protocol
  case class SendMessage(user: String, text: String) extends Protocol
  case class UserDropped(user: ActorRef) extends Protocol

  def props() = Props(new ChatManager())
}

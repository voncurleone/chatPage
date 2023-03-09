package actors

import actors.ChatActor.Msg
import actors.ChatManager.{AddUser, SendMessage, UserDropped}
import akka.actor.{Actor, ActorRef, Props}
import controllers.WebProtocol

class ChatManager() extends Actor {
  private var users = List.empty[ActorRef]
  private var usernames = List.empty[String]

  //log
  private def log(msg: String): Unit = println("[ChatManager] " + msg)

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
      val username = usernames(index)
      //log("Before: " + users)
      //log("Before: " + usernames)

      users = users.filter(_ != user)
      usernames = usernames.take(index) ::: usernames.drop(index + 1)
      if(!usernames.contains(username)) {
        for (u <- users) {
          u ! Msg(username + ": disconnected!")
        }
      }

      //log("After: " + users)
      //log("After: " + usernames)

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

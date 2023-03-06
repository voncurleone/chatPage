package actors

import actors.ChatActor.Msg
import actors.ChatManager.{AddUser, SendMessage}
import akka.actor.{Actor, ActorRef, Props}
import controllers.WebProtocol

class ChatManager() extends Actor {
  private var users = List.empty[ActorRef]

  override def receive: Receive = {
    case AddUser(user) =>
      users = user :: users
    case SendMessage(un, text) => for( u <- users ) {
      u ! Msg(un + ": " + text)
    }
    case m => println("Unhandled by chatManager: " + m)
  }
}

object ChatManager {
  trait Protocol extends WebProtocol
  case class AddUser(user: ActorRef) extends Protocol
  case class SendMessage(user: String, text: String)

  def props() = Props(new ChatManager())
}

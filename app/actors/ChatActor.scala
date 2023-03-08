package actors

import actors.ChatActor.Msg
import actors.ChatManager.SendMessage
import akka.actor.{Actor, ActorRef, PoisonPill, Props}
import controllers.WebProtocol

class ChatActor(client: ActorRef, manager: ActorRef, username: String) extends Actor {
  manager ! ChatManager.AddUser(self, username)
  //todo: watch client for terminating, if it does.. kill this actor too
  //context.watchWith(client, ClientDropped)

  override def receive: Receive = {
    case msg: String =>
      manager ! ChatManager.SendMessage(username, msg)
    case Msg(text) => client ! text
    case m => println("Unhandled by ChatActor: " + m)
  }
}

object ChatActor {
  trait Protocol extends WebProtocol
  case class Msg(text: String) extends Protocol

  def props(client: ActorRef, manager: ActorRef, username: String) = {
    Props(new ChatActor(client, manager, username))
  }
}

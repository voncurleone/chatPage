package actors

import actors.ChatActor.Msg
import actors.ChatManager.SendMessage
import akka.actor.ActorRef
import akka.actor.{Actor, Props}
import controllers.WebProtocol

class ChatActor(client: ActorRef, manager: ActorRef) extends Actor {
  manager ! ChatManager.AddUser(self)

  override def receive: Receive = {
    case msg: String =>
      val un = msg.takeWhile(_ != '$')
      manager ! ChatManager.SendMessage(un, msg.takeRight(msg.length - un.length))

    case Msg(text) => client ! text
    case m => println(m)
  }
}

object ChatActor {
  trait Protocol extends WebProtocol
  case class Msg(text: String) extends Protocol

  def props(client: ActorRef, manager: ActorRef) = {
    Props(new ChatActor(client, manager))
  }
}

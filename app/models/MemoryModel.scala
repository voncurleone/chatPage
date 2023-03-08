package models

import akka.actor.ActorRef
import models.ValidationResponse.{ValidationResponse, invalid, logged, valid}

import scala.collection.mutable
import scala.collection.mutable.{Map, Set}

object ValidationResponse extends Enumeration {
  type ValidationResponse = Value
  val valid, logged, invalid = Value
  //valid login response means that user status will be true
}

object MemoryModel {
  private val users = mutable.Map[String, String]("tim" -> "pass", "chris" -> "pass")
  private val userStatus = mutable.Map[String, Boolean]("tim" -> false, "chris" -> false)

  //actors
  private val managers: mutable.Set[ActorRef] = mutable.Set.empty[ActorRef]

  def addManager(manager: ActorRef): Boolean = {
    managers += manager
    true
  }

  def validateLogin(username: String, password: String): ValidationResponse = {
    if(users.get(username).contains(password)) {
      if(userStatus.get(username).contains(false)) {
        userStatus(username) = true
        valid
      } else {
        logged
      }
    } else {
      invalid
    }
  }

  def logout(username: String): Boolean = {
    /*
    * Chat Manager will be responsible for removing the actor from its user
    */
    userStatus(username) = false
    true
  }
  def getManager(name: String): Option[ActorRef] = managers.find(_.path.name == name)
}

package models

import scala.collection.mutable.Map

object MemoryModel {
  private val users = Map[String, String]("tim" -> "pass", "chris" -> "pass")
  private val userStatus = Map[String, Boolean]("tim" -> false, "chris" -> false)

  def login(username: String, password: String): Boolean = ???
  def logout(username: String): Boolean = ???
}

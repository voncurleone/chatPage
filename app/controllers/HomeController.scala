package controllers

import actors.{ChatActor, ChatManager}
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.stream.Materializer

import javax.inject._
import play.api._
import play.api.libs.json.{JsError, JsSuccess, Json, Reads}
import play.api.libs.streams.ActorFlow
import play.api.mvc._


trait WebProtocol

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val cc: ControllerComponents)(implicit system: ActorSystem, mat: Materializer) extends AbstractController(cc) {
  private var users = List.empty[String]
  private val manager: ActorRef = system.actorOf(ChatManager.props(), "Manager")

  private def withJson[A](f: A => Result)(implicit request: Request[AnyContent], reads: Reads[A]) = {
    request.body.asJson.map { body =>
      Json.fromJson[A](body)(reads) match {
        case JsSuccess(value, path) => f(value)
        case JsError(errors) => Redirect(routes.HomeController.index())
      }
    }.getOrElse(Redirect(routes.HomeController.index()))
  }

  def index() = Action { implicit request =>
    Ok(views.html.index())
  }

  def socket = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef( out =>
      ChatActor.props(out, manager)
    )
  }

  def validateLogin = Action { implicit request =>
    withJson[String] { data =>
      if(!users.contains(data)) {
        users = data :: users
        Ok(Json.toJson(true))
          .withSession("username" -> data, "csrfToken" -> play.filters.csrf.CSRF.getToken.get.value)
      } else {
        Ok(Json.toJson(false))
      }
    }
  }
}

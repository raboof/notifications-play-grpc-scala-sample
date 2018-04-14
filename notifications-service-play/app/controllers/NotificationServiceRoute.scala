package controllers

import javax.inject.{ Inject, Singleton }

import akka.stream.Materializer

import play.api.routing.Router
import play.api.routing.Router.Routes
import play.core.server.akkahttp.AkkaHttpHandler

import notifications.grpc._

@Singleton
class NotificationServiceRoute @Inject() (impl: NotificationService)(implicit mat: Materializer) extends Router {

  val handler = AkkaHttpHandler(NotificationServiceApiHandler(impl))

  override def routes: Routes = { case _ ⇒ handler }

  override def documentation: Seq[(String, String, String)] = Seq.empty

  override def withPrefix(prefix: String): Router = this
}

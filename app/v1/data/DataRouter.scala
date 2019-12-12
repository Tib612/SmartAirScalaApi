package v1.data

import javax.inject.Inject

import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

/**
  * Routes and URLs to the DataResource controller.
  */
class DataRouter @Inject()(controller: DataController) extends SimpleRouter {
  val prefix = "/v1/datas"

  def link(id: Int): String = {
    import io.lemonlabs.uri.dsl._
    val url = prefix / id.toString()
    url.toString()
  }

  override def routes: Routes = {
    case GET(p"/") =>
      controller.index

    case POST(p"/") =>
      controller.process
  }

}

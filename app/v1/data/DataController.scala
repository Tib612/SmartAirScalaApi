package v1.data

import javax.inject.Inject

import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._
import java.util.Date
import play.api.data.format.Formats._
import scala.concurrent.{ExecutionContext, Future}

case class DataFormInput(
    brightness: Int,
    temperature: Int,
    noise_pollution: Double,
    pollution: String,
    humidity: Double,
    latitude: Int,
    longitude: Int
)

/**
  * Takes HTTP requests and produces JSON.
  */
class DataController @Inject()(cc: DataControllerComponents)(
    implicit ec: ExecutionContext
) extends DataBaseController(cc) {

  private val logger = Logger(getClass)

  private val form: Form[DataFormInput] = {
    import play.api.data.Forms._

    Form(
      mapping(
        "brightness" -> number,
        "temperature" -> number,
        "noise_pollution" -> of[Double],
        "pollution" -> nonEmptyText,
        "humidity" -> of[Double],
        "latitude" -> number,
        "longitude" -> number
      )(DataFormInput.apply)(DataFormInput.unapply)
    )
  }

  def index: Action[AnyContent] = DataAction.async { implicit request =>
    logger.trace("index: ")
    DataResourceHandler.find.map { Datas =>
      Ok(Json.toJson(Datas))
    }
  }

  def process: Action[AnyContent] = DataAction.async { implicit request =>
    logger.trace("process: ")
    processJsonData()
  }

  private def processJsonData[A]()(
      implicit request: DataRequest[A]
  ): Future[Result] = {
    def failure(badForm: Form[DataFormInput]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    def success(input: DataFormInput) = {
      DataResourceHandler.create(input).map { Data =>
        Created(Json.toJson(Data)).withHeaders(LOCATION -> Data.link)
      }
    }

    form.bindFromRequest().fold(failure, success)
  }
}

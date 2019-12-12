package v1.data

import javax.inject.{Inject, Provider}

import play.api.MarkerContext
import java.util.{Date, Calendar}

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._

/**
  * DTO for displaying Data information.
  */
case class DataResource(
    id: String,
    link: String,
    time: Date,
    brightness: Int,
    temperature: Int,
    noise_pollution: Double,
    pollution: String,
    humidity: Double,
    latitude: Int,
    longitude: Int
)

object DataResource {

  /**
    * Mapping to read/write a DataResource out as a JSON value.
    */
  implicit val format: Format[DataResource] = Json.format
}

/**
  * Controls access to the backend data, returning [[DataResource]]
  */
class DataResourceHandler @Inject()(
    routerProvider: Provider[DataRouter],
    dataRepository: DataRepository
)(implicit ec: ExecutionContext) {

  def create(
      DataInput: DataFormInput
  )(implicit mc: MarkerContext): Future[DataResource] = {
    val data = Data(
      999,
      Calendar.getInstance().getTime(),
      DataInput.brightness,
      DataInput.temperature,
      DataInput.noise_pollution,
      DataInput.pollution,
      DataInput.humidity,
      DataInput.latitude,
      DataInput.longitude
    )
    // We don't actually create the Data, so return what we have
    dataRepository.create(data).map { id =>
      createDataResource(id, data)
    }
  }

  def find(implicit mc: MarkerContext): Future[Iterable[DataResource]] = {
    dataRepository.list().map { DataList =>
      DataList.map(Data => createDataResource(Data))
    }
  }

  private def createDataResource(id: Int, p: Data): DataResource = {
    DataResource(
      id.toString(),
      routerProvider.get.link(p.id),
      p.time,
      p.brightness,
      p.temperature,
      p.noise_pollution,
      p.pollution,
      p.humidity,
      p.latitude,
      p.longitude
    )
  }

  private def createDataResource(p: Data): DataResource = {
    createDataResource(p.id, p)
  }
}

package v1.data

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}
import anorm.SqlParser.{int, str, scalar}
import anorm.{Macro, RowParser}
import anorm._
import play.api.db.DBApi
import java.util.Date
import scala.concurrent.Future

final case class Data(
    id: Int,
    time: Date,
    brightness: Int,
    temperature: Int,
    noise_pollution: Double,
    pollution: String,
    humidity: Double,
    latitude: Int,
    longitude: Int
)

class DataExecutionContext @Inject()(actorSystem: ActorSystem)
    extends CustomExecutionContext(actorSystem, "repository.dispatcher")

/**
  * A pure non-blocking interface for the DataRepository.
  */
trait DataRepository {
  def create(data: Data)(implicit mc: MarkerContext): Future[Int]

  def list()(implicit mc: MarkerContext): Future[Iterable[Data]]
}

/**
  * A trivial implementation for the Data Repository.
  *
  * A custom execution context is used here to establish that blocking operations should be
  * executed in a different thread than Play's ExecutionContext, which is used for CPU bound tasks
  * such as rendering.
  */
@Singleton
class DataRepositoryImpl @Inject()(dbapi: DBApi)(
    implicit ec: DataExecutionContext
) extends DataRepository {

  private val logger = Logger(this.getClass)
  private val db = dbapi.database("default")

  val parser: RowParser[Data] = Macro.namedParser[Data]

  override def list()(
      implicit mc: MarkerContext
  ): Future[Iterable[Data]] = {
    Future {
      logger.trace(s"list: ")
      db.withConnection { implicit c =>
        val code: List[Data] = SQL(
          """
              select * from Data;
          """
        ).as(parser.*)
        code
      }
    }
  }

  def create(data: Data)(implicit mc: MarkerContext): Future[Int] = {
    Future {
      logger.trace(s"create: data = $data")
      db.withConnection { implicit c =>
        val id: Option[Long] =
          SQL(
            "insert into Data(`id`, `time`, `brightness`, `temperature`, `noise_pollution`, `pollution`, `humidity`, `latitude`, `longitude`) VALUES (NULL, {time}, {brightness}, {temperature}, {noise_pollution}, {pollution}, {humidity}, {latitude}, {longitude});"
          ).onParams(
              data.time,
              data.brightness,
              data.temperature,
              data.noise_pollution,
              data.pollution,
              data.humidity,
              data.latitude,
              data.longitude
            )
            .executeInsert()

        id.get.toInt
      }
    }
  }

}

package data

import play.api.db.DB
import play.api.Play.current

import anorm.{~, SQL}
import anorm.SqlParser.get

import scala.language.postfixOps

/**
 * This class represents a Guardian user and it's described only
 * by the information associated with the PDGuard. (e.g. client
 * credentials, their trusted escrow agent.)
 *
 * If current application wants to issue a new PDGuard request associated
 * with a specific user, it has to retrieve required fields (escrow agent,
 * data subject id, client credentials).
 *
 * @param id ID of guardian user.
 * @param email Email of a guardian user.
 * @param dataSubjectId Data subject's id associated with chosen
 * escrow agent.
 */
case class GuardianUser(id: String, email: String, dataSubjectId: String)

/**
 * This class represents a Guardian user and it's described only
 * by the information associated with the PDGuard. (e.g. client
 * credentials, their trusted escrow agent.)
 *
 * If current application wants to issue a new PDGuard request associated
 * with a specific user, it has to retrieve required fields (escrow agent,
 * data subject id, client credentials).
 *
 * @param id Id of user. The same with escrow agent.
 * @param eagent Trusted escrow agent of guardian user.
 * @param clientId Client id of guardian user with this application
 * of Guardian organization.
 * @param clientSecret Client secret used to sign PDGuard requests.
 */
case class DataSubject(id: String, eagent: String, clientId: String,
                        clientSecret: String)

/** Helper for pagination. */
case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

object GuardianUser {
  val guardianUser = {
    get[String]("id") ~
    get[String]("email") ~
    get[String]("data_subject_id") map {
      case id~email~dataSubjectId => GuardianUser(id, email, dataSubjectId)
    }
  }

  val dataSubject = {
    get[String]("id") ~
    get[String]("eagent") ~
    get[String]("client_id") ~
    get[String]("client_secret") map {
      case id~eagent~clientId~clientSecret => DataSubject(id, eagent, clientId, clientSecret)
    }
  }

  /** Retrieve a guardian user from the email. */
  def findByEmail(email: String): GuardianUser = {
    DB.withConnection { implicit connection =>
      SQL("select * from guardian_user where email = {email}")
        .on('email -> email).as(GuardianUser.guardianUser.single)
    }
  }

  /** Retrieve data subject information by the data subject id. */
  def findById(dataSubjectId: String): Option[DataSubject] = {
    DB.withConnection { implicit connection =>
      SQL("select * from data_subject where id = {id}")
        .on('id -> dataSubjectId).as(GuardianUser.dataSubject.singleOpt)
    }
  }

  /** Insert a new guardian user. */
  def insert(guardianUser: GuardianUser) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into guardian_user values (
            {id}, {email}, {dataSubjectId}
          )
        """
      ).on(
          'id -> guardianUser.id,
          'dataSubjectId -> guardianUser.dataSubjectId,
          'email -> guardianUser.email
        ).executeUpdate()
    }
  }

  /** Insert a new data subject. */
  def insert(dataSubject: DataSubject) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into data_subject values (
            {id}, {eagent}, {clientId}, {clientSecret}
          )
        """
      ).on(
          'id -> dataSubject.id,
          'eagent -> dataSubject.eagent,
          'clientId -> dataSubject.clientId,
          'clientSecret -> dataSubject.clientSecret
        ).executeUpdate()
    }
  }
}

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
 * @param id Id of user. The same with escrow agent.
 * @param email Email of a guardian user.
 * @param eagent Trusted escrow agent of guardian user.
 * @param clientId Client id of guardian user with this application
 * of Guardian organization.
 * @param clientSecret Client secret used to sign PDGuard requests.
 */
case class GuardianUser(id: String, email: String, eagent: String,
                        clientId: String, clientSecret: String)

/** Helper for pagination. */
case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

object GuardianUser {
  val simple = {
    get[String]("id") ~
    get[String]("email") ~
    get[String]("eagent") ~
    get[String]("client_id") ~
    get[String]("client_secret") map {
      case id~email~eagent~clientId~clientSecret => GuardianUser(id, email, eagent, clientId, clientSecret)
    }
  }

  /** Retrieve a guardian user from the id. */
  def findByEmail(email: String): GuardianUser = {
    DB.withConnection { implicit connection =>
      SQL("select * from guardian_user where email = {email}")
        .on('email -> email).as(GuardianUser.simple.single)
    }
  }

  /** Insert a new guardian user. */
  def insert(guardianUser: GuardianUser) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into guardian_user values (
            {id}, {email}, {eagent}, {clientId}, {clientSecret}
          )
        """
      ).on(
          'id -> guardianUser.id,
          'email -> guardianUser.email,
          'eagent -> guardianUser.eagent,
          'clientId -> guardianUser.clientId,
          'clientSecret -> guardianUser.clientSecret
        ).executeUpdate()
    }
  }
}

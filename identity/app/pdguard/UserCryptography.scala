package pdguard

import com.gu.identity.model.User
import data.GuardianUser
import idapiclient.UserUpdate
import org.pdguard.api.exceptions.AccessDeniedException
import org.pdguard.api.model.ClientCredentials
import org.pdguard.api.utils.{InteractionPurpose, DataUse, DataProvenance}
import utils.UpdateParser
import org.apache.commons.codec.binary.Hex.decodeHex

/**
 * This class is responsible for the encryption and decryption of
 * private fields of an guardian user.
 *
 * These fields are associated with the full name of a user and
 * information associated with their location.
 *
 * @author Thodoris Sotiropoulos
 */
class UserCryptography(dataProtector: DataProtector) {
  /**
   * This method encrypts private fields of a guardian user.
   *
   * Private fields are taken by a client web form which a guardian user
   * fills in order to update his account profile.
   *
   * @param fieldToEncrypt A map with fields which are going to be encrypted.
   * Keyed by name of the corresponding field of user object.
   *
   * @return A map with the fields and their encrypted values.
   */
  def massiveEncrypt(fieldToEncrypt: Map[String, String], user: User): Map[String, String] = {
    var encryptedValues: Map[String, String] = Map()
    fieldToEncrypt.foreach {
      case(key, value) =>
        println(key, value)
        val existedValue = user.getPrivateFields().getClass.getMethod("get"
          + key.capitalize).invoke(user.getPrivateFields())
        val update = if (existedValue != null) true else false
        encryptedValues += (key -> dataProtector.encrypt(value.getBytes("UTF-8"),
            UpdateParser.dataType.get(key).get, DataProvenance.DATA_SUBJECT_EXPLICIT,
            update))
    }
    encryptedValues
  }

  /**
   * This method decrypts private fields of a guardian user.
   *
   * Private fields are taken by a client web form which a guardian user
   * has previously filled and now this method requests for decryption
   * in order to display them to guardian user.
   *
   * @param fieldToDecrypt map with fields which are going to be decrypted.
   * Keyed by name of the corresponding field of user object.
   *
   * @return A map with the fields and their decrypted values.
   */
  def massiveDecrypt(fieldToDecrypt: Map[String, String]): Map[String, String] = {
    var decryptedValues: Map[String, String] = Map()
    fieldToDecrypt.foreach {
      case(key, value) =>
        try {
          decryptedValues += (key -> dataProtector.decrypt(decodeHex(value.toCharArray),
            UpdateParser.dataType.get(key).get, DataUse.REPORT,
            InteractionPurpose.INFORMATIVE))
        } catch {
          case e: AccessDeniedException =>
            decryptedValues += (key -> "We don't have access to this piece of information")
        }
    }
    decryptedValues
  }
}

object UserDecryptor {
  /**
   * This method decrypts fields of an authenticated guardian user.
   *
   * @param user Authenticated guardian user.
   */
  def decryptAuthUser(user: User) = {
    val guardianUser = GuardianUser.findById(GuardianUser
      .findByEmail(user.primaryEmailAddress).dataSubjectId).get
    val clientCredentials = new ClientCredentials(guardianUser.clientId,
      guardianUser.clientSecret)
    val dataProtector = new DataProtector(guardianUser.eagent, clientCredentials,
      SecureContext.loadKeyStore(true), SecureContext.loadKeyStore(false),
      SecureContext.getKeyStorePswrd)
    val massiveDecryptor = new UserCryptography(dataProtector)
    val decryptedValues = massiveDecryptor.massiveDecrypt(UpdateParser.getFieldsToUpdate(
      user.privateFields.getClass, user.privateFields))
    UpdateParser.updateFields(decryptedValues, user.privateFields.getClass,
      user.privateFields)
  }
}


object UserEncryptor {
  /**
   * This method encrypts updated fields of an authenticated guardian user.
   *
   * @param user Authenticated guardian user.
   * @param updatedUser Updated guardina user.
   */
  def encryptUpdateUser(user: User, updatedUser: UserUpdate) = {
    val guardianUser = GuardianUser.findById(GuardianUser
      .findByEmail(user.primaryEmailAddress).dataSubjectId).get
    val clientCredentials = new ClientCredentials(guardianUser.clientId,
      guardianUser.clientSecret)
    val dataProtector = new DataProtector(guardianUser.eagent, clientCredentials,
      SecureContext.loadKeyStore(true), SecureContext.loadKeyStore(false),
      SecureContext.getKeyStorePswrd)
    val massiveEncryptor = new UserCryptography(dataProtector)
    val values = massiveEncryptor.massiveEncrypt(UpdateParser.getFieldsToUpdate(
      updatedUser.privateFields.get.getClass, updatedUser.privateFields.get), user)
    UpdateParser.updateFields(values, updatedUser.privateFields.get.getClass,
      updatedUser.privateFields.get)
  }
}

package utils

import org.pdguard.api.utils.DataType

/**
 * This is a utility class that is used to create a map of fields
 * of a given class and their values as wells to update values
 * of a class fields according to a map.
 *
 * It is used to create a map with the private fields of a guardian user
 * application wants to encrypt or decrypt according to the organization
 * policy.
 *
 * @author Thodoris Sotiropoulos
 */
object UpdateParser {
  /** Map a field with the type of data it represents. */
  lazy val dataType = Map(
    ("firstName", DataType.GIVEN_NAME),
    ("secondName", DataType.SURNAME),
    ("country", DataType.COUNTRY),
    ("postcode", DataType.HOME_POSTAL_CODE),
    ("address1", DataType.HOME_STREET_ADDRESS),
    ("address2", DataType.HOME_STREET_ADDRESS),
    ("address3", DataType.HOME_CITY),
    ("address4", DataType.HOME_STATE_OR_PROVINCE)
  )

  /**
   * Creates a map of fields and their values application wants to
   * encrypt or decrypt.
   *
   * It is used to create a map with the private fields of a guardian user
   * application wants to encrypt or decrypt according to the organization
   * policy.
   *
   * @param givenClass Class represents private fields of a guardian user.
   * @param obj Object of a guardian user
   *
   * @return Map of fields and their values application wants to encrypt or
   * decrypt.
   */
  def getFieldsToUpdate(givenClass: Class[_], obj: Object) = {
    var fieldsToUpdate: Map[String, String] = Map()
    val fields = givenClass.getDeclaredFields
    fields.foreach {
      case(field) =>
        val fieldName = field.getName
        val value = givenClass.getMethod("get" + fieldName.capitalize).invoke(obj)
        if (dataType.contains(fieldName) && value != null)
          fieldsToUpdate += (fieldName -> String.valueOf(value))
    }
    fieldsToUpdate
  }

  /**
   * This method updates a guardian user fields with the new encrypted or
   * decrypted values.
   *
   * @param fields Map of updated fields with the new encrypted or decrypted
   * values.
   * @param givenClass Class represents private fields of a guardian user.
   * @param obj Object of a guardian user.
   */
  def updateFields(fields: Map[String, String], givenClass: Class[_],
      obj: Object) = {
    fields.foreach {
      case(key, value) =>
        givenClass.getMethod("set" + key.capitalize, classOf[String]).invoke(obj, value)
    }
  }
}

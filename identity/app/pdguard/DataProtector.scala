package pdguard

import java.security.KeyStore

import org.pdguard.api.DataProtection
import org.pdguard.api.exceptions.AccessDeniedException
import org.pdguard.api.model.ClientCredentials
import org.pdguard.api.utils.{InteractionPurpose, DataUse, DataProvenance, DataType}
import org.apache.commons.codec.binary.Hex.encodeHex

/**
 * This class is responsible for the connection of current app with escrow agent
 * for the encryption and decryption of personal data which belongs to a
 * specific data subject.
 *
 * @author Thodoris Sotiropoulos
 */
class DataProtector(eagent: String, clientCredentials: ClientCredentials,
    keyStore: KeyStore, trustStore: KeyStore, keyStorePswrd: String) {
  /**
   * Object which is responsible for the encryption and
   * decryption of data.
   */
  val dataProtection = new DataProtection(eagent, clientCredentials,
    trustStore, keyStore, keyStorePswrd)

  /**
   * Encrypt the specified data block.
   *
   * Through this method data subjects can control and audit the types
   * of personal data stored by a data controller.
   * Requests can support two scenarios. First, the data subject can
   * deny the storage of particular data types (e.g. mobile phone). Second,
   * the data subject can lock-down a particular data type (e.g. home
   * address) to prevent fraud.
   *
   * @param cleartextData The data to encrypt.
   * @param dataType The type of the data to encrypt.
   * @param dataProvenance The data's provenance; where the data was obtained
   * from.
   * @param update True if an existing field is updated; false otherwise.
   *
   * @throws java.io.IOException IOException is thrown when the communication
   * with the escrow agent fails.
   * @throws AccessDeniedException when the data subject
   * denies access to the corresponding data.
   *
   * @return The data encrypted.
   */
  def encrypt(cleartextData: Array[Byte], dataType: DataType,
      dataProvenance: DataProvenance, update: Boolean): String =
    new String(encodeHex(dataProtection.encryptData(cleartextData, dataType, dataProvenance,
      update)))

  /**
   * Decrypt the specified data block.
   *
   * Through this method data subjects can control and audit how the data
   * controller uses their personal data.
   *
   * @param encryptedData The data to decrypt.
   * @param dataType The type of the data to decrypt.
   * @param dataUse The intended use of the decrypted data.
   * @param interactionPurpose The purpose of the requested interaction.
   *
   * @throws java.io.IOException IOException is thrown when the communication
   * with the escrow agent fails.
   * @throws AccessDeniedException when the data subject
   * denies access to the corresponding data.
   *
   * @return The data decrypted.
   */
  def decrypt(encryptedData: Array[Byte], dataType: DataType,
      dataUse: DataUse, interactionPurpose: InteractionPurpose): String =
    new String(dataProtection.decryptData(encryptedData, dataType, dataUse,
      interactionPurpose), "UTF-8")
}

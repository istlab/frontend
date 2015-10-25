package pdguard

import java.io.FileInputStream
import java.security.KeyStore

import play.api.Play

/**
 * This class is responsible for the retrieval of client trust stores and client
 * key stores files and the creation object corresponding to them.
 *
 * This is required for the establishment of a secure connection with the
 * escrow agent over TLS protocol 1.2.
 *
 * @author Thodoris Sotiropoulos
 */
object SecureContext {
  /** Path to client key store file. */
  private val keyStorePath = Play.current.path
    .toString.concat("/certs/app.keystore")

  /** Path to client trust store file. */
  private val trustStorePath = Play.current.path
    .toString.concat("/certs/app.trustore")

  /** Password of key store file. */
  private val keyStorePswrd = "clientpassword"

  /** Password of trust store file. */
  private val trustStorePswrd = "clientpassword"

  /**
   * Reads and initializes key store which corresponds to the selected file.
   *
   * @param isKeyStore True if we need the retrieval of client key store
   * file; false if we need the retrieval of client trust store file.
   *
   * @return Keystore object in order an encrypted connection with escrow
   * agent can be established using TLS protocol.
   */
  def loadKeyStore(isKeyStore: Boolean): KeyStore = {
    val path = if (isKeyStore) keyStorePath else trustStorePath
    val password = if (isKeyStore) keyStorePswrd else trustStorePswrd
    val keyStore = KeyStore.getInstance("JKS")
    keyStore.load(new FileInputStream(path), password.toCharArray)
    keyStore
  }

  def getKeyStorePswrd = keyStorePswrd
}

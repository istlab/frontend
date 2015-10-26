package pdguard

/**
 * This class is responsible for the retrieval of data which describe the
 * whole organization where the frontend application is running.
 *
 * These data are required in order this application can issue PDGuard
 * requests.
 *
 * @author Thodoris Sotiropoulos
 */
object OrganizationInfo {
  /** Id of Guardian. */
  val dataControllerId = "test-id2"

  /** Id of current application. */
  val appId = "test-id2"
}

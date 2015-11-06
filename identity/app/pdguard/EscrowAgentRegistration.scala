package pdguard

import java.io.IOException

import data.{DataSubject, GuardianUser}
import org.pdguard.api.exceptions.EscrowAgentErrorResponseException
import org.pdguard.api.model.{Client, EscrowAgent, ClientCredentials}
import org.pdguard.api.security.SecureConnection
import org.pdguard.api.services.RegistrationService

/**
 * This class is responsible for the registration of anew client to
 * the escrow agent.
 *
 * This step is required in order Guardian can issue PDGuard requests
 * to have access to data of its users.
 *
 * @author Thodoris Sotiropoulos
 */
class EscrowAgentRegistration(eagent: String, dataSubjectId: String) {
  /**
   * This method requests escrow agent to register new client which
   * corresponds to this application, data controller who owns this
   * application and customer who is registering this application.
   *
   * If client registration is successful, escrow agent sends back
   * client credentials (client id, client secret) required so that this
   * application can issue PDGuard requests.
   *
   * @return Client credentials to issue PDGuard requests.
   *
   * @throws IOException is thrown when the communication with escrow agent
   *                     fails.
   * @throws EscrowAgentErrorResponseException is thrown when the client
   * registration cannot be completed and escrow agent sends back an error
   * message.
   */
  def createPDGuardClient(): ClientCredentials = {
    val dataControllerId = OrganizationInfo.dataControllerId
    val appId = OrganizationInfo.appId
    val escrowAgent = new EscrowAgent(eagent)
    val client = new Client(dataSubjectId, dataControllerId,
        appId, "address")
    val secureConnection = new SecureConnection(SecureContext.loadKeyStore(true),
      SecureContext.loadKeyStore(false), SecureContext.getKeyStorePswrd)
    val registration = new RegistrationService(
      client, escrowAgent, secureConnection)
    val clientCredentials = registration.register()
    GuardianUser.insert(DataSubject(dataSubjectId, eagent, clientCredentials.getClientId,
        clientCredentials.getClientSecret))
    clientCredentials
  }
}

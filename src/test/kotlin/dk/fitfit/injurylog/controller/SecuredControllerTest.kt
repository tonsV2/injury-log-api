package dk.fitfit.injurylog.controller

import dk.fitfit.injurylog.controller.client.LoginClient
import dk.fitfit.injurylog.dto.Credentials
import javax.inject.Inject

internal abstract class SecuredControllerTest {
    @Inject
    lateinit var loginClient: LoginClient

    internal open fun getAuthorization(username: String, password: String): String {
        val credentials = Credentials(username, password)
        val loginResponse = loginClient.login(credentials)
        return "Bearer ${loginResponse.accessToken}"
    }
}

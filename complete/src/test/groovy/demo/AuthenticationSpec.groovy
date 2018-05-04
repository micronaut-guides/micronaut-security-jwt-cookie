package demo

import geb.spock.GebSpec
import io.micronaut.context.ApplicationContext
import io.micronaut.context.exceptions.NoSuchBeanException
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.security.endpoints.LoginController
import io.micronaut.security.endpoints.LogoutController
import io.micronaut.security.token.jwt.bearer.BearerTokenReader
import io.micronaut.security.token.jwt.cookie.JwtCookieLoginHandler
import io.micronaut.security.token.jwt.cookie.JwtCookieClearerLogoutHandler
import spock.lang.AutoCleanup
import spock.lang.Shared

class AuthenticationSpec extends GebSpec {

    @Shared
    @AutoCleanup
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer)

    def "verify session based authentication works"() {
        given:
        browser.baseUrl = "http://localhost:${embeddedServer.port}"

        when:
        to HomePage

        then:
        at HomePage

        when:
        HomePage homePage = browser.page HomePage

        then: 'As we are not logged in, there is no username'
        homePage.username() == null

        when: 'click the login link'
        homePage.login()

        then:
        at LoginPage

        when: 'fill the login form, with invalid credentials'
        LoginPage loginPage = browser.page LoginPage
        loginPage.login('foo', 'foo')

        then: 'the user is still in the login form'
        at LoginPage

        and: 'and error is displayed'
        loginPage.hasErrors()

        when: 'fill the form with valid credentials'
        loginPage.login('sherlock', 'password')

        then: 'we get redirected to the home page'
        at HomePage

        when:
        homePage = browser.page HomePage

        then: 'the username is populated'
        homePage.username() == 'sherlock'

        when: 'click the logout button'
        homePage.logout()

        then: 'we are in the home page'
        at HomePage

        when:
        homePage = browser.page HomePage

        then: 'but we are no longer logged in'
        homePage.username() == null
    }
}

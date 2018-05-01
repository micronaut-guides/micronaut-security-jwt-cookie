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

class SessionAuthenticationSpec extends GebSpec {

    @Shared
    @AutoCleanup
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer)

    def "verify session based authentication works"() {
        given:
        browser.baseUrl = "http://localhost:${embeddedServer.port}"

        embeddedServer.applicationContext.getBean(LoginController.class)
        embeddedServer.applicationContext.getBean(LogoutController.class)
        embeddedServer.applicationContext.getBean(JwtCookieLoginHandler.class)
        embeddedServer.applicationContext.getBean(JwtCookieClearerLogoutHandler.class)

        when:
        embeddedServer.applicationContext.getBean(BearerTokenReader.class)

        then:
        thrown(NoSuchBeanException)

        when:
        to HomePage

        then:
        at HomePage

        when:
        HomePage homePage = browser.page HomePage

        then:
        homePage.username() == null

        when:
        homePage.login()

        then:
        at LoginPage

        when:
        LoginPage loginPage = browser.page LoginPage
        loginPage.login('foo', 'foo')

        then:
        at LoginPage

        and:
        loginPage.hasErrors()

        when:
        loginPage.login('sherlock', 'password')

        then:
        at HomePage

        when:
        homePage = browser.page HomePage

        then:
        homePage.username() == 'sherlock'

        when:
        homePage.logout()

        then:
        at HomePage

        when:
        homePage = browser.page HomePage

        then:
        homePage.username() == null
    }
}

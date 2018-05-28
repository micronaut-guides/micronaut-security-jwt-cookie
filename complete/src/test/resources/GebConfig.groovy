import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions

environments {

    // run via “./gradlew -Dgeb.env=chrome test”
    chrome {
        driver = { new ChromeDriver() }
    }

    // run via “./gradlew -Dgeb.env=chromeHeadless test”
    chromeHeadless {
        driver = {
            ChromeOptions o = new ChromeOptions()
            o.addArguments('headless')
            new ChromeDriver(o)
        }
    }

    firefoxHeadless {
        driver = {
            FirefoxOptions o = new FirefoxOptions()
            o.addArguments('-headless')
            new FirefoxDriver(o)
        }
    }

    // run via “./gradlew -Dgeb.env=firefox test”
    firefox {
        driver = { new FirefoxDriver() }
    }
}

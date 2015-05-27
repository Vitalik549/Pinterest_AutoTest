package com.pinterest.uk.helpers;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.Proxy.ProxyType;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

public class BaseTest {

    private static final Logger LOGGER = Logger.getLogger(BaseTest.class);
    protected WebDriver driver;
    private String remoteWebDriverUrl = null;
    private LoggingPreferences logs;
    private String environment;
    private static String browser;
    private static boolean useRemoteWebDriver;
    private EnvironmentPropertiesHandler properties;

    public void setUp() {
        final Os os = getOs();
        initializeStaticFields();
        if (!useRemoteWebDriver && os != Os.MACOS32) {
            initializeDriver("webdriver.chrome.driver", os, os.chromePath);
        }
        initialiseWebDriver();
        WebDriverRunner.setWebDriver(driver); //for selenide purposes
        Configuration.reportsFolder = "./target/screens";
    }

    public String getEnvironment() {
        return this.environment;
    }

    private enum Os {

        WINDOWS32("webdriver/", "chromedriver_win32", "IEDriverServer", ".exe"),
        WINDOWS64("webdriver/", "chromedriver_win32", "IEDriverServer", ".exe"),
        LINUX32("webdriver/", "chromedriver_linux32", null, ""),
        LINUX64("webdriver/", "chromedriver_linux64", null, ""),
        MACOS32("webdriver/", "chromedriver_32", null, "");

        private final String chromePath;
        private final String prefix;
        private final String suffix;

        Os(String prefix, String chromePath, String iePath, String suffix) {
            this.prefix = prefix;
            this.chromePath = chromePath;
            this.suffix = suffix;
        }
    }

    private static Os getOs() {

        if (SystemUtils.IS_OS_WINDOWS) {
            return SystemUtils.OS_ARCH.contains("32") ? Os.WINDOWS32 : Os.WINDOWS64;
        } else if (SystemUtils.IS_OS_LINUX) {
            return SystemUtils.OS_ARCH.contains("32") ? Os.LINUX32 : Os.LINUX64;
        } else if (SystemUtils.IS_OS_MAC_OSX || SystemUtils.IS_OS_MAC) {
            return Os.MACOS32;
        }
        throw new IllegalStateException("Unknown OS: " + SystemUtils.OS_NAME);
    }

    private void initializeDriver(String browserSystemVariable, Os os, String browserPath) {
        if (browserPath != null) {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(
                    os.prefix + browserPath + os.suffix);
            if (inputStream == null) {
                throw new IllegalStateException("Cannot locate driver on classpath (missing dependency): "
                        + os.prefix + browserPath + os.suffix);
            }
            try {
                File temp = File.createTempFile(browserPath, os.suffix);
                temp.setExecutable(true);
                FileUtils.copyInputStreamToFile(inputStream, temp);
                System.setProperty(browserSystemVariable, temp.getAbsolutePath());
            } catch (IOException e) {
                final String msg = "Error while copying driver exacutable";
                LOGGER.error(msg, e);
                throw new RuntimeException(msg, e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        final String msg = "Error while closing the input stream";
                        LOGGER.error(msg, e);
                        throw new RuntimeException(msg, e);
                    }
                }
            }
        }
    }

    private void initializeStaticFields() {
        this.properties = EnvironmentPropertiesHandler.getInstance();
        environment = System.getProperty("testEnv");
        if (environment == null) {
            environment = "default";
        }
        LOGGER.info("Environment is set to: " + environment);
        browser = properties.getProperty(environment + "."
                + EnvironmentPropertiesHandler.BROWSER);
        if (properties.getProperty(environment + "."
                + EnvironmentPropertiesHandler.USE_REMOTE_WEBDRIVER) != null) {
            useRemoteWebDriver = Boolean.parseBoolean(properties.getProperty(environment + "."
                    + EnvironmentPropertiesHandler.USE_REMOTE_WEBDRIVER));
        } else {
            useRemoteWebDriver = false;
        }
    }

    protected void initialiseWebDriver() {
        DesiredCapabilities capability = null;
        if (useRemoteWebDriver) {
            String remoteWebDriverUrl = properties.getProperty(environment + "."
                    + EnvironmentPropertiesHandler.REMOTE_WEBDRIVER_URL);
            if (remoteWebDriverUrl != null) {
                this.remoteWebDriverUrl = remoteWebDriverUrl;
            }

            if (browser.equalsIgnoreCase("firefox")) {
                capability = DesiredCapabilities.firefox();
                addProfileToCapability(capability);
            } else if (browser.equalsIgnoreCase("internetexplorer")) {
                capability = DesiredCapabilities.internetExplorer();
                capability.setCapability("ignoreZoomSetting", true);
            } else if (browser.equalsIgnoreCase("chrome")) {
                capability = DesiredCapabilities.chrome();
            } else if (browser.equalsIgnoreCase("opera")) {
                capability = DesiredCapabilities.opera();
            } else if (browser.equalsIgnoreCase("mobileSafari")) {
                capability = new DesiredCapabilities();
            } else if (browser.equalsIgnoreCase("androidWeb")) {
                capability = DesiredCapabilities.android();
            } else {
                capability = new DesiredCapabilities();
            }
            try {
                LOGGER.info("Initializing remote webdriver with: "
                        + capability.getBrowserName() + ", "
                        + capability.getVersion() + ", "
                        + capability.getPlatform());
                driver = new RemoteWebDriver(new URL(this.remoteWebDriverUrl), setCapabilities(capability));
                ((RemoteWebDriver) driver).setFileDetector(new LocalFileDetector());
            } catch (MalformedURLException e) {
                final String msg = "Error while initializing remote webdriver with url: "
                        + this.remoteWebDriverUrl;
                LOGGER.error(msg, e);
                throw new RuntimeException(msg, e);
            }
        } else if (browser.equals("firefox")) {
            capability = DesiredCapabilities.firefox();
            {
                addProfileToCapability(capability);
                LOGGER.info("Initializing webdriver with " + capability.getBrowserName() + ", "
                        + capability.getVersion()
                        + ", " + capability.getPlatform());
                driver = new FirefoxDriver(setCapabilities(capability));
            }
        } else if (browser.equalsIgnoreCase("chrome")) {
            capability = DesiredCapabilities.chrome();
            LOGGER.info("Initializing webdriver with " + capability.getBrowserName() + ", "
                    + capability.getVersion()
                    + ", " + capability.getPlatform());
            driver = new ChromeDriver(setCapabilities(capability));
        } else if (browser.equalsIgnoreCase("opera")) {
        } else {
            final String msg = "No proper browser settings, check environment.properties";
            LOGGER.error(msg);
            {
                throw new RuntimeException(msg);
            }
        }
    }

    private void logBrowserSetting(DesiredCapabilities capability) {
        LOGGER.info("Initializing webdriver with " + capability.getBrowserName() + ", " + capability.getVersion() + ", " + capability.getPlatform());
    }

    private DesiredCapabilities setCapabilities(DesiredCapabilities desiredCapability) {
        if (properties.getProperty(environment + "."
                + EnvironmentPropertiesHandler.ACCEPT_SSL_CERTS) != null) {
            desiredCapability.setCapability(
                    CapabilityType.ACCEPT_SSL_CERTS,
                    Boolean.parseBoolean(properties.getProperty(environment + "."
                            + EnvironmentPropertiesHandler.ACCEPT_SSL_CERTS)));
        }
        if (properties.getProperty(environment + "."
                + EnvironmentPropertiesHandler.BROWSER_NAME) != null) {
            desiredCapability.setCapability(CapabilityType.BROWSER_NAME,
                    properties.getProperty(environment + "."
                            + EnvironmentPropertiesHandler.BROWSER_NAME));
        }
        if (properties.getProperty(environment + "."
                + EnvironmentPropertiesHandler.ENABLE_PROFILING_CAPABILITY) != null) {
            desiredCapability.setCapability(
                    CapabilityType.ENABLE_PROFILING_CAPABILITY,
                    Boolean.parseBoolean(properties.getProperty(environment + "."
                            + EnvironmentPropertiesHandler.ENABLE_PROFILING_CAPABILITY)));
        }
        if (properties.getProperty(environment + "."
                + EnvironmentPropertiesHandler.HAS_NATIVE_EVENTS) != null) {
            desiredCapability.setCapability(
                    CapabilityType.HAS_NATIVE_EVENTS,
                    Boolean.parseBoolean(properties.getProperty(environment + "."
                            + EnvironmentPropertiesHandler.HAS_NATIVE_EVENTS)));
        }
        if (properties.getProperty(environment + "."
                + EnvironmentPropertiesHandler.PLATFORM) != null) {
            desiredCapability
                    .setCapability(
                            CapabilityType.PLATFORM,
                            Platform.valueOf(properties.getProperty(environment + "."
                                    + EnvironmentPropertiesHandler.PLATFORM)));
        }
        if (properties.getProperty(environment + "."
                + EnvironmentPropertiesHandler.ROTATABLE) != null) {
            desiredCapability.setCapability(
                    CapabilityType.ROTATABLE,
                    Boolean.parseBoolean(properties.getProperty(environment + "."
                            + EnvironmentPropertiesHandler.ROTATABLE)));
        }
        if (properties.getProperty(environment + "."
                + EnvironmentPropertiesHandler.SUPPORTS_ALERTS) != null) {
            desiredCapability.setCapability(
                    CapabilityType.SUPPORTS_ALERTS,
                    Boolean.parseBoolean(properties.getProperty(environment + "."
                            + EnvironmentPropertiesHandler.SUPPORTS_ALERTS)));
        }
        if (properties.getProperty(environment + "."
                + EnvironmentPropertiesHandler.SUPPORTS_APPLICATION_CACHE) != null) {
            desiredCapability.setCapability(
                    CapabilityType.SUPPORTS_APPLICATION_CACHE,
                    Boolean.parseBoolean(properties.getProperty(environment + "."
                            + EnvironmentPropertiesHandler.SUPPORTS_APPLICATION_CACHE)));
        }
        if (properties.getProperty(environment + "."
                + EnvironmentPropertiesHandler.SUPPORTS_FINDING_BY_CSS) != null) {
            desiredCapability.setCapability(
                    CapabilityType.SUPPORTS_FINDING_BY_CSS,
                    Boolean.parseBoolean(properties.getProperty(environment + "."
                            + EnvironmentPropertiesHandler.SUPPORTS_FINDING_BY_CSS)));
        }
        if (properties.getProperty(environment + "."
                + EnvironmentPropertiesHandler.JAVA_SCRIPT_ENABLED) != null) {
            desiredCapability.setCapability(
                    CapabilityType.SUPPORTS_JAVASCRIPT,
                    Boolean.parseBoolean(properties.getProperty(environment + "."
                            + EnvironmentPropertiesHandler.JAVA_SCRIPT_ENABLED)));
        }
        if (properties.getProperty(environment + "."
                + EnvironmentPropertiesHandler.SUPPORTS_LOCATION_CONTEXT) != null) {
            desiredCapability.setCapability(
                    CapabilityType.SUPPORTS_LOCATION_CONTEXT,
                    Boolean.parseBoolean(properties.getProperty(environment + "."
                            + EnvironmentPropertiesHandler.SUPPORTS_LOCATION_CONTEXT)));
        }
        if (properties.getProperty(environment + "."
                + EnvironmentPropertiesHandler.SUPPORTS_SQL_DATABASE) != null) {
            desiredCapability.setCapability(
                    CapabilityType.SUPPORTS_SQL_DATABASE,
                    Boolean.parseBoolean(properties.getProperty(environment + "."
                            + EnvironmentPropertiesHandler.SUPPORTS_SQL_DATABASE)));
        }
        if (properties.getProperty(environment + "."
                + EnvironmentPropertiesHandler.SUPPORTS_WEB_STORAGE) != null) {
            desiredCapability.setCapability(
                    CapabilityType.SUPPORTS_WEB_STORAGE,
                    Boolean.parseBoolean(properties.getProperty(environment + "."
                            + EnvironmentPropertiesHandler.SUPPORTS_WEB_STORAGE)));
        }
        if (properties.getProperty(environment + "."
                + EnvironmentPropertiesHandler.TAKES_SCREENSHOT) != null) {
            desiredCapability.setCapability(
                    CapabilityType.TAKES_SCREENSHOT,
                    Boolean.parseBoolean(properties.getProperty(environment + "."
                            + EnvironmentPropertiesHandler.TAKES_SCREENSHOT)));
        }
        if (properties.getProperty(environment + "."
                + EnvironmentPropertiesHandler.UNEXPECTED_ALERT_BEHAVIOUR) != null) {
            desiredCapability.setCapability(
                    CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR,
                    UnexpectedAlertBehaviour.fromString(properties.getProperty(environment + "."
                            + EnvironmentPropertiesHandler.UNEXPECTED_ALERT_BEHAVIOUR)));
        }
        if (properties.getProperty(environment + "."
                + EnvironmentPropertiesHandler.VERSION) != null) {
            desiredCapability.setCapability(CapabilityType.VERSION,
                    properties.getProperty(environment + "."
                            + EnvironmentPropertiesHandler.VERSION));
        }
        if (properties.getProperty(environment + "."
                + EnvironmentPropertiesHandler.IGNORE_ZOOM_SETTING) != null) {
            desiredCapability.setCapability("ignoreZoomSetting",
                    properties.getProperty(environment + "."
                            + EnvironmentPropertiesHandler.IGNORE_ZOOM_SETTING));
        }
        if (properties.getProperty(environment + "."
                + EnvironmentPropertiesHandler.IGNORE_PROTECTED_MODE_SETTINGS) != null) {
            desiredCapability.setCapability(
                    "ignoreProtectedModeSettings",
                    properties.getProperty(environment + "."
                            + EnvironmentPropertiesHandler.IGNORE_PROTECTED_MODE_SETTINGS));
            logs = new LoggingPreferences();
        }
        if (properties.getProperty(environment + "."
                + EnvironmentPropertiesHandler.LOGGING_LEVEL) != null) {
            logs.enable(LogType.DRIVER,
                    Level.parse(properties.getProperty(environment + "."
                            + EnvironmentPropertiesHandler.LOGGING_LEVEL)));
            desiredCapability.setCapability(CapabilityType.LOGGING_PREFS, logs);
        }
        if (properties.getProperty(environment + "."
                + EnvironmentPropertiesHandler.PROXY_TYPE) != null
                && properties.getProperty(environment + "."
                + EnvironmentPropertiesHandler.PROXY_ADDRESS) != null) {
            Proxy proxy = new Proxy();
            proxy.setProxyType(ProxyType.valueOf(properties.getProperty(environment + "."
                    + EnvironmentPropertiesHandler.PROXY_TYPE)));
            String proxyAddress = properties
                    .getProperty(environment + "."
                            + EnvironmentPropertiesHandler.PROXY_ADDRESS);
            proxy.setHttpProxy(proxyAddress);
            desiredCapability.setCapability(CapabilityType.PROXY, proxy);
        }
        return desiredCapability;

    }

    private void addProfileToCapability(DesiredCapabilities desiredCapabilities) {
        FirefoxProfile profile = new FirefoxProfile();
        final boolean allowAuth = StringUtils.equals(
                properties.getProperty(environment + "."
                        + EnvironmentPropertiesHandler.ALLOW_BROWSER_AUTHENTICATION),
                "true");
        final boolean automaticallySave = properties.getProperty(environment + "."
                + EnvironmentPropertiesHandler.AUTOMATICALLY_SAVE_TO_DISK) != null;
        if (allowAuth) {
            String trustedDomains = StringUtils.defaultString(properties.getProperty(environment + "."
                    + EnvironmentPropertiesHandler.LIST_OF_TRUSTED_DOMAINS_FOR_BROWSER_AUTHENTICATION));
            profile.setPreference("network.http.phishy-userpass-length", 255);
            profile.setPreference("network.automatic-ntlm-auth.trusted-uris", trustedDomains);
        } else if (automaticallySave) {
            profile.setPreference("browser.download.folderList", 2);
            profile.setPreference("browser.download.manager.showWhenStarting", false);
            profile.setPreference("browser.download.dir",
                    properties.getProperty(environment + "."
                            + EnvironmentPropertiesHandler.DOWNLOAD_FILE_TO));
            profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
                    properties.getProperty(environment + "."
                            + EnvironmentPropertiesHandler.AUTOMATICALLY_SAVE_TO_DISK));
        }
        if (allowAuth || automaticallySave) {
            LOGGER.info("Adding profile to " + desiredCapabilities.getBrowserName());
            desiredCapabilities.setCapability(FirefoxDriver.PROFILE, profile);
        }
    }
}

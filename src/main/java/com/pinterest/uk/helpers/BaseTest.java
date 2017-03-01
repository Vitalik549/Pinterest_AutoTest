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
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;

public class BaseTest {

    private static final Logger LOGGER = Logger.getLogger(BaseTest.class);
    protected WebDriver driver;
    private LoggingPreferences logs;
    private String environment;
    private static String browser;
    private static boolean useRemoteWebDriver;
    private EnvPropertiesHandler properties;
    private DesiredCapabilities desiredCapabilities;

    public void setUp() {
        final Os os = getOs();
        initializeStaticFields();
        if (!useRemoteWebDriver) {
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
        if (browserPath == null) return;

        InputStream inputStream = getClass()
                .getClassLoader()
                .getResourceAsStream(os.prefix + browserPath + os.suffix);

        if (inputStream == null)
            throw new IllegalStateException("Cannot locate driver on classpath (missing dependency): "
                    + os.prefix + browserPath + os.suffix);

        try {
            File temp = File.createTempFile(browserPath, os.suffix);
            temp.setExecutable(true);
            FileUtils.copyInputStreamToFile(inputStream, temp);
            System.setProperty(browserSystemVariable, temp.getAbsolutePath());
        } catch (IOException e) {
            final String msg = "Error while copying driver executable";
            LOGGER.error(msg, e);
            throw new RuntimeException(msg, e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                final String msg = "Error while closing the input stream";
                LOGGER.error(msg, e);
                throw new RuntimeException(msg, e);
            }
        }

    }

    private void initializeStaticFields() {
        environment = Optional.ofNullable(System.getProperty("testEnv")).orElse("default");

        LOGGER.info("Environment is set to: " + environment);

        properties = EnvPropertiesHandler.getInstance();
        browser = properties.getProperty(EnvPropertiesHandler.BROWSER);

        Optional<String> remoteProperty = Optional.ofNullable(properties.getProperty(EnvPropertiesHandler.USE_REMOTE_WEBDRIVER));
        useRemoteWebDriver = remoteProperty.isPresent() && Boolean.parseBoolean(remoteProperty.get());
    }

    private void initialiseWebDriver() {
        if (useRemoteWebDriver) {
            initialiseRemoteWebDriver();
        } else {
            initialiseLocalWebDriver();
        }
        logBrowserSettings(desiredCapabilities);
    }

    private void initialiseRemoteWebDriver() {
        String remoteWebDriverUrl = properties.getProperty(EnvPropertiesHandler.REMOTE_WEBDRIVER_URL);
        if (remoteWebDriverUrl == null) throw new RuntimeException("Remote webdriver url was not defined!");

        if (browser.equalsIgnoreCase("firefox")) {
            desiredCapabilities = DesiredCapabilities.firefox();
            addProfileToCapability(desiredCapabilities);
        } else if (browser.equalsIgnoreCase("internetexplorer")) {
            desiredCapabilities = DesiredCapabilities.internetExplorer();
            desiredCapabilities.setCapability("ignoreZoomSetting", true);
        } else if (browser.equalsIgnoreCase("chrome")) {
            desiredCapabilities = DesiredCapabilities.chrome();
        } else if (browser.equalsIgnoreCase("opera")) {
            desiredCapabilities = DesiredCapabilities.opera();
        } else if (browser.equalsIgnoreCase("mobileSafari")) {
            desiredCapabilities = new DesiredCapabilities();
        } else if (browser.equalsIgnoreCase("androidWeb")) {
            desiredCapabilities = DesiredCapabilities.android();
        } else {
            desiredCapabilities = new DesiredCapabilities();
        }

        try {
            driver = new RemoteWebDriver(new URL(remoteWebDriverUrl), setCapabilities());
            ((RemoteWebDriver) driver).setFileDetector(new LocalFileDetector());
        } catch (MalformedURLException e) {
            final String msg = "Error while initializing remote webdriver with url: " + remoteWebDriverUrl;
            LOGGER.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    private void initialiseLocalWebDriver() {
        if (browser.equals("firefox")) {
            desiredCapabilities = DesiredCapabilities.firefox();
            addProfileToCapability(desiredCapabilities);
            driver = new FirefoxDriver(setCapabilities());
        } else if (browser.equalsIgnoreCase("chrome")) {
            desiredCapabilities = DesiredCapabilities.chrome();
            driver = new ChromeDriver(setCapabilities());
        } else if (browser.equalsIgnoreCase("opera")) {
        } else {
            final String msg = "No proper browser settings, check environment.properties";
            LOGGER.error(msg);
            throw new RuntimeException(msg);
        }
    }

    private void setCapability(String capability, String property, Function<String, Object> func) {
        if (properties.getProperty(property) != null) {
            desiredCapabilities.setCapability(capability, func.apply(property));
        }
    }

    private DesiredCapabilities setCapabilities() {
        setCapability(CapabilityType.ACCEPT_SSL_CERTS, EnvPropertiesHandler.ACCEPT_SSL_CERTS, Boolean::parseBoolean);
        setCapability(CapabilityType.BROWSER_NAME, EnvPropertiesHandler.BROWSER_NAME, prop -> prop);
        setCapability(CapabilityType.ENABLE_PROFILING_CAPABILITY, EnvPropertiesHandler.ENABLE_PROFILING_CAPABILITY, Boolean::parseBoolean);
        setCapability(CapabilityType.HAS_NATIVE_EVENTS, EnvPropertiesHandler.HAS_NATIVE_EVENTS, Boolean::parseBoolean);
        setCapability(CapabilityType.PLATFORM, EnvPropertiesHandler.PLATFORM, Platform::valueOf);
        setCapability(CapabilityType.ROTATABLE, EnvPropertiesHandler.ROTATABLE, Boolean::parseBoolean);
        setCapability(CapabilityType.SUPPORTS_ALERTS, EnvPropertiesHandler.SUPPORTS_ALERTS, Boolean::parseBoolean);
        setCapability(CapabilityType.SUPPORTS_APPLICATION_CACHE, EnvPropertiesHandler.SUPPORTS_APPLICATION_CACHE, Boolean::parseBoolean);
        setCapability(CapabilityType.SUPPORTS_FINDING_BY_CSS, EnvPropertiesHandler.SUPPORTS_FINDING_BY_CSS, Boolean::parseBoolean);
        setCapability(CapabilityType.SUPPORTS_LOCATION_CONTEXT, EnvPropertiesHandler.SUPPORTS_LOCATION_CONTEXT, Boolean::parseBoolean);
        setCapability(CapabilityType.SUPPORTS_SQL_DATABASE, EnvPropertiesHandler.SUPPORTS_SQL_DATABASE, Boolean::parseBoolean);
        setCapability(CapabilityType.SUPPORTS_WEB_STORAGE, EnvPropertiesHandler.SUPPORTS_WEB_STORAGE, Boolean::parseBoolean);
        setCapability(CapabilityType.TAKES_SCREENSHOT, EnvPropertiesHandler.TAKES_SCREENSHOT, Boolean::parseBoolean);
        setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, EnvPropertiesHandler.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour::fromString);
        setCapability(CapabilityType.VERSION, EnvPropertiesHandler.VERSION, prop -> prop);
        setCapability("ignoreZoomSetting", EnvPropertiesHandler.IGNORE_ZOOM_SETTING, prop -> prop);
        setCapability("ignoreProtectedModeSettings", EnvPropertiesHandler.IGNORE_PROTECTED_MODE_SETTINGS, prop -> {
            logs = new LoggingPreferences();
            return prop;
        });
        setCapability(CapabilityType.LOGGING_PREFS, EnvPropertiesHandler.LOGGING_LEVEL, prop -> {
            logs.enable(LogType.DRIVER, Level.parse(properties.getProperty(EnvPropertiesHandler.LOGGING_LEVEL)));
            return logs;
        });

        if (properties.getProperty(EnvPropertiesHandler.PROXY_TYPE) != null
                && properties.getProperty(EnvPropertiesHandler.PROXY_ADDRESS) != null) {
            Proxy proxy = new Proxy();
            proxy.setProxyType(ProxyType.valueOf(properties.getProperty(EnvPropertiesHandler.PROXY_TYPE)));
            String proxyAddress = properties
                    .getProperty(EnvPropertiesHandler.PROXY_ADDRESS);
            proxy.setHttpProxy(proxyAddress);
            desiredCapabilities.setCapability(CapabilityType.PROXY, proxy);
        }
        return desiredCapabilities;
    }

    private void addProfileToCapability(DesiredCapabilities desiredCapabilities) {
        FirefoxProfile profile = new FirefoxProfile();
        final boolean allowAuth = StringUtils.equals(
                properties.getProperty(EnvPropertiesHandler.ALLOW_BROWSER_AUTHENTICATION),
                "true");
        final boolean automaticallySave = properties.getProperty(EnvPropertiesHandler.AUTOMATICALLY_SAVE_TO_DISK) != null;
        if (allowAuth) {
            String trustedDomains = StringUtils.defaultString(properties.getProperty(EnvPropertiesHandler.LIST_OF_TRUSTED_DOMAINS_FOR_BROWSER_AUTHENTICATION));
            profile.setPreference("network.http.phishy-userpass-length", 255);
            profile.setPreference("network.automatic-ntlm-auth.trusted-uris", trustedDomains);
        } else if (automaticallySave) {
            profile.setPreference("browser.download.folderList", 2);
            profile.setPreference("browser.download.manager.showWhenStarting", false);
            profile.setPreference("browser.download.dir", properties.getProperty(EnvPropertiesHandler.DOWNLOAD_FILE_TO));
            profile.setPreference("browser.helperApps.neverAsk.saveToDisk", properties.getProperty(EnvPropertiesHandler.AUTOMATICALLY_SAVE_TO_DISK));
        }
        if (allowAuth || automaticallySave) {
            LOGGER.info("Adding profile to " + desiredCapabilities.getBrowserName());
            desiredCapabilities.setCapability(FirefoxDriver.PROFILE, profile);
        }
    }

    private void logBrowserSettings(DesiredCapabilities capability) {
        String remote = useRemoteWebDriver ? "remote " : "";
        String msg = String.format("Initializing %swebdriver with %s, %s, %s.",
                remote, capability.getBrowserName(), capability.getVersion(), capability.getPlatform());
        LOGGER.info(msg);
    }
}

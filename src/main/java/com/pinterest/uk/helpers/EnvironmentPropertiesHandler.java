package com.pinterest.uk.helpers;

public class EnvironmentPropertiesHandler extends AbstractPropertiesHandler {
    public static final String PROPERTIES_LOCATION = "properties/environment.properties";
    public static final String BROWSER = "browser";
    public static final String BASE_URL = "base_url";
    public static final String IMPLICITLY_WAIT_TIMEOUT_IN_MILISECONDS = "implicitly_wait_timeout_in_miliseconds";
    public static final String USE_REMOTE_WEBDRIVER = "use_remote_webdriver";
    public static final String JAVA_SCRIPT_ENABLED = "java_script_enabled";
    public static final String BROWSER_NAME = "browser_name";
    public static final String VERSION = "version";
    public static final String PLATFORM = "platform";
    public static final String ACCEPT_SSL_CERTS = "accept_ssl_certs";
    public static final String ENABLE_PROFILING_CAPABILITY = "enable_profiling_capability";
    public static final String HAS_NATIVE_EVENTS = "has_native_events";
    public static final String SUPPORTS_ALERTS = "supports_alerts";
    public static final String SUPPORTS_APPLICATION_CACHE = "supports_application_cache";
    public static final String SUPPORTS_BROWSER_CONNECTION = "supports_browser_connection";
    public static final String SUPPORTS_FINDING_BY_CSS = "supports_finding_by_css";
    public static final String SUPPORTS_LOCATION_CONTEXT = "supports_location_context";
    public static final String SUPPORTS_SQL_DATABASE = "supports_sql_database";
    public static final String SUPPORTS_WEB_STORAGE = "supports_web_storage";
    public static final String TAKES_SCREENSHOT = "takes_screenshot";
    public static final String ROTATABLE = "rotatable";
    public static final String UNEXPECTED_ALERT_BEHAVIOUR = "unexpected_alert_behaviour";
    public static final String LOGGING_LEVEL = "logging_level";
    public static final String PROXY_TYPE = "proxy_type";
    public static final String PROXY_ADDRESS = "proxy_address";
    public static final String IGNORE_ZOOM_SETTING = "ignore_zoom_setting";
    public static final String IGNORE_PROTECTED_MODE_SETTINGS = "ignore_protected_mode_settings";
    public static final String REMOTE_WEBDRIVER_URL = "remote_webdriver_url";
    public static final String ALLOW_BROWSER_AUTHENTICATION = "allow_browser_authentication";
    public static final String LIST_OF_TRUSTED_DOMAINS_FOR_BROWSER_AUTHENTICATION =
            "list_of_trusted_domains_for_browser_authentication";
    public static final String AUTOMATICALLY_SAVE_TO_DISK = "automatically_save_file_to_disk";
    public static final String DOWNLOAD_FILE_TO = "download_file_to";
    public static final String STORE_SCREEN_TO = "store_screen_to";
    public static final String IMPLICITLY_WAIT_TIMEOUT_IN_SECONDS = "implicitly_wait_timeout_in_seconds";

    private EnvironmentPropertiesHandler(String location) {
        super(location);
    }

    private static EnvironmentPropertiesHandler propertiesHandler = null;

    public static EnvironmentPropertiesHandler getInstance() {
        if (propertiesHandler == null) {
            propertiesHandler = new EnvironmentPropertiesHandler(PROPERTIES_LOCATION);
        }
        return propertiesHandler;
    }

    // TODO Remove unused code found by UCDetector
    // public static void createOrReplaceHandler(String
    // alternativePropertiesLocation) {
    // propertiesHandler = new
    // EnvironmentPropertiesHandler(alternativePropertiesLocation);
    // }
}

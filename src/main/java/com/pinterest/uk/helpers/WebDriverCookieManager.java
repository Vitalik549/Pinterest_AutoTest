/**
 * Copyright (C) Zoomdata, Inc. 2012-2016. All rights reserved.
 */
package com.pinterest.uk.helpers;

import com.codeborne.selenide.WebDriverRunner;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.openqa.selenium.Cookie;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.open;

public class WebDriverCookieManager {

    private Logger log = Logger.getLogger(WebDriverCookieManager.class);

    private static String BASE_URL = EnvPropertiesHandler.getInstance().getProperty(EnvPropertiesHandler.BASE_URL);
    private static final String COOKIE_NAME = "JSESSIONID";
    private static final String GET_TOKEN_JS_CODE = "return Zoomdata.pageData.csrfToken;";
    private static final String SECURITY_CHECK_URL = BASE_URL + "/j_spring_security_check";

    private String userName;
    private String userPassword;
    private String cookieValue;
    private String sessionIdCookie;
    private String csrfToken;

    public WebDriverCookieManager(String userName, String userPassword) {
        this.userName = userName;
        this.userPassword = userPassword;
    }

    public String getSessionIdCookie() {
        return sessionIdCookie;
    }

    public void setSessionCookie() {
        boolean sessionInitialized = false;
        int attempt = 1;

        while (!sessionInitialized && attempt < 4) {
            try {
                getLoginPageCookieValue();
                fetchCsrfToken();
                authenticateSessionAndSetNewCookie();

                sessionInitialized = true;
            } catch (Exception | AssertionError e) {
                log.error("Failed to fetch cookie or csrf token from Login Page after " + attempt + " attempt(s)!\n" +
                        "Will try one more time...\n");
                log.warn(e.getStackTrace());

                attempt++;

                WebDriverRunner.clearBrowserCache();
                open(BASE_URL);
            }
        }

        if (!sessionInitialized) {
            throw new AssertionError("Failed to get cookie or csrf token and logged in after " + attempt + " attempts!");
        }
    }

    private void getLoginPageCookieValue() {
        log.info("getLoginPageCookieValue");
        Set<Cookie> cookies = WebDriverRunner.getWebDriver().manage().getCookies();

        if (cookies.size() > 1) {
            List<Cookie> jsessionidCookies = new ArrayList<>();
            for (Cookie item : cookies) {
                if (item.getName().equals(COOKIE_NAME)) {
                    jsessionidCookies.add(item);
                }
            }
            cookieValue = jsessionidCookies.get(jsessionidCookies.size() - 1).getValue();
        } else {
            cookieValue = WebDriverRunner.getWebDriver().manage().getCookieNamed(COOKIE_NAME).getValue();
        }

        log.info("Cookie '" + COOKIE_NAME + "' value is: " + cookieValue);

        if (cookieValue == null) {
            throw new AssertionError(COOKIE_NAME + " cookie in NULL!");
        }
    }


    private String fetchCsrfToken() {
        log.info("fetchCsrfToken");

        csrfToken = executeJavaScript(GET_TOKEN_JS_CODE);
        log.info("Login page CSRF Token is: " + csrfToken);

        if (csrfToken == null) {
            throw new AssertionError("csrfToken in NULL!");
        }

        return csrfToken;
    }

    private void authenticateSessionAndSetNewCookie() {
        log.info("authenticateSessionAndSetNewCookie");
        log.info(String.format("Trying to authenticate with: login=%s; csrf_token=%s;", userName, csrfToken));

        HttpClient httpclient = HttpClients.createDefault();
        HttpResponse response = null;
        try {
            //Execute and get the response.
            response = httpclient.execute(createPostSecurityCheck());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Fetch Cookie for next request
        assert response != null;

        log.info("Response on POST security check request\n" + response.toString());

        Header[] header = response.getHeaders("Set-Cookie");
        String rawSessionIdCookie = header[0].getValue();
        rawSessionIdCookie = rawSessionIdCookie.replace("JSESSIONID=", "");
        int index = rawSessionIdCookie.indexOf(";");
        sessionIdCookie = rawSessionIdCookie.substring(0, index);

        if (sessionIdCookie.equals(cookieValue)) {
            throw new AssertionError("'" + COOKIE_NAME + "' was not updated after executing Security Check");
        }

        WebDriverRunner.getWebDriver().manage().deleteCookieNamed(COOKIE_NAME);
        log.info("Cleaned old cookie '" + COOKIE_NAME + "' with value: " + cookieValue);

       /* if (Driver.isIE() && WebDriverRunner.getWebDriver().manage().getCookieNamed(COOKIE_NAME) != null) {
            log.info("Old cookie '" + COOKIE_NAME + "' with value: " + cookieValue + " haven't been deleted on IE browser.Try to delete all cookies.");
            WebDriverRunner.getWebDriver().manage().deleteAllCookies(); // to force deleting all cookies on IE browser
        }*/
        Cookie newCookie = new Cookie.Builder(COOKIE_NAME, sessionIdCookie).domain("www.pinterest.com").build();

        WebDriverRunner.getWebDriver().manage().addCookie(newCookie);
        log.info("Set new cookie '" + COOKIE_NAME + "' with value: " + sessionIdCookie);

        Cookie requestIdCookie = new Cookie.Builder("X-REQUEST-ID", "requestIdCookie").domain("www.pinterest.com").build();
        WebDriverRunner.getWebDriver().manage().deleteCookieNamed(requestIdCookie.getName());
        log.info("Set new cookie '" + requestIdCookie.getName() + "' with value: " + requestIdCookie.getValue());
        WebDriverRunner.getWebDriver().manage().addCookie(requestIdCookie);
    }

    private HttpPost createPostSecurityCheck() {
        log.info("createPostSecurityCheck");
        HttpPost httpPost = new HttpPost(SECURITY_CHECK_URL);

        // Set POST request headers
        httpPost.setHeader("Cookie", COOKIE_NAME + "=" + cookieValue + ";");

        // Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<>(3);
        params.add(new BasicNameValuePair("j_username", userName));
        params.add(new BasicNameValuePair("j_password", userPassword));
        params.add(new BasicNameValuePair("_csrf", csrfToken));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            //Execute and get the response.
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return httpPost;
    }

}

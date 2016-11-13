package ru.maratk;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.junit.*;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Category(IntegrationTest.class)
public class SimpleIntegrationTest {

    // HSQL database server
    private static final org.hsqldb.Server HSQLDBSERVER = new org.hsqldb.Server();
    private static final int HSQLDBSERVER_PORT = 9001;

    // HSQL database name
    private static final String DATABASENAME = "authusers";
    private static final String DATABASEPATH = "mem:authusers";

    // embedded Tomcat instance and properties
    private static final Tomcat TOMCAT = new Tomcat();
    private static final int EMBEDDED_TOMCAT_PORT = 8888;
    private static final String EMBEDDED_TOMCAT_BASE_DIR = "/var/tmp";
    private static final String EMBEDDED_TOMCAT_APP_BASE = "/";
    private static final boolean EMBEDDED_TOMCAT_AUTO_DEPLOY = true;
    private static final boolean EMBEDDED_TOMCAT_DEPLOY_ON_STARTUP = true;

    // file of test build
    private static final File TEST_BUILD = new File("target/welcomeauthwebapp.war");

    // deployed webapp context
    private static final String APP_CONTEXT_PATH = "/";

    // welcome page URL
    private static final String WELCOME_PAGE_URL = (new StringBuilder())
            .append("http://")
            .append("localhost")
            .append(":")
            .append(EMBEDDED_TOMCAT_PORT)
            .append(APP_CONTEXT_PATH)
            .append("welcome").toString();

    // new user with password
    private static final String NEW_USER_USERNAME = "admin2";
    private static final String NEW_USER_PASSWORD = "Password1";

    @BeforeClass
    public static void init() throws LifecycleException, MalformedURLException {

        // init and start embedded database
        HSQLDBSERVER.setDatabaseName(0, DATABASENAME);
        HSQLDBSERVER.setDatabasePath(0, DATABASEPATH);
        HSQLDBSERVER.setPort(HSQLDBSERVER_PORT);
        HSQLDBSERVER.setSilent(true);
        HSQLDBSERVER.start();

        // embedded Tomcat work dir
        File embeddedTomcatWorkDir = new File(EMBEDDED_TOMCAT_BASE_DIR + "/work");
        // delete embedded Tomcat work dir without check
        embeddedTomcatWorkDir.delete();

        // init and up embedded Tomcat and deploy app
        TOMCAT.setBaseDir(EMBEDDED_TOMCAT_BASE_DIR);
        TOMCAT.setPort(EMBEDDED_TOMCAT_PORT);
        TOMCAT.getHost().setAppBase(EMBEDDED_TOMCAT_APP_BASE);
        TOMCAT.getHost().setAutoDeploy(EMBEDDED_TOMCAT_AUTO_DEPLOY);
        TOMCAT.getHost().setDeployOnStartup(EMBEDDED_TOMCAT_DEPLOY_ON_STARTUP);
        // enable naming
        TOMCAT.enableNaming();
        // add webapp
        TOMCAT.addWebapp(TOMCAT.getHost(), APP_CONTEXT_PATH, TEST_BUILD.getAbsolutePath());
        // start embedded tomcat
        TOMCAT.start();
    }

    @Test
    public void loginTest() throws Exception {
        // create instance of HTMLUnit web client
        WebClient webClient = new WebClient(BrowserVersion.FIREFOX_38);
        // enable redirects
        webClient.getOptions().setRedirectEnabled(true);
        // enable JS
        webClient.getOptions().setJavaScriptEnabled(true);

        // go to welcome page, but first we must be redirected to login page
        HtmlPage loginPage = webClient.getPage(new URL(WELCOME_PAGE_URL));
        // check redirect to login page and
        // if succeed, get submit login button
        HtmlElement loginSubmitButton = checkAndFillLoginPageAndReturnSubmitButton(loginPage, NEW_USER_USERNAME, NEW_USER_PASSWORD, null);
        // by click on submit button
        // we must be redirected on login page with wrong password label
        loginPage = loginSubmitButton.click();
        // check login page with wrong password label existence
        HtmlElement signUpPageLink = checkLoginPageAndReturnSignInLink(loginPage, "wrong-credentials-mark");
        // get sign-up-page by clicking on link
        HtmlPage signUpPage = signUpPageLink.click();
        // check sign-up page, fill it and get submit button
        HtmlElement signUpSubmitButton = checkAndFillSignupPageAndReturnSubmitButton(signUpPage, NEW_USER_USERNAME, NEW_USER_PASSWORD, null);
        // we must be redirected to welcome page filled by our username
        HtmlPage welcomePage = signUpSubmitButton.click();
        // check welcome page for new user
        checkWelcomePageForUser(welcomePage, NEW_USER_USERNAME);
        // from welcome page get log-out button
        List<HtmlElement> logoutButtonsList = welcomePage.getBody().getElementsByAttribute("input", "id", "so_button");
        Assert.assertNotNull(logoutButtonsList);
        Assert.assertTrue(logoutButtonsList.size() == 1);
        HtmlElement logoutButton = logoutButtonsList.get(0);
        // click on log-out button and go to log-in page
        loginPage = logoutButton.click();
        // check login page for presence of log-out label
        checkLoginPageAndReturnSignInLink(loginPage, "logout-mark");
    }

    // check, fill login page and return submit button
    private HtmlElement checkAndFillLoginPageAndReturnSubmitButton(HtmlPage loginPage, String username, String password, String messageLabelId){
        // var for submit input instance
        HtmlElement submitInput = null;

        // if we must find some label on login page
        if(messageLabelId != null){
            List<HtmlElement> expectedMessageLabels = loginPage.getBody().getElementsByAttribute("div", "id", messageLabelId);
            Assert.assertTrue(expectedMessageLabels.size() == 1);
        }
        // login form element
        List<HtmlElement> loginForms = loginPage.getBody().getElementsByAttribute("form", "name", "loginForm");
        // assert, that we find login form element on page
        Assert.assertNotNull(loginForms);
        Assert.assertTrue(loginForms.size() == 1);
        HtmlElement loginForm = loginForms.get(0);
        Assert.assertNotNull(loginForm);
        // find input elements for login form
        List<HtmlElement> inputHtmlElements = loginForm.getHtmlElementsByTagName("input");
        // flags for input elements
        boolean usernameInputFinded = false, passwordInputFinded = false, submitInputFinded = false;
        // find submit elements and fill form fields
        for(HtmlElement e: inputHtmlElements){
            String nameAttrForElement = e.getAttribute("name");
            switch (nameAttrForElement){
                case "j_username":
                    usernameInputFinded = true;
                    e.setAttribute("value", username);
                    break;
                case "j_password":
                    passwordInputFinded = true;
                    e.setAttribute("value", password);
                    break;
                case "submit":
                    submitInputFinded = true;
                    submitInput = e;
                    break;
            }
        }
        // assert, that we find used inputs
        Assert.assertTrue(usernameInputFinded);
        Assert.assertTrue(passwordInputFinded);
        Assert.assertTrue(submitInputFinded);

        return submitInput;
    }

    // check, fill login page and return submit button
    private HtmlElement checkLoginPageAndReturnSignInLink(HtmlPage loginPage, String messageLabelId){
        // if we must find some label on page
        if(messageLabelId != null){
            List<HtmlElement> expectedMessageLabels = loginPage.getBody().getElementsByAttribute("div", "id", messageLabelId);
            Assert.assertTrue(expectedMessageLabels.size() == 1);
        }
        // login form element
        List<HtmlElement> loginForms = loginPage.getBody().getElementsByAttribute("form", "name", "loginForm");
        // assert, that we find login form element on page
        Assert.assertNotNull(loginForms);
        Assert.assertTrue(loginForms.size() == 1);
        HtmlElement loginForm = loginForms.get(0);
        Assert.assertNotNull(loginForm);
        // find input elements for login form
        List<HtmlElement> inputHtmlElements = loginForm.getHtmlElementsByTagName("input");
        // flags for input elements
        boolean usernameInputFinded = false, passwordInputFinded = false, submitInputFinded = false;
        // find submit elements and fill form fields
        for(HtmlElement e: inputHtmlElements){
            String nameAttrForElement = e.getAttribute("name");
            switch (nameAttrForElement){
                case "j_username":
                    usernameInputFinded = true;
                    break;
                case "j_password":
                    passwordInputFinded = true;
                    break;
                case "submit":
                    submitInputFinded = true;
                    break;
            }
        }
        // assert, that we find used inputs
        Assert.assertTrue(usernameInputFinded);
        Assert.assertTrue(passwordInputFinded);
        Assert.assertTrue(submitInputFinded);

        // find sign-up link and return it
        List<HtmlElement> signUpLinks = loginPage.getBody().getElementsByAttribute("a", "id", "sign-up-link");
        Assert.assertTrue(signUpLinks.size() == 1);
        Assert.assertNotNull(signUpLinks.get(0));

        return signUpLinks.get(0);
    }

    // check and fill signup page
    private HtmlElement checkAndFillSignupPageAndReturnSubmitButton(HtmlPage signupPage, String username, String password, String messageLabelId){
        // if we must find some label on page
        if(messageLabelId != null){
            List<HtmlElement> expectedMessageLabels = signupPage.getBody().getElementsByAttribute("div", "id", messageLabelId);
            Assert.assertTrue(expectedMessageLabels.size() == 1);
        }
        // var for signup submit input instance
        HtmlElement signupSubmitInput = null;
        // signup form element
        List<HtmlElement> signUpForms = signupPage.getBody().getElementsByAttribute("form", "name", "signupForm");
        // assert, that we find signup form element on page
        Assert.assertNotNull(signUpForms);
        Assert.assertTrue(signUpForms.size() == 1);
        HtmlElement signUpForm = signUpForms.get(0);
        Assert.assertNotNull(signUpForm);

        // find input elements for signup form
        List<HtmlElement> signupInputHtmlElements = signUpForm.getHtmlElementsByTagName("input");
        // flags for input elements
        boolean signupUsernameInputFinded = false,
                signupPasswordInputFinded = false,
                signupRetryPasswordInputFinded = false,
                signupSubmitInputFinded = false;
        // find signup submit elements and fill form fields
        for(HtmlElement e: signupInputHtmlElements){
            String nameAttrForElement = e.getAttribute("name");
            switch (nameAttrForElement){
                case "username":
                    signupUsernameInputFinded = true;
                    e.setAttribute("value", username);
                    break;
                case "password":
                    signupPasswordInputFinded = true;
                    e.setAttribute("value", password);
                    break;
                case "retryPassword":
                    signupRetryPasswordInputFinded = true;
                    e.setAttribute("value", password);
                    break;
                case "submit":
                    signupSubmitInputFinded = true;
                    signupSubmitInput = e;
                    break;
            }
        }

        // assert, that we find used inputs
        Assert.assertTrue(signupUsernameInputFinded);
        Assert.assertTrue(signupPasswordInputFinded);
        Assert.assertTrue(signupRetryPasswordInputFinded);
        Assert.assertTrue(signupSubmitInputFinded);

        return signupSubmitInput;
    }

    // check for welcome page generation
    private void checkWelcomePageForUser(HtmlPage welcomePage, String username){
        // get 'username' span
        List<HtmlElement> usernameSpanList = welcomePage.getBody().getElementsByAttribute("span", "id", "username");
        Assert.assertNotNull(usernameSpanList);
        Assert.assertTrue(usernameSpanList.size() == 1);
        // assert, that welcome for auth user
        Assert.assertTrue(usernameSpanList.get(0).getTextContent().equals(username));
    }

    @After
    public void stopHsqlDatabase() {
        HSQLDBSERVER.stop();
    }

    @AfterClass
    public static void stopEmbeddedTomcat() throws LifecycleException {
        // stop and destroy embedded tomcat
        TOMCAT.stop();
        TOMCAT.destroy();
    }
}
# Simple webapp for welcome auth users on Tomcat
Application use container managed security provided by Tomcat. Authentication information stored in a HSQLDB database (app use JDBCRealm), with JDBC URL: [jdbc:hsqldb:hsql://localhost:9001/authusers](jdbc:hsqldb:hsql://localhost:9001/authusers), in table "users", with fields "user_name", "user_pass", "role_name". Liquibase servlet listener create this table on deploy.

If user have role *welcome-webapp-user*, he can access */welcome* page.

Builded web archive path: *build/welcomeauthwebapp.war*.

### Integration test
Integration test use embedded Tomcat v.8.0.38, in-memory HSQLDB v.2.3.4, and HTMLUnit v.2.18.

Before running integration test you must ensure, that:

1. Working directory of embedded Tomcat (by default */var/tmp*) can be created and deleted by user, that run this test.
2. Embedded HSQLDB can listen port 9001.

##### Integration test steps:
1. Go to */welcome* page URL.
2. Check redirect to */sign-in* page.
3. Fill */sign-in* form, send that form.
4. Check redirect to */sign-in* page with label of wrong credentials.
5. Click on the link and go to */sign-up* page.
6. Fill */sign-up* form, send that form.
7. Check redirect to */welcome* page with sended username.
8. Click to the button and go to */sign-out* page.
9. Check redirect to */sign-in* page with sign-out label.

To run integration test:
```sh
$ mvn verify
```
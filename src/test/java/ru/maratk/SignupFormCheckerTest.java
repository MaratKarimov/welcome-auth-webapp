package ru.maratk;

import org.junit.Assert;
import org.junit.Test;
import ru.maratk.enums.SignupMessages;
import ru.maratk.tools.SignupFormChecker;

public class SignupFormCheckerTest {

    // instance of form checker
    private SignupFormChecker signupFormChecker = new SignupFormChecker();

    @Test
    public void testForUsernameChecks(){
        // short username
        Assert.assertEquals(signupFormChecker.checkUsername("admi"), SignupMessages.USER_NAME_INCORRECT);
        // username with space in middle
        Assert.assertEquals(signupFormChecker.checkUsername("ad mi"), SignupMessages.USER_NAME_INCORRECT);
        // username with ampersand in middle
        Assert.assertEquals(signupFormChecker.checkUsername("ad&mi"), SignupMessages.USER_NAME_INCORRECT);
        // username with russian character
        Assert.assertEquals(signupFormChecker.checkUsername("adгmin"), SignupMessages.USER_NAME_INCORRECT);
        // username with french character
        Assert.assertEquals(signupFormChecker.checkUsername("çadmin"), SignupMessages.USER_NAME_INCORRECT);
        // valid username
        Assert.assertEquals(signupFormChecker.checkUsername("admin"), SignupMessages.USER_NAME_IS_OK);
    }

    @Test
    public void testForPasswordChecks(){
        // not identical passwords
        Assert.assertEquals(signupFormChecker.checkPassword("password1", "password2"), SignupMessages.PASSWORDS_ARE_NOT_EQUAL);
        // short password
        Assert.assertEquals(signupFormChecker.checkPassword("Passwo1", "Passwo1"), SignupMessages.PASSWORD_IS_EASY);
        // without capital letter password
        Assert.assertEquals(signupFormChecker.checkPassword("password1", "password1"), SignupMessages.PASSWORD_IS_EASY);
        // without small letter password
        Assert.assertEquals(signupFormChecker.checkPassword("PASSWORD1", "PASSWORD1"), SignupMessages.PASSWORD_IS_EASY);
        // without any digit password
        Assert.assertEquals(signupFormChecker.checkPassword("Password", "Password"), SignupMessages.PASSWORD_IS_EASY);
        // valid password
        Assert.assertEquals(signupFormChecker.checkPassword("Password1", "Password1"), SignupMessages.PASSWORD_IS_OK);
        // valid password with russian character
        Assert.assertEquals(signupFormChecker.checkPassword("Password1п", "Password1п"), SignupMessages.PASSWORD_IS_OK);
    }
}
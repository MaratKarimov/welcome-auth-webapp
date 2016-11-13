package ru.maratk.tools;

import ru.maratk.enums.SignupMessages;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 * Checker for sign-up form
 */
public class SignupFormChecker {

    /// username check parameters

    // min username length
    private final static int MIN_USERNAME_LENGTH = 5;
    // charset for test chars
    private final static String CHARSET_TO_TEST = "US-ASCII";

    /// password check parameters

    // min password length
    private final static int MIN_PASSWORD_LENGTH = 8;

    // check username
    public SignupMessages checkUsername(final String username){
        // check length of username
        if(username.length() < MIN_USERNAME_LENGTH)
            return SignupMessages.USER_NAME_INCORRECT;
        // create ascii encoder
        CharsetEncoder asciiEncoder = Charset.forName(CHARSET_TO_TEST).newEncoder();
        // boolean is chars valid
        boolean isCharsOfUsernameValid = true;
        for(int i = 0; i < username.length(); i++){
            // take char to test
            char charToTest = username.charAt(i);
            // check by ascci encoder
            isCharsOfUsernameValid = asciiEncoder.canEncode(charToTest);
            // if invalid, break
            if(!isCharsOfUsernameValid){
                break;
            }
            // check is char letter/digit or not
            isCharsOfUsernameValid = Character.isLetterOrDigit(charToTest);
            // if invalid, break
            if(!Character.isLetterOrDigit(charToTest)){
                break;
            }
        }
        if(isCharsOfUsernameValid)
            return SignupMessages.USER_NAME_IS_OK;
        else
            return SignupMessages.USER_NAME_INCORRECT;
    }

    // check password
    public SignupMessages checkPassword(final String password, final String retryPassword){
        // if retry password don't equal to first password
        if(!password.equals(retryPassword))
            return SignupMessages.PASSWORDS_ARE_NOT_EQUAL;
        // if password length is less than minimum
        if(password.length() < MIN_PASSWORD_LENGTH)
            return SignupMessages.PASSWORD_IS_EASY;
        // check if password contain capital letter, small letter and digit
        boolean isPasswordContainCapitalLetter = false,
                isPasswordContainSmallLetter = false,
                isPasswordContainDigit = false;
        for(int i = 0; i < password.length(); i++){
            // take char to test
            char charToTest = password.charAt(i);
            // check for capital letter
            if(!isPasswordContainCapitalLetter)
                isPasswordContainCapitalLetter = isPasswordContainCapitalLetter | Character.isUpperCase(charToTest);
            // check for capital letter
            if(!isPasswordContainSmallLetter)
                isPasswordContainSmallLetter = isPasswordContainSmallLetter | Character.isLowerCase(charToTest);
            // check for digit
            if(!isPasswordContainDigit)
                isPasswordContainDigit = isPasswordContainDigit | Character.isDigit(charToTest);
            // if all checks passed, then break
            if(isPasswordContainCapitalLetter &
                    isPasswordContainSmallLetter &
                    isPasswordContainDigit){
                break;
            }
        }
        // if all checks passed
        if (isPasswordContainCapitalLetter &
                isPasswordContainSmallLetter &
                isPasswordContainDigit)
            return SignupMessages.PASSWORD_IS_OK;
        // if some checks failed
        else
            return SignupMessages.PASSWORD_IS_EASY;
    }
}
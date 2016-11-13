<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="ru.maratk.enums.SignupMessages" %>
<html>
    <body>
        <%-- check message attribute of request --%>
        <c:catch var="exception">
            <c:if test="${message == 'USER_ALREADY_EXISTS'}">
                <div style="color:#FF0000" id="user-already-exists-label">User already exists!</div>
            </c:if>
            <c:if test="${message == 'USER_NAME_INCORRECT'}">
                <div style="color:#FF0000" id="user-name-incorrect-label">Username should be longer than 4 characters and it can only contain alphanumeric characters (letters A-Z, numbers 0-9)!</div>
            </c:if>
            <c:if test="${message == 'PASSWORDS_ARE_NOT_EQUAL'}">
                <div style="color:#FF0000" id="passwords-don't-equal-label">Passwords are not equal!</div>
            </c:if>
            <c:if test="${message == 'PASSWORD_IS_EASY'}">
                <div style="color:#FF0000" id="password-is-easy-label">Password is easy: you should use digits, capital and small letters, it should be 8 or more characters!</div>
            </c:if>
        </c:catch>
        <form name='signupForm' action="<%=request.getContextPath()%>/registernewuser" method='POST'>
            <table>
                <tr>
                    <%-- username input --%>
                    <td>User:</td>
                    <td><input type='text' name='username' value='' id="usernameInput"/></td>
                </tr>
                <tr>
                    <%-- password input --%>
                    <td>Password:</td>
                    <td><input type='password' name='password' id="passwordInput"/></td>
                </tr>
                <tr>
                    <%-- retry password input --%>
                    <td>Retry password:</td>
                    <td><input type='password' name='retryPassword' id="retryPasswordInput"/></td>
                </tr>
                <tr>
                    <%-- submit input --%>
                    <td colspan='2'><input name="submit" type="submit" value="submit" /></td>
                </tr>
		    </table>
		</form>
    </body>
</html>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="ru.maratk.enums.SigninMessages" %>
<html>
    <body>
        <%-- try to check sended message --%>
        <c:catch var="exception">
            <%-- 'logout' type message --%>
            <c:if test="${message == 'LOGOUT'}">
                <div id="logout-mark" style="color:#008000">Log out successfull!</div>
            </c:if>
            <%-- 'wrong credentials' type message --%>
            <c:if test="${message == 'WRONG_CREDENTIALS'}">
                <div id="wrong-credentials-mark" style="color:#FF0000">Wrong username or password!</div>
            </c:if>
        </c:catch>
        <%-- 'empty credentials' field, checked by JavaScript --%>
        <div id="empty-credentials-mark" style="color:#FFA500;display:none;">You should fill fields of form!</div>
        <%-- login form --%>
        <form name='loginForm' action="j_security_check" method='POST' id="login-form" onsubmit="return validateLoginForm()">
            <table>
                <tr>
                    <%-- username input --%>
                    <td>User:</td>
                    <td><input type='text' name='j_username' value='' id="usernameInput"/></td>
                    <td><a id="sign-up-link" href="<%=request.getContextPath()%>/sign-up">Registration</a></td>
                </tr>
                <tr>
                    <%-- password input --%>
                    <td>Password:</td>
                    <td><input type='password' name='j_password' id="passwordInput"/></td>
                </tr>
                <tr>
                    <%-- submit input --%>
                    <td colspan='2'><input name="submit" type="submit" value="submit" /></td>
                </tr>
		    </table>
		</form>
        <%-- js check empty fields on form --%>
        <script>
            function validateLoginForm() {
                var userInput = document.getElementById('usernameInput');
                if (userInput.value == ""){
                    document.getElementById('empty-credentials-mark').style.display = "block";
                    return false;
                }
                var passwordInput = document.getElementById('passwordInput');
                if (passwordInput.value == ""){
                    document.getElementById('empty-credentials-mark').style.display = "block";
                    return false;
                }
                return true;
            }
        </script>
    </body>
</html>
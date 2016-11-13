<%@page contentType="text/html" pageEncoding="UTF-8"%>
<html>
    <body>
        <%-- greeting message --%>
        <h2><span id="greeting">Hello</span>, <span id="username"><%= request.getUserPrincipal().getName() %></span>!</td>
        <%-- Sign-out button --%>
        <div>
            <form name="logoutForm" action="<%=request.getContextPath()%>/sign-out">
                <input type="submit" value="Sign-out" id="so_button" />
            </form>
        </div>
        <%-- js for greeting --%>
        <script>
            var currentUserDate = new Date();
            var hours = currentUserDate.getHours();
            if ((hours >= 6) & ((hours < 10))) {
                greeting = "Good morning";
            } else if ((hours >= 10) & ((hours < 18))) {
                greeting = "Good day";
            } else if ((hours >= 18) & ((hours < 22))) {
                greeting = "Good evening";
            } else {
                greeting = "Good night";
            }
            document.getElementById("greeting").innerHTML = greeting;
        </script>
    </body>
</html>
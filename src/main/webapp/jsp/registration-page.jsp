<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
    <title>Registration</title>
</head>
<body>

<div class="container-fluid">

    <div class="row"><div class="col"></div>

        <div class="col-5 border border-info my-5 pt-2" style="min-width: 500px">
            <jsp:include page="/jsp/inner/notifications.jsp"/>

            <h1><fmt:message key="registration.page.main.form.label" /></h1>

            <form action="${pageContext.request.contextPath}/registration" method="post">
                <p class="input-group">
                    <label for="username" class="input-group-text"><fmt:message key="registration.page.form.label.username" /></label>
                    <input name="username" id="username" type="text" class="form-control" pattern="^[0-9a-zA-Z-_]{1,15}$" placeholder="1-15 symbols (a-Z0-9-_)">
                </p>
                <p class="input-group">
                    <label for="password" class="input-group-text"><fmt:message key="registration.page.form.label.password" /></label>
                    <input name="password" id="password" type="text" class="form-control" pattern="^[0-9a-zA-Z_-]{4,10}$" placeholder="4-10 symbols (a-Z0-9-_)">
                </p>
                <p class="input-group">
                    <label for="phone-number" class="input-group-text"><fmt:message key="registration.page.form.label.phone" /></label>
                    <input name="phone-number" id="phone-number" type="text" class="form-control" pattern="^[+]?[0-9() -]{10,18}" placeholder="+11 (111) 111-11-11">
                </p>
                <p style="display: flex;flex-direction: row-reverse">
                    <button type="submit" class="btn btn-lg btn-primary"><fmt:message key="registration.page.form.button.submit" /></button>
                </p>
            </form>

        </div>


    <div class="col"></div></div>
</div>

</body>
</html>

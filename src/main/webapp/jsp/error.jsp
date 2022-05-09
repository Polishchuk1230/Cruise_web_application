<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
    <title>Error page</title>
</head>
<body>

<div class="container px-0 border border-top-0 border-warning align-items-center justify-content-center" style="max-width:991px; min-height: 600px;">

    <%-- ------------------------------------  Topbar section  ---------------------------------------------------- --%>
    <jsp:include page="/jsp/inner/topbar.jsp" />

    <h2 class="container text-warning p-3 mx-2 mt-5">
        ${requestScope.get("jakarta.servlet.error.status_code")}<br />
        <c:choose>
            <c:when test="${requestScope.get('jakarta.servlet.error.status_code') eq '404'}">
                <fmt:message key="error.page.message.404" />
            </c:when>
            <c:when test="${requestScope.get('jakarta.servlet.error.status_code') eq '403'}">
                <fmt:message key="error.page.message.403" />
            </c:when>
            <c:otherwise>
                ${requestScope.get("jakarta.servlet.error.message")}
            </c:otherwise>
        </c:choose>
    </h2>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM" crossorigin="anonymous"></script>
</body>
</html>

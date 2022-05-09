<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ page import="ua.com.cruises.model.Role" %>
<%@ page import="static ua.com.cruises.controller.uris.AppPath.*" %>

<%--  -----------------------------------  Topbar section  ---------------------------------------------------  --%>
<nav class="navbar navbar-dark bg-dark px-4">
    <a href="${pageContext.request.contextPath}" class="navbar-brand text-warning">
        <img src="${pageContext.request.contextPath}/static/images/boat-sharp.svg" alt="cruise-company-icon" style="max-height: 30px; filter:invert(100%); " />
        <fmt:message key="app.text.Logo" />
    </a>

    <c:choose>
        <c:when test="${empty sessionScope.user}">
            <button class="btn btn-outline-light" type="button" data-bs-toggle="collapse" data-bs-target="#login-form-container" aria-expanded="false" aria-controls="login-form-container">
                <fmt:message key="topbar.link.sign.in" />
            </button>
        </c:when>
        <c:when test="${not empty sessionScope.user}">
            <a href="<%=request.getContextPath() + PATH_TO_USER_PROFILE%>"><span class="text-light position-absolute top-0 end-0 me-1">
                <fmt:message key="topbar.message.hello.user" />, ${sessionScope.user.username}.
            </span></a>
            <a class="btn btn-sm btn-outline-warning position-absolute bottom-0 end-0 m-1"
               href="<%=request.getContextPath() + PATH_TO_LOGOUT%>" role="button">
                <fmt:message key="index.page.form.button.log.out" />
            </a>
        </c:when>
    </c:choose>
</nav>

<%--  ----------------------------------- Admin Navbar section  ----------------------------------------------  --%>
<c:if test="${sessionScope.user.roles.contains(Role.ADMIN)}">
    <ul class="nav justify-content-center">
        <li class="nav-item"><a href="<%=request.getContextPath() + PATH_TO_ORDER_ADMINISTRATION_PAGE%>" class="nav-link mx-3"><fmt:message key="app.link.orders" /></a></li>
        <li class="nav-item"><a href="<%=request.getContextPath() + PATH_TO_CRUISE_ADMINISTRATION_PAGE%>" class="nav-link mx-3"><fmt:message key="app.link.cruises" /></a></li>
        <li class="nav-item"><a href="<%=request.getContextPath() + PATH_TO_BOAT_ADMINISTRATION_PAGE%>" class="nav-link mx-3"><fmt:message key="app.link.boats" /></a></li>
        <li class="nav-item"><a href="<%=request.getContextPath() + PATH_TO_CREW_ADMINISTRATION_PAGE%>" class="nav-link mx-3"><fmt:message key="app.link.crews" /></a></li>
        <li class="nav-item"><a href="<%=request.getContextPath() + PATH_TO_PORT_ADMINISTRATION_PAGE%>" class="nav-link mx-3"><fmt:message key="app.link.ports" /></a></li>
    </ul>
</c:if>

<%--  -----------------------------------  Logging in form-container  ----------------------------------------  --%>
<div id="login-form-container" class="container-fluid collapse bg-light">
    <form action="${pageContext.request.contextPath}<%=PATH_TO_LOGIN%>" method="post">
        <label for="login" class="form-label mt-2"><fmt:message key="index.page.form.label.login" />:</label>
        <input id="login" name="login" type="text" class="form-control" />

        <label for="password" class="form-label mt-2"><fmt:message key="index.page.form.label.password" />:</label>
        <input id="password" name="password" type="password" class="form-control" />

        <input class="btn btn-primary my-2" type="submit" value="<fmt:message key="index.page.form.button.log.in" />" />
        <a href="${pageContext.request.contextPath}<%=PATH_TO_REGISTRATION%>" class="btn btn-outline-primary my-2">
            <fmt:message key="index.page.form.link.registration" />
        </a>
    </form>
</div>
<%@ page import="ua.com.cruises.model.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="static ua.com.cruises.controller.uris.AppPath.*" %>
<%@ page import="ua.com.cruises.model.Role" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="my" uri="/myTags" %>

<html>
<head>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
    <title>User profile</title>
    <meta name="viewport">
</head>
<body>
<div class="container px-0 border border-top-0 border-warning" style="max-width:991px;">

    <%-- ------------------------------------  Topbar section  ---------------------------------------------------- --%>
    <jsp:include page="/jsp/inner/topbar.jsp" />

<%-- ----------------------------------------- Upload new documents form ------------------------------------------ --%>
    <c:if test="${empty param.user || param.user.equalsIgnoreCase(sessionScope.user.username)}">
        <form action="${pageContext.request.contextPath}<%=PATH_TO_USER_PHOTO_UPLOAD_PAGE%>" method="post" enctype="multipart/form-data">
            <div id="file-input-container">
                <p class="input-group my-3">
                    <label for="file1" class="input-group-text"><fmt:message key="profile.page.documents.form.file" /> 1</label>
                    <input name="file1" id="file1" type="file" class="form-control" />
                </p>
                <p class="d-grid gap-2 mb-3">
                    <button type="button" class="btn btn-sm btn-outline-primary" title="Add another fileinput" onclick="addFileInput()">+</button>
                </p>
            </div>
            <p class="container-fluid" style="display: flex;flex-direction: row-reverse;">
                <button type="submit" class="btn btn-lg btn-primary">
                    <fmt:message key="profile.page.documents.form.button.submit" />
                </button>
            </p>
        </form>
    </c:if>

<%-- ---------------------------------------------- User's documents ---------------------------------------------- --%>
    <div class="container-fluid" style="display: flex; flex-direction: column">
        <c:choose>
            <c:when test="${images.size() > 0}">
                <h3><fmt:message key="profile.page.documents.title" /></h3>
                <p>
                    <c:forEach var="image" items="${images}">
                        <img src="${pageContext.request.servletContext.getInitParameter("upload.path")}${image}" style="max-width: 100px;" />
                    </c:forEach>
                </p>
            </c:when>
            <c:otherwise>
                <h3><fmt:message key="profile.page.no.documents.title" /></h3>
            </c:otherwise>
        </c:choose>
    </div>

<%-- ---------------------------------------------- User's orders ------------------------------------------------- --%>
    <div class="mx-3" style="min-width: 400px;">
        <c:forEach var="order" items="${orders}">
            <p class="m-2" style="display: inline-block">
                <fmt:message key="profile.page.content.cruise.status.title" />:
                <c:choose>
                    <c:when test="${order.getCruise().getCruiseStatus() == 'REGISTRATION_IN_PROGRESS'}">
                        <fmt:message key="tag.cruise.message.status.registration.in.progress" />
                    </c:when>
                    <c:when test="${order.getCruise().getCruiseStatus() == 'REGISTRATION_CLOSED'}">
                        <fmt:message key="tag.cruise.message.status.registration.closed" />
                    </c:when>
                    <c:when test="${order.getCruise().getCruiseStatus() == 'IN_PROGRESS'}">
                        <fmt:message key="tag.cruise.message.status.in.progress" />
                    </c:when>
                    <c:when test="${order.getCruise().getCruiseStatus() == 'COMPLETED'}">
                        <fmt:message key="tag.cruise.message.status.completed" />
                    </c:when>
                    <c:otherwise>???</c:otherwise>
                </c:choose>
                <br />
                <my:order obj="${order}" />
            </p>
        </c:forEach>
    </div>

        <%-- -------------------------------------- Form for toggling Role.ADMIN ---------------------------------- --%>
        <c:if test="${not empty param.user and sessionScope.user.roles.contains(Role.ADMIN)}">
            <form class="m-5" action="<%=request.getContextPath() + PATH_TO_USER_PROFILE%>" method="post">
                <input name="user" value="${param.user}" type="text" hidden>
                <c:set var="isUserAdmin"
                       value="${not empty requestScope.profileOwner and requestScope.profileOwner.roles.contains(Role.ADMIN)}"/>
                <button class="btn btn-sm btn-outline-${isUserAdmin ? 'danger' : 'primary'}"
                        type="submit">
                    <c:choose>
                        <c:when test="${isUserAdmin}">
                            <fmt:message key="profile.page.button.admin.off" />
                        </c:when>
                        <c:otherwise>
                            <fmt:message key="profile.page.button.admin.on" />
                        </c:otherwise>
                    </c:choose>
                </button>
            </form>
        </c:if>

</div>
<script>
    function addFileInput() {
        let container = document.getElementById("file-input-container");

        let element1 = document.createElement("p");
        element1.setAttribute("class", "input-group mb-3");

        let child1 = document.createElement("label");
        child1.setAttribute("class", "input-group-text");
        child1.setAttribute("for", "file" + container.childElementCount);
        child1.innerText = "<fmt:message key="profile.page.documents.form.file" /> " + container.childElementCount;

        let child2 = document.createElement("input");
        child2.setAttribute("name", "file" + container.childElementCount);
        child2.setAttribute("id", "file" + container.childElementCount);
        child2.setAttribute("type", "file");
        child2.setAttribute("class", "form-control");

        let child3 = document.createElement("button"); // <-- button to remove added input
        child3.setAttribute("class", "btn btn-outline-primary");
        child3.setAttribute("type", "button");
        child3.setAttribute("onclick", "removeFileInput()")
        child3.innerText = "−";

        element1.appendChild(child1);
        element1.appendChild(child2);
        element1.appendChild(child3);

        container.insertBefore(element1, container.lastElementChild);
    }

    function removeFileInput() {
        let container = document.getElementById("file-input-container");
        container.removeChild(container.childNodes[container.childNodes.length-3])
    }
</script>
</body>
</html>

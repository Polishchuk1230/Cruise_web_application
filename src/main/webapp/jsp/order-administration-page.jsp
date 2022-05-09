<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="static ua.com.cruises.controller.uris.AppPath.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="my" uri="/myTags" %>

<html>
<head>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
    <title>Orders administration</title>
</head>
<body>

<div class="container px-0 border border-top-0 border-warning" style="max-width:991px;">

    <%-- ------------------------------------  Topbar section  ---------------------------------------------------- --%>
    <jsp:include page="/jsp/inner/topbar.jsp" />

    <div class="row">
        <div class="col-1"></div>
        <div class="col">

            <%-- ----------------------------------  Filtration form section  ------------------------------------- --%>
            <form class="form-check form-switch" action="<%=request.getContextPath() + PATH_TO_ORDER_ADMINISTRATION_PAGE%>" method="get">
                <input class="form-check-input" type="checkbox" id="confirmedTooCheck" name="getall" value="true"
                       oninput="this.parentElement.submit()" ${not empty param.getall ? "checked" : ""} />
                <label class="form-check-label" for="confirmedTooCheck">
                    <fmt:message key="order.adm.page.filter.confirmed.too" />
                </label>
            </form>

            <%-- ------------------------------------------  Content section  ------------------------------------- --%>
            <div>
                <c:forEach var="order" items="${orders}">
                    <p class="m-4" style="display: inline-block; min-width: 400px;">
                        <a href="<%=request.getContextPath() + PATH_TO_USER_PROFILE + "?user="%>${order.user.username}">
                            <fmt:message key="order.adm.page.link.profile" />
                        </a>
                        <br />
                        <my:order obj="${order}" />
                        <button type="button"
                                class="btn btn-sm btn-outline-${order.confirmed ? "danger" : "success"}"
                                onclick="fetch('<%=request.getContextPath() + PATH_TO_ORDER_ADMINISTRATION_PAGE + "/id"%>' + ${order.id}, {method: 'PUT'});
                                         setTimeout(() => document.location.reload(), 1000);">
                            <fmt:message key="${order.confirmed ? 'order.adm.page.button.unconfirm' : 'order.adm.page.button.confirm'}" />
                        </button>
                        <br />
                    </p>
                </c:forEach>
            </div>

        </div>
    </div>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM" crossorigin="anonymous"></script>
</body>
</html>

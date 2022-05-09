<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="my" uri="/myTags" %>

<%@ page import="ua.com.cruises.model.Role" %>
<%@ page import="static ua.com.cruises.controller.uris.AppPath.*" %>

<html>
<head>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
    <title>Cruise Company</title>
</head>
<body>

<div class="container px-0 border border-top-0 border-warning" style="max-width:991px;">
    <%-- ------------------------------------  Topbar section  ---------------------------------------------------- --%>
    <jsp:include page="/jsp/inner/topbar.jsp" />

        <jsp:include page="/jsp/inner/notifications.jsp" />

        <%-- ------------------------------  Sorting cruises form  -------------------------------------------- --%>
        <form action="<%=request.getContextPath() + PATH_TO_CATALOGUE%>" method="GET" class="col align-self-center input-group m-0">
            <label for="sort-by" class="input-group-text"><fmt:message key="index.page.form.label.sorted" /></label>
            <select name="sorted" id="sort-by" class="form-control" oninput="this.parentElement.submit()">
                <option><fmt:message key="index.page.form.label.sorted.option.default" /></option>
                <option value="BY_BOOKED_SEATS"><fmt:message key="index.page.form.label.sorted.option.by.seats" /></option>
                <option value="BY_CRUISE_COST"><fmt:message key="index.page.form.label.sorted.option.by.cost" /></option>
                <option value="BY_CRUISE_DURATION"><fmt:message key="index.page.form.label.sorted.option.by.duration" /></option>
            </select>
        </form>

        <%-- ------------------------------  Filtering cruises form  -------------------------------------------- --%>
        <form action="<%=request.getContextPath() + PATH_TO_CATALOGUE%>" method="GET" class="col align-self-center input-group mt-3">
            <div class="col-sm-6">
                <div class="input-group mb-3">
                    <label for="startDateInput" class="input-group-text"><fmt:message key="index.page.form.label.filter.date.start" /></label>
                    <input name="cruiseStartDate" type="date" id="startDateInput" class="form-control"
                        ${not empty sessionScope.get("cruiseStartDate") ? "value=\"".concat(sessionScope.get("cruiseStartDate")).concat("\"") : ""}>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="input-group mb-3">
                    <label for="endDateInput" class="input-group-text"><fmt:message key="index.page.form.label.filter.date.end" /></label>
                    <input name="cruiseEndDate" type="date" id="endDateInput" class="form-control"
                        ${not empty sessionScope.get("cruiseEndDate") ? "value=\"".concat(sessionScope.get("cruiseEndDate")).concat("\"") : ""}>
                </div>
            </div>

            <div class="col-sm-6">
                <div class="input-group mb-3">
                    <label for="startDaysDiapason" class="input-group-text"><fmt:message key="index.page.form.label.filter.duration.start" /></label>
                    <input name="startDaysDiapason" type="number" min="1" max="100" id="startDaysDiapason" class="form-control"
                        ${not empty sessionScope.get("startDaysDiapason") ? "value=\"".concat(sessionScope.get("startDaysDiapason")).concat("\"") : ""}>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="input-group mb-3">
                    <label for="endDaysDiapason" class="input-group-text"><fmt:message key="index.page.form.label.filter.duration.end" /></label>
                    <input name="endDaysDiapason" type="number" min="1" max="100" id="endDaysDiapason" class="form-control"
                        ${not empty sessionScope.get("endDaysDiapason") ? "value=\"".concat(sessionScope.get("endDaysDiapason")).concat("\"") : ""}>
                </div>
            </div>
            <button type="submit" class="btn btn-primary" title="filter"><fmt:message key="index.page.form.label.filter.button.submit" /></button>
        </form>

    <%--  ----------------------------------  Content section  ---------------------------------------------------  --%>
    <div class="row gy-4 mx-0 py-2">
        <c:forEach var="cruise" items="${cruises}">
            <div class="col-md-6">
                <div class="card">
                    <div class="card-body">
                        <my:cruise obj="${cruise}" /><br />
                        <c:if test="${empty cruise.boat.crew}">
                            <a class="btn btn-sm btn-outline-primary"
                               href="${pageContext.request.contextPath}/catalogue?boatId=${cruise.boat.id}${not empty param.page ? "&page=".concat(param.page) : ""}">
                                <fmt:message key="index.page.form.link.show.crew" />
                            </a>
                        </c:if>

                        <c:if test="${sessionScope.user.roles.contains(Role.USER) and cruise.getCruiseStatus() == 'REGISTRATION_IN_PROGRESS'}">
                            <a href="#" class="btn btn-sm btn-outline-primary card-link" type="button" data-bs-toggle="modal" data-bs-target="#confirmOrder-${cruise.id}">
                                <fmt:message key="index.page.form.link.make.order" />
                            </a>
                        </c:if>
                    </div>
                </div>
            </div>

            <!--  ---------------------------------  order-confirmation form block  -------------------------------- -->
            <div class="modal fade" id="confirmOrder-${cruise.id}" tabindex="-1" aria-labelledby="confirmOrderLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <form class="modal-content" action="<%=request.getContextPath() + PATH_TO_ORDER_PAGE%>" method="post">
                        <input name="user-id" value="${sessionScope.user.id}" hidden />
                        <input name="cruise-id" value="${cruise.id}" hidden />
                        <div class="modal-header">
                            <h5 class="modal-title" id="confirmOrderLabel"></h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <p class="form-label">
                                <fmt:message key="index.page.form.label.order.confirmation.seats.amount" />
                            </p>
                            <input name="seats" type="number" min="1" class="form-control" required />
                        </div>
                        <div class="modal-footer">
                            <span class="position-absolute start-0 ms-3">
                                <fmt:message key="index.page.form.label.order.confirmation.message" />
                            </span>
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                <fmt:message key="index.page.form.label.order.confirmation.button.close" />
                            </button>
                            <button type="submit" class="btn btn-primary">
                                <fmt:message key="index.page.form.label.order.confirmation.button.confirm" />
                            </button>
                        </div>
                    </form>
                </div>
            </div>

        </c:forEach>

        <%-- --------------------------------------  pagination section  ------------------------------------------ --%>
        <nav>
            <ul class="pagination pagination-lg m-3">
                <c:set var="pageNum" value="${not empty param.page ? param.page : 1}" />
                <c:forEach var="i" begin="${pageNum-1}" end="${pageNum+1}" step="1">
                    <c:if test="${i > 0}">
                        <li class="page-item ${i == pageNum ? "disabled" : ""}"
                            ${i > pageNum and i > requestScope.get("lastPageNum") ? "hidden" : ""}>
                            <a class="page-link"
                               href="<%=request.getContextPath() + PATH_TO_CATALOGUE%>?page=${i}">
                                    ${i}
                            </a>
                        </li>
                    </c:if>
                </c:forEach>
            </ul>
        </nav>

<%-- -----------------------------------------change language form----------------------------------------------------%>
        <form action="<%=request.getContextPath() + PATH_TO_CHANGE_LOCALE%>" method="get">
            <select name="locale" class="form-select" oninput="this.parentElement.submit()">
                <option value="en">English</option>
                <c:forEach items="${applicationScope.locales}" var="locale">
                    <c:set var="selected" value="${locale.key.equalsIgnoreCase(currentLocale) ? 'selected' : '' }"/>
                    <option value="${locale.key}" ${selected}>${locale.value}</option>
                </c:forEach>
            </select>
        </form>

    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM" crossorigin="anonymous"></script>
<script>
    //Selects correct option of sorting (if sorted)
    <c:if test='${sessionScope.get("sorted") != null}'>
        document.querySelector('select>option[value=${sessionScope.get("sorted")}]').toggleAttribute("selected");
    </c:if>
</script>
</body>
</html>

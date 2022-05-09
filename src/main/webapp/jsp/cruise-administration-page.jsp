<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="static ua.com.cruises.controller.uris.AppPath.*" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="my" uri="/myTags" %>
<html>
<head>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
    <title>Cruises administration</title>
</head>
<body>

<div class="container px-0 border border-top-0 border-warning" style="max-width:991px;">

    <%-- ------------------------------------  Topbar section  ---------------------------------------------------- --%>
    <jsp:include page="/jsp/inner/topbar.jsp" />

        <%-- --------------------------------------  add cruise form  --------------------------------------------- --%>
        <div class="row justify-content-end collapse py-3" id="addCruiseFormContainer">
            <div class="col pt-4 d-none d-md-block">
                <img src="${pageContext.request.contextPath}/static/images/boat-sharp.svg" alt="cruise-company-icon" />
            </div>
            <div class="col-md-8">
                <form action="${pageContext.request.contextPath}/cruise-adm" style="max-width: 700px;margin-left:auto;" method="post">
                    <div class="row">

                        <div class="col-12">
                            <div class="input-group my-3">
                                <label class="input-group-text" for="boatSelect">
                                    <fmt:message key="cruise.adm.page.form.add.cruise.select.boat.label" />
                                </label>
                                <select name="boat-id" class="form-select" id="boatSelect" required="required">
                                    <option value="" selected>
                                        <fmt:message key="cruise.adm.page.form.add.cruise.select.boat.default.option" />
                                    </option>
                                    <c:forEach items="${boats}" var="boat">
                                        <option value="${boat.id}">${boat.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <div class="col-sm-6">
                            <div class="input-group mb-3">
                                <label for="startDateInput" class="input-group-text">
                                    <fmt:message key="cruise.adm.page.form.add.cruise.input.start.date.label" />
                                </label>
                                <%  //Java code here (!!!bad practice!!!)
                                    //It provides current date in an appropriate format for date input option validation
                                    Date today = new Date();
                                    String convertedToday = new SimpleDateFormat("yyyy-MM-dd").format(today);
                                %>
                                <input name="cruise-start-date" type="date" id="startDateInput" class="form-control"
                                       min="<%=convertedToday%>" required oninput="{
                                           //JavaScript block here
                                           //Sets minimal available date for the next input every time this one is filled
                                            let startDateInputElem = document.getElementById('startDateInput');
                                            let endDateInputElem = document.getElementById('endDateInput');
                                            endDateInputElem.setAttribute('min', startDateInputElem.value);
                                       }" />
                            </div>
                        </div>
                        <div class="col-sm-6">
                            <div class="input-group mb-3">
                                <label for="endDateInput" class="input-group-text">
                                    <fmt:message key="cruise.adm.page.form.add.cruise.input.end.date.label" />
                                </label>
                                <input name="cruise-end-date" type="date" id="endDateInput" class="form-control" min="<%=convertedToday%>" required />
                            </div>
                        </div>

                        <div id="port-selects-container" class="col-12 mt-4">
                            <div class="d-grid gap-2 mb-3">
                                <%-- JavaScript adds two port - select tags here --%>
                                <button type="button" class="btn btn-sm btn-outline-primary" title="Add another port" onclick="addPortSelect()">+</button>
                            </div>
                        </div>

                        <div class="col-sm-6 align-self-center">
                            <label for="cruiseCost" class="form-label">
                                <fmt:message key="cruise.adm.page.form.add.cruise.cost.message" />
                            </label>
                        </div>
                        <div class="col-sm-6">
                            <div class="input-group mb-3">
                                <span class="input-group-text">$</span>
                                <input name="cruise-cost" min="100" step="100" id="cruiseCost" type="number" class="form-control" aria-label="Pay for a seat" required>
                                <span class="input-group-text">.00</span>
                            </div>
                        </div>
                    </div>
                    <div style="display: flex; flex-direction: row-reverse;">
                        <button
                                type="button"
                                class="btn btn-outline-secondary"
                                data-bs-toggle="collapse"
                                data-bs-target="#addCruiseFormContainer"
                                aria-expanded="true"
                                aria-controls="addCruiseFormContainer"
                                onclick="document.getElementById('showBtnForAddCruiseForm').toggleAttribute('hidden')">
                            <fmt:message key="cruise.adm.page.form.add.cruise.button.close" />
                        </button>
                        <button type="submit" class="btn btn-primary me-1" title="Add another port">
                            <fmt:message key="cruise.adm.page.form.add.cruise.button.submit" />
                        </button>
                    </div>

                </form>

            </div>
        </div>

        <div class="row">
            <div class="col">
                <div class="container">
                    <button
                            type="button"
                            id="showBtnForAddCruiseForm"
                            class="btn btn-outline-primary my-3"
                            data-bs-toggle="collapse"
                            data-bs-target="#addCruiseFormContainer"
                            aria-expanded="false"
                            aria-controls="addCruiseFormContainer"
                            onclick="this.toggleAttribute('hidden');">
                        <fmt:message key="cruise.adm.page.button.add.cruise" />
                    </button>
                </div>
            </div>
            <%-- ------------------------------  sorting cruises form  -------------------------------------------- --%>
            <form action="<%=request.getContextPath() + PATH_TO_CRUISE_ADMINISTRATION_PAGE%>" method="GET"
                  class="col align-self-center input-group m-0">
                <label for="sort-by" class="input-group-text"><fmt:message key="index.page.form.label.sorted" /></label>
                <select name="sorted" id="sort-by" class="form-control" oninput="this.parentElement.submit()">
                    <option><fmt:message key="index.page.form.label.sorted.option.default" /></option>
                    <option value="BY_BOOKED_SEATS"><fmt:message key="index.page.form.label.sorted.option.by.seats" /></option>
                    <option value="BY_CRUISE_COST"><fmt:message key="index.page.form.label.sorted.option.by.cost" /></option>
                    <option value="BY_CRUISE_DURATION"><fmt:message key="index.page.form.label.sorted.option.by.duration" /></option>
                </select>
            </form>
        </div>

        <%-- --------------------------------------  current list of cruises  ------------------------------------- --%>
        <ul class="list-group" style="margin-left: 50px;margin-right: 50px;">
            <c:forEach var="cruise" items="${cruises}">
                <li class="row py-3 list-group-item list-group-item-action d-flex align-items-start">
                    <p class="col-sm-2">
                        <fmt:message key="cruise.adm.page.content.booked" />:
                        <span class="badge bg-primary rounded-pill">${cruise.getBookedSeats()}</span>
                    </p>
                    <p class="col"><my:cruise obj="${cruise}" /></p>
                    <p class="col-md-2 align-self-end">
                        <button type="button" class="btn btn-outline-danger"
                                onclick="fetch('${pageContext.request.contextPath}/cruise-adm/id${cruise.getId()}',{method: 'DELETE'});
                                         setTimeout(function(){document.location.reload()}, 1000)" class="btn btn-sm btn-outline-danger">
                            <fmt:message key="cruise.adm.page.content.button.remove" />
                        </button>
                    </p>
                </li>
            </c:forEach>
        </ul>

        <%-- --------------------------------------  pagination section  ------------------------------------------ --%>
        <%--        Bad practice was used here. Good practice is shown at index.jsp   --%>
        <%--        This variant is an experience example.   --%>
        <nav>
            <ul class="pagination pagination-lg m-3">
                <%
                    int pageNum = request.getParameter("page") != null ?
                            Integer.parseInt(request.getParameter("page")) : 1;
                    for (int i = pageNum-1; i<=pageNum+1; i++) {
                        if (i <= 0) continue;
                %>
                <li class="page-item <%= i==pageNum ? "disabled" : ""%>"
                        <%= i > pageNum && i > (int) request.getAttribute("lastPageNum") ? "hidden" : "" %>>
                    <a class="page-link"
                       href="<%=request.getContextPath() + PATH_TO_CRUISE_ADMINISTRATION_PAGE%>?page=<%=i%>">
                        <%=i%>
                    </a>
                </li>
                <%}%>
            </ul>
        </nav>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM" crossorigin="anonymous"></script>
<script>
    //Selects correct option of sorting (if sorted)
    <c:if test='${sessionScope.get("sorted") != null}'>
        document.querySelector('select>option[value=${sessionScope.get("sorted")}]').toggleAttribute("selected");
    </c:if>

    /*
    * JavaScript function
    * */
    function addPortSelect() {
        //gets existing container of port-selectors
        let container = document.getElementById("port-selects-container");

        //creates container for new one port-selector
        let element1 = document.createElement("div");
        element1.setAttribute("class", "input-group mb-3");

        //creates a label for a new port-select element
        let child1 = document.createElement("label");
        child1.setAttribute("class", "input-group-text");
        child1.setAttribute("for", "portSelect" + container.childElementCount);
        // child1.innerText = "Port";
        child1.innerText = "<fmt:message key="cruise.adm.page.form.add.cruise.select.port.label" />";

        //creates a new port-select element
        let child2 = document.createElement("select");
        child2.setAttribute("name", "port-id");
        child2.setAttribute("id", "portSelect" + container.childElementCount);
        child2.setAttribute("class", "form-select");
        child2.setAttribute("required", "required");

        //creates the first option for the new port-select element
        let tempPortOption;
        tempPortOption = document.createElement("option");
        tempPortOption.setAttribute("selected", "selected");
        tempPortOption.setAttribute("value", "");
        // tempPortOption.innerText = "Choose a port";
        tempPortOption.innerText = "<fmt:message key="cruise.adm.page.form.add.cruise.select.port.default.option" />";
        child2.appendChild(tempPortOption);

        //creates an option for each existing in DB port
        <c:forEach var="port" items="${ports}">
            tempPortOption = document.createElement("option");
            tempPortOption.setAttribute("value", "${port.getId()}");
            tempPortOption.innerText = "${port.getCountry()} ${port.getCity()}";
            child2.appendChild(tempPortOption);
        </c:forEach>

        //creates a button to dynamically remove added select
        let child3 = document.createElement("button");
        child3.setAttribute("class", "btn btn-outline-primary");
        child3.setAttribute("type", "button");
        child3.setAttribute("onclick",
            "document.getElementById(\"port-selects-container\").removeChild(document.getElementById(\"portSelect"
            + container.childElementCount +
            "\").parentNode)"
        );
        child3.innerText = "−";

        //group all together
        element1.appendChild(child1);
        element1.appendChild(child2);
        element1.appendChild(child3);

        container.insertBefore(element1, container.lastElementChild);
    }

    //Add two start port select elements
    addPortSelect();
    addPortSelect();
</script>
</body>
</html>

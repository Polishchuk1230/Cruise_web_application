<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="my" uri="/myTags" %>

<html>
<head>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
    <title>Crew admin page</title>
</head>
<body>
<div class="container px-0 border border-top-0 border-warning" style="max-width:991px;">

    <%-- ------------------------------------  Topbar section  ---------------------------------------------------- --%>
    <jsp:include page="/jsp/inner/topbar.jsp" />

        <%--  ------------------------------------  Content block  -----------------------------------------------  --%>
        <div class="row mt-2">
            <div class="col-1"></div>
            <%--      a Crew of the Boat      --%>
            <div class="col-5">
                <c:choose>
                    <%-- if param boat-id was not defined, instead of a boat will be provided collection of boats
                         to choose one of them --%>
                    <c:when test="${empty boat}">
                        <form class="input-group" action="${pageContext.request.contextPath}/crew-adm" method="get">
                            <label class="input-group-text" for="select-boat">
                                <fmt:message key="crew.adm.page.form.liner.select.label" />:
                            </label>
                            <select class="form-select" id="select-boat" name="boat-id" oninput="this.parentElement.submit()" required>
                                <option value="" selected><fmt:message key="crew.adm.page.form.liner.select.default.option" /></option>
                                <c:forEach items="${boats}" var="boat">
                                    <option value="${boat.id}">${boat.name}</option>
                                </c:forEach>
                            </select>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <%-- if boat was provided --%>
                        ${boat.name}
                        <div id="cadre-input-labels-container">
                            <c:forEach items="${boat.crew.cadres}" var="cadre">
                                <p id="label-block-${cadre.id}" class="input-group">
                                    <label class="input-group-text">${cadre.name} ${cadre.surname}</label>
                                    <button class="btn btn-outline-danger"
                                            onclick="removeFromCrew(${cadre.id}, '${cadre.name}', '${cadre.surname}', '${cadre.position}')">
                                        -
                                    </button>
                                </p>
                            </c:forEach>
                        </div>
                        <form action="${pageContext.request.contextPath}/crew-adm" method="post" id="collect-crew-form">
                            <input name="boat-id" value="${boat.id}" hidden>
                            <c:forEach items="${boat.crew.cadres}" var="cadre">
                                <input name="cadre-id" id="input-${cadre.id}" value="${cadre.id}" type="number" hidden />
                            </c:forEach>
                            <button type="submit" class="btn btn-primary">
                                <fmt:message key="crew.adm.page.form.button.submit" />
                            </button>
                        </form>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="col-1"></div>
            <div id="free-cadres-container" class="col-4">
                <%--     FORM for new Cadre     --%>
                <jsp:include page="inner/notifications.jsp" />
<%--                <c:if test="${not empty param.error}">--%>
<%--                    <div class="alert alert-warning" role="alert">--%>
<%--                        <fmt:message key="${param.error}" />--%>
<%--                    </div>--%>
<%--                </c:if>--%>
                <button type="button" class="btn btn-outline-primary mb-2" data-bs-toggle="collapse" data-bs-target="#newCadreForm" aria-expanded="false" aria-controls="newCadreForm">
                    <fmt:message key="crew.adm.page.button.show.form" />
                </button>
                <form class="form-control collapse mb-2" id="newCadreForm" action="${pageContext.request.contextPath}/cadre-adm" method="post">
                    <label for="newCadreName" class="input-group-text">
                        <fmt:message key="crew.adm.page.form.cadre.name.label" />:
                    </label>
                    <input class="form-control" id="newCadreName" name="name" type="text" maxlength="20" required/>
                    <label for="newCadreSurname" class="input-group-text mt-2">
                        <fmt:message key="crew.adm.page.form.cadre.surname.label" />:
                    </label>
                    <input class="form-control" id="newCadreSurname" name="surname" type="text" maxlength="20" required/>
                    <label for="newCadrePosition" class="input-group-text mt-2">
                        <fmt:message key="crew.adm.page.form.cadre.position.label" />:
                    </label>
                    <input class="form-control" id="newCadrePosition" name="position" type="text" maxlength="15" required/>
                    <label for="newCadreCharacteristic" class="input-group-text mt-2">
                        <fmt:message key="crew.adm.page.form.cadre.characteristic.label" />:
                    </label>
                    <textarea class="form-control" id="newCadreCharacteristic" name="characteristic" maxlength="400"></textarea>
                    <button type="submit" class="btn btn-primary">
                        <fmt:message key="crew.adm.page.form.cadre.button.submit" />
                    </button>
                </form>

                <%--      free cadres      --%>
                <c:forEach items="${freeCadres}" var="cadre">
                    <p id="free-cadre-block-${cadre.id}">
                        <button class="btn btn-outline-primary" type="button"
                                onclick="addToCrew(${cadre.id}, '${cadre.name}', '${cadre.surname}', '${cadre.position}')"
                                ${empty boat ? "disabled" : ""}>
                            +
                        </button>
                        ${cadre.name} ${cadre.surname} ${cadre.position}<br />
                    </p>
                </c:forEach>
            </div>
            <div class="col-1"></div>
        </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM" crossorigin="anonymous"></script>
<script>
    function addToCrew(cadreId, cadreName, cadreSurname, cadrePosition) {
        let container = document.getElementById("collect-crew-form");
        let labelContainer = document.getElementById("cadre-input-labels-container");

        let element1 = document.createElement("input");
        element1.setAttribute("name", "cadre-id");
        element1.setAttribute("id", "input-" + cadreId);
        element1.setAttribute("value", cadreId);
        element1.setAttribute("type", "number")
        element1.setAttribute("hidden", "hidden")

        let newInputContainer = document.createElement("p");
        newInputContainer.setAttribute("id", "label-block-" + cadreId);
        newInputContainer.setAttribute("class", "input-group");

        let newLabel = document.createElement("label");
        newLabel.setAttribute("class", "input-group-text");
        newLabel.innerText = cadreName + " " + cadreSurname;
        newInputContainer.appendChild(newLabel);

        let newButton = document.createElement("button");
        newButton.setAttribute("class", "btn btn-outline-danger");
        newButton.setAttribute("onclick", "removeFromCrew(" + cadreId + ", '" + cadreName + "', '" + cadreSurname + "', '" + cadrePosition + "')");
        newButton.innerText = "-";
        newInputContainer.appendChild(newButton);

        container.appendChild(element1);
        labelContainer.appendChild(newInputContainer);

        document.getElementById("free-cadre-block-" + cadreId).remove();
    }

    function removeFromCrew(cadreId, cadreName, cadreSurname, cadrePosition) {
        document.getElementById("label-block-" + cadreId).remove();
        document.getElementById("input-" + cadreId).remove();

        let freeCadreBlock = document.createElement("p");
        freeCadreBlock.setAttribute("id", "free-cadre-block-" + cadreId);

        let newButton = document.createElement("button");
        newButton.setAttribute("class", "btn btn-outline-primary");
        newButton.setAttribute("type", "button");
        newButton.setAttribute("onclick", "addToCrew(" + cadreId + ", '" + cadreName + "', '" + cadreSurname + "', '" + cadrePosition + "')");
        newButton.innerText = "+";
        freeCadreBlock.appendChild(newButton);

        freeCadreBlock.appendChild(document.createTextNode(" " + cadreName + " " + cadreSurname + " " + cadrePosition));
        freeCadreBlock.appendChild(document.createElement("br"));

        document.getElementById("free-cadres-container").appendChild(freeCadreBlock);
    }
</script>
</body>
</html>

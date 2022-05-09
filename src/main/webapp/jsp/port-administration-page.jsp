<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
    <title>Ports administration</title>
</head>
<body>
<div class="container-fluid px-0 pb-5" style="max-width:991px;">

    <%-- ------------------------------------  Topbar section  ---------------------------------------------------- --%>
    <jsp:include page="/jsp/inner/topbar.jsp" />

        <form action="${pageContext.request.contextPath}/port-adm" method="post">
            <input name="purpose" type="text" value="add-port" hidden>
            <p class="input-group">
                <label class="input-group-text" for="portCountryInput">
                    <fmt:message key="port.adm.page.form.port.country.label" />
                </label>
                <input name="port-country" type="text" id="portCountryInput" class="form-control" pattern="^[a-zA-Zа-яА-ЯїЇ ]{2,}$" required/>
            </p>

            <p class="input-group">
                <label class="input-group-text" for="portCountryInput">
                    <fmt:message key="port.adm.page.form.port.city.label" />
                </label>
                <input name="port-city" type="text" id="portCityInput" class="form-control" pattern="^[a-zA-Zа-яА-ЯїЇ ]{2,}$" required/>
            </p>

            <p style="display: flex; flex-direction: row-reverse;">
                <button type="submit" class="btn btn-primary">
                    <fmt:message key="port.adm.page.form.port.button.submit" />
                </button>
            </p>
        </form>


    <c:forEach var="port" items="${ports}">
        ${port.toString()}
        <button class="btn btn-outline-secondary" type="button" data-bs-toggle="collapse" aria-expanded="false"
                data-bs-target="#portEditForm${port.getId()}" aria-controls="portEditForm${port.getId()}">
            <fmt:message key="port.adm.page.content.button.edit" />
        </button>
        <form action="${pageContext.request.contextPath}/port-adm" method="post" style="display: inline-block">
            <input name="purpose" type="text" value="delete-port" hidden>
            <input name="port-id" type="number" value="${port.getId()}" hidden>
            <button type="submit" class="btn btn-outline-danger">
                <fmt:message key="port.adm.page.content.button.remove" />
            </button>
        </form>
        <br />

        <form action="${pageContext.request.contextPath}/port-adm" method="post" id="portEditForm${port.getId()}" class="collapse">
            <input name="purpose" type="text" value="edit-port" hidden>
            <input name="port-id" type="number" value="${port.getId()}" hidden>
            <p class="input-group">
                <label class="input-group-text"><fmt:message key="port.adm.page.form.port.country.label" /></label>
                <input name="port-country" type="text" class="form-control" value="${port.getCountry()}" pattern="^[a-zA-Zа-яА-ЯїЇ ]{2,}$" required/>
            </p>

            <p class="input-group">
                <label class="input-group-text"><fmt:message key="port.adm.page.form.port.city.label" /></label>
                <input name="port-city" type="text" class="form-control" value="${port.getCity()}" pattern="^[a-zA-Zа-яА-ЯїЇ ]{2,}$" required/>
            </p>

            <p style="display: flex; flex-direction: row-reverse;">
                <button type="submit" class="btn btn-outline-primary">
                    <fmt:message key="port.adm.page.content.form.submit" />
                </button>
            </p>
        </form>
        <br />
    </c:forEach>

</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM" crossorigin="anonymous"></script>
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="my" uri="/myTags" %>
<html>
<head>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
    <title>Liners administration</title>
</head>
<body>

<div class="container px-0 border border-top-0 border-warning" style="max-width:991px;">

    <%-- ------------------------------------  Topbar section  ---------------------------------------------------- --%>
    <jsp:include page="/jsp/inner/topbar.jsp" />

    <%-- ------------------------------------  New Liner form section  -------------------------------------------- --%>
    <div class="container">
        <h2><fmt:message key="boat.adm.page.form.add.liner.label" /></h2>
        <form action="${pageContext.request.contextPath}/boat-adm" method="post">
            <p class="input-group">
                <label for="boat-name-input" class="input-group-text">
                    <fmt:message key="boat.adm.page.form.input.name.label" />
                </label>
                <input name="boat-name" type="text" id="boat-name-input" class="form-control" pattern="^[a-zA-Zа-яА-ЯїЇ -']{3,}$" placeholder="[a-zA-Zа-яА-ЯїЇ -']{3,}" />
            </p>
            <p class="input-group">
                <label for="boat-capacity-input" class="input-group-text">
                    <fmt:message key="boat.adm.page.form.input.capacity.label" />
                </label>
                <input name="boat-capacity" type="number" id="boat-capacity-input" class="form-control" step="10" min="100" />
            </p>
            <p style="display: flex; flex-direction: row-reverse">
                <button class="btn btn-primary"><fmt:message key="boat.adm.page.form.button.submit" /></button>
            </p>
        </form>
    </div>

    <%-- ------------------------------------  Content section  --------------------------------------------------- --%>
    <ul class="list-group">
        <c:forEach var="boat" items="${boats}">
            <li class="list-group-item"><my:boat obj="${boat}" /></li>
            <button class="btn btn-sm btn-outline-danger" type="button" class="btn btn-outline-danger"
                    onclick="fetch('${pageContext.request.contextPath}/boat-adm/id${boat.getId()}',{method: 'DELETE'});
                             setTimeout(() => document.location.reload(), 1000)">
                <fmt:message key="boat.adm.page.content.button.remove" />
            </button>
        </c:forEach>
    </ul>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM" crossorigin="anonymous"></script>
</body>
</html>

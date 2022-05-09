<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${not empty param.error or not empty param.success}">
    <div class="row justify-content-center align-items-center">
        <p class="col-1"></p>
        <p class="col alert alert-${empty param.error ? 'success' : 'warning'}" role="alert" onclick="this.parentElement.remove()">
            <c:choose>
                <c:when test="${param.error eq 'err1'}">
                    <fmt:message key="crew.adm.page.form.cadre.error.message" />
                </c:when>
                <c:when test="${param.error eq 'err2'}">
                    <fmt:message key="registration.page.form.incorrect.username.warn" />
                </c:when>
                <c:when test="${param.error eq 'err3'}">
                    <fmt:message key="registration.page.form.incorrect.password.warn" />
                </c:when>
                <c:when test="${param.error eq 'err4'}">
                    <fmt:message key="registration.page.form.incorrect.phone.warn" />
                </c:when>

                <c:when test="${param.success eq 'success1'}">
                    <fmt:message key="registration.page.form.success.registration" />
                </c:when>

                <c:otherwise>
                    ${param.error}
                </c:otherwise>
            </c:choose>
        </p>
        <p class="col-1"></p>
    </div>
</c:if>
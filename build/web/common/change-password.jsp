<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Change Password" />
</jsp:include>

<div class="data-card" style="max-width: 500px; margin: 0 auto;">
    <div class="card-header">
        <h5><i class="bi bi-shield-lock me-2"></i>Change Password</h5>
    </div>
    <div class="card-body">
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>
        <c:if test="${not empty success}">
            <div class="alert alert-success">${success}</div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/change-password">
            <div class="mb-3">
                <label for="oldPassword" class="form-label">Current Password</label>
                <input type="password" class="form-control" id="oldPassword" name="oldPassword"
                       placeholder="Enter current password" required>
            </div>
            <div class="mb-3">
                <label for="newPassword" class="form-label">New Password</label>
                <input type="password" class="form-control" id="newPassword" name="newPassword"
                       placeholder="At least 6 characters" minlength="6" required>
            </div>
            <div class="mb-3">
                <label for="confirmPassword" class="form-label">Confirm New Password</label>
                <input type="password" class="form-control" id="confirmPassword" name="confirmPassword"
                       placeholder="Re-enter new password" minlength="6" required>
            </div>
            <button type="submit" class="btn btn-gradient w-100">
                <i class="bi bi-check-lg me-1"></i> Update Password
            </button>
        </form>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reset Password - Assignment Manager</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css?v=2.0" rel="stylesheet">
</head>
<body>
<div class="auth-page">
    <div class="auth-card">
        <div class="logo">
            <h2><i class="bi bi-mortarboard-fill"></i> AssignmentMS</h2>
            <p>Set New Password</p>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/reset-password">
            <input type="hidden" name="token" value="${token}">
            <div class="mb-3">
                <label for="newPassword" class="form-label">New Password</label>
                <div class="input-group">
                    <span class="input-group-text"><i class="bi bi-lock"></i></span>
                    <input type="password" class="form-control" id="newPassword" name="newPassword"
                           placeholder="At least 6 characters" minlength="6" required>
                </div>
            </div>
            <div class="mb-3">
                <label for="confirmPassword" class="form-label">Confirm New Password</label>
                <div class="input-group">
                    <span class="input-group-text"><i class="bi bi-lock-fill"></i></span>
                    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword"
                           placeholder="Re-enter new password" minlength="6" required>
                </div>
            </div>
            <button type="submit" class="btn btn-primary w-100 mt-2">
                <i class="bi bi-check-lg me-1"></i> Reset Password
            </button>
        </form>

        <div class="text-center mt-3">
            <a href="${pageContext.request.contextPath}/login" class="fw-semibold" style="color: var(--primary);">
                <i class="bi bi-arrow-left me-1"></i> Back to Login
            </a>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

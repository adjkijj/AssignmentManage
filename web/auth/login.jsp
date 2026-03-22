<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Assignment Manager</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css?v=2.0" rel="stylesheet">
</head>
<body>
<div class="auth-page">
    <div class="auth-card">
        <div class="logo">
            <h2><i class="bi bi-mortarboard-fill"></i> AssignmentMS</h2>
            <p>Assignment Management System</p>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>
        <c:if test="${not empty success}">
            <div class="alert alert-success">${success}</div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/login">
            <div class="mb-3">
                <label for="username" class="form-label">Username</label>
                <div class="input-group">
                    <span class="input-group-text"><i class="bi bi-person"></i></span>
                    <input type="text" class="form-control" id="username" name="username"
                           value="${username}" placeholder="Enter username" required>
                </div>
            </div>
            <div class="mb-3">
                <label for="password" class="form-label">Password</label>
                <div class="input-group">
                    <span class="input-group-text"><i class="bi bi-lock"></i></span>
                    <input type="password" class="form-control" id="password" name="password"
                           placeholder="Enter password" required>
                </div>
            </div>
            <button type="submit" class="btn btn-primary w-100 mt-2">
                <i class="bi bi-box-arrow-in-right"></i> Sign In
            </button>
        </form>

        <div class="text-center mt-2">
            <a href="${pageContext.request.contextPath}/forgot-password" class="text-muted">
                <i class="bi bi-question-circle me-1"></i>Forgot password?
            </a>
        </div>

        <div class="text-center mt-3">
            <span class="text-muted">Don't have an account?</span>
            <a href="${pageContext.request.contextPath}/register" class="fw-semibold" style="color: var(--primary);">
                Register here
            </a>
        </div>

        <hr class="my-3">
        <div class="text-center">
            <small class="text-muted">
                Demo: admin/admin123 | instructor1/instructor123 | student1/student123
            </small>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

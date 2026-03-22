<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Forgot Password - Assignment Manager</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css?v=2.0" rel="stylesheet">
</head>
<body>
<div class="auth-page">
    <div class="auth-card">
        <div class="logo">
            <h2><i class="bi bi-mortarboard-fill"></i> AssignmentMS</h2>
            <p>Password Recovery</p>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>
        <c:if test="${not empty success}">
            <div class="alert alert-success">${success}</div>
        </c:if>

        <c:if test="${not empty resetLink}">
            <div class="alert alert-info">
                <strong>Reset Link (demo mode):</strong><br>
                <a href="${resetLink}" class="text-break">${resetLink}</a>
            </div>
        </c:if>

        <c:if test="${empty resetLink}">
            <form method="post" action="${pageContext.request.contextPath}/forgot-password">
                <div class="mb-3">
                    <label for="email" class="form-label">Email Address</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="bi bi-envelope"></i></span>
                        <input type="email" class="form-control" id="email" name="email"
                               placeholder="Enter your registered email" required>
                    </div>
                </div>
                <button type="submit" class="btn btn-primary w-100 mt-2">
                    <i class="bi bi-send me-1"></i> Send Reset Link
                </button>
            </form>
        </c:if>

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

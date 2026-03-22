<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Import Users (CSV)" />
</jsp:include>

<div class="data-card">
    <div class="card-header">
        <h5><i class="bi bi-file-earmark-arrow-up me-2"></i>Bulk Import Users from CSV</h5>
        <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-sm btn-outline-primary">
            <i class="bi bi-arrow-left me-1"></i> Back to Users
        </a>
    </div>
    <div class="card-body">
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>
        <c:if test="${not empty success}">
            <div class="alert alert-success">${success}</div>
        </c:if>
        <c:if test="${not empty parseErrors}">
            <div class="alert alert-warning">
                <strong>Parse Warnings:</strong>
                <ul class="mb-0 mt-1">
                    <c:forEach var="pe" items="${parseErrors}">
                        <li>${pe}</li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>

        <div class="mb-4">
            <h6>CSV Format</h6>
            <p class="text-muted">Your CSV file should have the following columns (one user per line):</p>
            <div class="p-3" style="background: var(--body-bg); border-radius: 8px; font-family: monospace;">
                username,password,fullName,email,role<br>
                john_doe,pass123,John Doe,john@example.com,student<br>
                jane_doe,pass456,Jane Doe,jane@example.com,instructor
            </div>
            <small class="text-muted mt-2 d-block">
                The first row is automatically skipped if it contains the header "username".
                Valid roles: <code>student</code>, <code>instructor</code>, <code>admin</code>
            </small>
        </div>

        <form method="post" action="${pageContext.request.contextPath}/admin/import-users" enctype="multipart/form-data">
            <div class="mb-3">
                <label for="csvFile" class="form-label">Select CSV File *</label>
                <input type="file" class="form-control" id="csvFile" name="csvFile" accept=".csv" required>
                <div class="form-text">Maximum file size: 10MB</div>
            </div>

            <button type="submit" class="btn btn-gradient">
                <i class="bi bi-cloud-arrow-up me-1"></i> Import Users
            </button>
            <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-outline-primary ms-2">
                <i class="bi bi-x-lg me-1"></i> Cancel
            </a>
        </form>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />

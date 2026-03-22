<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="ct" uri="http://assignmentmanage/customtags"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Manage Subjects" />
</jsp:include>

<c:if test="${not empty sessionScope.success}">
    <div class="alert alert-success">${sessionScope.success}</div>
    <c:remove var="success" scope="session" />
</c:if>
<c:if test="${not empty sessionScope.error}">
    <div class="alert alert-danger">${sessionScope.error}</div>
    <c:remove var="error" scope="session" />
</c:if>

<!-- Create Subject -->
<div class="data-card mb-4">
    <div class="card-header"><h5><i class="bi bi-plus-circle me-2"></i>Add New Subject</h5></div>
    <div class="card-body">
        <form method="post" action="${pageContext.request.contextPath}/admin/subjects">
            <input type="hidden" name="action" value="create">
            <div class="row g-3">
                <div class="col-md-3">
                    <label class="form-label">Subject Code</label>
                    <input type="text" class="form-control" name="subjectCode" placeholder="e.g., PRJ301" required>
                </div>
                <div class="col-md-4">
                    <label class="form-label">Subject Name</label>
                    <input type="text" class="form-control" name="subjectName" placeholder="e.g., Web Development" required>
                </div>
                <div class="col-md-3">
                    <label class="form-label">Description</label>
                    <input type="text" class="form-control" name="description" placeholder="Brief description">
                </div>
                <div class="col-md-2 d-flex align-items-end">
                    <button type="submit" class="btn btn-gradient w-100">
                        <i class="bi bi-plus-circle me-1"></i> Create
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>

<!-- Subjects Table -->
<div class="data-card">
    <div class="card-header"><h5><i class="bi bi-book me-2"></i>All Subjects</h5></div>
    <div class="card-body">
        <c:if test="${empty subjects}">
            <div class="empty-state"><i class="bi bi-book"></i><h5>No Subjects</h5></div>
        </c:if>
        <c:if test="${not empty subjects}">
        <div class="table-responsive">
            <table class="table">
                <thead><tr><th>Code</th><th>Name</th><th>Description</th><th>Created</th><th>Actions</th></tr></thead>
                <tbody>
                    <c:forEach var="s" items="${subjects}">
                        <tr>
                            <td><span class="badge bg-primary">${s.subjectCode}</span></td>
                            <td><strong>${s.subjectName}</strong></td>
                            <td class="text-muted">${s.description}</td>
                            <td><ct:dateFormat date="${s.createdAt}" /></td>
                            <td>
                                <form method="post" action="${pageContext.request.contextPath}/admin/subjects" style="display:inline;" onsubmit="return confirm('Are you sure you want to permanently delete this subject?');">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="subjectId" value="${s.subjectId}">
                                    <button type="submit" class="btn btn-sm btn-outline-danger">
                                        <i class="bi bi-trash"></i> Delete
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
        </c:if>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />

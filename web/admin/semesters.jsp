<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="ct" uri="http://assignmentmanage/customtags"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Manage Semesters" />
</jsp:include>

<c:if test="${not empty sessionScope.success}">
    <div class="alert alert-success">${sessionScope.success}</div>
    <c:remove var="success" scope="session" />
</c:if>
<c:if test="${not empty sessionScope.error}">
    <div class="alert alert-danger">${sessionScope.error}</div>
    <c:remove var="error" scope="session" />
</c:if>

<!-- Create Semester -->
<div class="data-card mb-4">
    <div class="card-header"><h5><i class="bi bi-plus-circle me-2"></i>Add New Semester</h5></div>
    <div class="card-body">
        <form method="post" action="${pageContext.request.contextPath}/admin/semesters">
            <input type="hidden" name="action" value="create">
            <div class="row g-3">
                <div class="col-md-4">
                    <label class="form-label">Semester Name</label>
                    <input type="text" class="form-control" name="semesterName" placeholder="e.g., Spring 2026" required>
                </div>
                <div class="col-md-3">
                    <label class="form-label">Start Date</label>
                    <input type="date" class="form-control" name="startDate" required>
                </div>
                <div class="col-md-3">
                    <label class="form-label">End Date</label>
                    <input type="date" class="form-control" name="endDate" required>
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

<!-- Semesters Table -->
<div class="data-card">
    <div class="card-header"><h5><i class="bi bi-calendar3 me-2"></i>All Semesters</h5></div>
    <div class="card-body">
        <c:if test="${empty semesters}">
            <div class="empty-state"><i class="bi bi-calendar-x"></i><h5>No Semesters</h5></div>
        </c:if>
        <c:if test="${not empty semesters}">
        <div class="table-responsive">
            <table class="table">
                <thead><tr><th>Name</th><th>Start</th><th>End</th><th>Status</th><th>Actions</th></tr></thead>
                <tbody>
                    <c:forEach var="s" items="${semesters}">
                        <tr>
                            <td><strong>${s.semesterName}</strong></td>
                            <td><ct:dateFormat date="${s.startDate}" pattern="dd/MM/yyyy" /></td>
                            <td><ct:dateFormat date="${s.endDate}" pattern="dd/MM/yyyy" /></td>
                            <td>
                                <span class="badge ${s.isActive ? 'bg-success' : 'bg-secondary'}">
                                    ${s.isActive ? 'Active' : 'Inactive'}
                                </span>
                            </td>
                            <td>
                                <form method="post" action="${pageContext.request.contextPath}/admin/semesters" style="display:inline;">
                                    <input type="hidden" name="action" value="toggle">
                                    <input type="hidden" name="semesterId" value="${s.semesterId}">
                                    <button type="submit" class="btn btn-sm btn-outline-secondary">
                                        <i class="bi bi-toggle-on"></i> Toggle
                                    </button>
                                </form>
                                <form method="post" action="${pageContext.request.contextPath}/admin/semesters" style="display:inline;" onsubmit="return confirm('Are you sure you want to permanently delete this semester?');">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="semesterId" value="${s.semesterId}">
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

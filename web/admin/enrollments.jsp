<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="ct" uri="http://assignmentmanage/customtags"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Manage Enrollments" />
</jsp:include>

<c:if test="${not empty error}">
    <div class="alert alert-danger">${error}</div>
</c:if>
<c:if test="${not empty success}">
    <div class="alert alert-success">${success}</div>
</c:if>

<!-- Enroll Student Form -->
<div class="data-card mb-4">
    <div class="card-header">
        <h5><i class="bi bi-person-plus me-2"></i>Enroll Student</h5>
    </div>
    <div class="card-body">
        <form method="post" action="${pageContext.request.contextPath}/admin/enrollments">
            <input type="hidden" name="action" value="enroll">
            <div class="row g-3">
                <div class="col-md-4">
                    <label for="studentId" class="form-label">Student *</label>
                    <select class="form-select" id="studentId" name="studentId" required>
                        <option value="">Select Student</option>
                        <c:forEach var="student" items="${students}">
                            <option value="${student.userId}">
                                ${student.fullName} (${student.username})
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-4">
                    <label for="courseId" class="form-label">Course *</label>
                    <select class="form-select" id="courseId" name="courseId" required>
                        <option value="">Select Course</option>
                        <c:forEach var="course" items="${courses}">
                            <option value="${course.courseId}">
                                ${course.courseCode} - ${course.courseName}
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-4 d-flex align-items-end">
                    <button type="submit" class="btn btn-gradient w-100">
                        <i class="bi bi-person-check me-1"></i> Enroll
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>

<!-- Enroll All Students Form -->
<div class="data-card mb-4">
    <div class="card-header">
        <h5><i class="bi bi-people-fill me-2"></i>Enroll All Students</h5>
    </div>
    <div class="card-body">
        <form method="post" action="${pageContext.request.contextPath}/admin/enrollments"
              onsubmit="return confirm('Are you sure you want to enroll ALL students in the selected course?');">
            <input type="hidden" name="action" value="enrollAll">
            <div class="row g-3">
                <div class="col-md-8">
                    <label for="courseIdAll" class="form-label">Course *</label>
                    <select class="form-select" id="courseIdAll" name="courseId" required>
                        <option value="">Select Course</option>
                        <c:forEach var="course" items="${courses}">
                            <option value="${course.courseId}">
                                ${course.courseCode} - ${course.courseName}
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-4 d-flex align-items-end">
                    <button type="submit" class="btn btn-success w-100">
                        <i class="bi bi-people-fill me-1"></i> Enroll All Students
                    </button>
                </div>
            </div>
            <small class="text-muted mt-2 d-block">This will enroll all active students who are not already enrolled in the selected course.</small>
        </form>
    </div>
</div>

<!-- Filter Bar -->
<div class="data-card mb-4 mt-2">
    <div class="card-body">
        <form action="${pageContext.request.contextPath}/admin/enrollments" method="get" class="row g-3">
            <div class="col-md-5">
                <label class="form-label">Tìm kiếm sinh viên</label>
                <input type="text" name="keyword" class="form-control" placeholder="Tên, username, email..." value="${keyword}">
            </div>
            <div class="col-md-4">
                <label class="form-label">Khóa học</label>
                <select name="courseFilter" class="form-select">
                    <option value="">Tất cả khóa học</option>
                    <c:forEach var="c" items="${courses}">
                        <option value="${c.courseId}" ${courseFilter == c.courseId ? 'selected' : ''}>
                            ${c.courseCode} - ${c.courseName}
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-md-3 d-flex align-items-end gap-2">
                <button type="submit" class="btn btn-primary w-100"><i class="bi bi-funnel"></i> Lọc</button>
                <a href="${pageContext.request.contextPath}/admin/enrollments" class="btn btn-outline-secondary w-100"><i class="bi bi-arrow-counterclockwise"></i> Reset</a>
            </div>
        </form>
    </div>
</div>

<!-- Enrollments Table -->
<div class="data-card mb-4">
    <div class="card-header">
        <h5><i class="bi bi-list-check me-2"></i>All Enrollments (${enrollments.size()})</h5>
    </div>
    <div class="card-body p-0">
        <c:if test="${empty enrollments}">
            <div class="empty-state">
                <i class="bi bi-person-x"></i>
                <h5>No Enrollments</h5>
                <p>No students have been enrolled yet.</p>
            </div>
        </c:if>
        <c:if test="${not empty enrollments}">
            <div class="table-responsive">
                <table class="table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Student</th>
                            <th>Course</th>
                            <th>Enrolled On</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="e" items="${enrollments}">
                            <tr>
                                <td>${e.enrollmentId}</td>
                                <td><strong>${e.studentName}</strong></td>
                                <td>${e.courseName}</td>
                                <td><ct:dateFormat date="${e.enrolledAt}" pattern="dd/MM/yyyy HH:mm" /></td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/enrollments?action=unenroll&id=${e.enrollmentId}"
                                       class="btn btn-sm btn-outline-danger"
                                       onclick="return confirm('Remove this enrollment?');">
                                        <i class="bi bi-person-dash me-1"></i> Unenroll
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:if>
    </div>
</div>

<!-- Pagination -->
<div class="d-flex justify-content-between align-items-center mb-5 mt-3">
    <div>
        Trang <strong>${currentPage}</strong> / <strong>${totalPages > 0 ? totalPages : 1}</strong> — Tổng <strong>${totalRecords}</strong> ghi danh
    </div>
    
    <c:if test="${totalPages > 1}">
        <nav>
            <ul class="pagination mb-0">
                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                    <a class="page-link" href="?page=${currentPage - 1}&keyword=${keyword}&courseFilter=${courseFilter}">Trước</a>
                </li>
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <c:if test="${i >= currentPage - 2 && i <= currentPage + 2}">
                        <li class="page-item ${currentPage == i ? 'active' : ''}">
                            <a class="page-link" href="?page=${i}&keyword=${keyword}&courseFilter=${courseFilter}">${i}</a>
                        </li>
                    </c:if>
                </c:forEach>
                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                    <a class="page-link" href="?page=${currentPage + 1}&keyword=${keyword}&courseFilter=${courseFilter}">Sau</a>
                </li>
            </ul>
        </nav>
    </c:if>
</div>

<jsp:include page="/common/footer.jsp" />

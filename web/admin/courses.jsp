<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="ct" uri="http://assignmentmanage/customtags"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Manage Courses" />
</jsp:include>

<c:if test="${not empty error}">
    <div class="alert alert-danger">${error}</div>
</c:if>
<c:if test="${not empty success}">
    <div class="alert alert-success">${success}</div>
</c:if>

<!-- Create / Edit Course Form -->
<div class="data-card mb-4">
    <div class="card-header">
        <h5>
            <i class="bi bi-${not empty editCourse ? 'pencil' : 'plus-circle'} me-2"></i>
            ${not empty editCourse ? 'Edit Class' : 'Create New Class'}
        </h5>
    </div>
    <div class="card-body">
        <form method="post" action="${pageContext.request.contextPath}/admin/courses">
            <input type="hidden" name="action" value="${not empty editCourse ? 'edit' : 'create'}">
            <c:if test="${not empty editCourse}">
                <input type="hidden" name="courseId" value="${editCourse.courseId}">
            </c:if>

            <div class="row g-3">
                <div class="col-md-2">
                    <label for="courseCode" class="form-label">Class Code *</label>
                    <input type="text" class="form-control" id="courseCode" name="courseCode"
                           value="${editCourse.courseCode}" placeholder="e.g. SE1234" required>
                </div>
                <div class="col-md-3">
                    <label for="courseName" class="form-label">Class Name *</label>
                    <input type="text" class="form-control" id="courseName" name="courseName"
                           value="${editCourse.courseName}" placeholder="Class name" required>
                </div>
                <div class="col-md-3">
                    <label for="description" class="form-label">Description</label>
                    <input type="text" class="form-control" id="description" name="description"
                           value="${editCourse.description}" placeholder="Brief description">
                </div>
                <div class="col-md-2">
                    <label for="instructorId" class="form-label">Instructor *</label>
                    <select class="form-select" id="instructorId" name="instructorId" required>
                        <option value="">Select</option>
                        <c:forEach var="inst" items="${instructors}">
                            <option value="${inst.userId}"
                                ${editCourse.instructorId == inst.userId ? 'selected' : ''}>
                                ${inst.fullName}
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-2">
                    <label for="semesterId" class="form-label">Semester</label>
                    <select class="form-select" id="semesterId" name="semesterId">
                        <option value="">-- None --</option>
                        <c:forEach var="sem" items="${semesters}">
                            <option value="${sem.semesterId}"
                                ${editCourse.semesterId == sem.semesterId ? 'selected' : ''}>
                                ${sem.semesterName}
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-2">
                    <label for="subjectId" class="form-label">Subject</label>
                    <select class="form-select" id="subjectId" name="subjectId">
                        <option value="">-- None --</option>
                        <c:forEach var="sub" items="${subjects}">
                            <option value="${sub.subjectId}"
                                ${editCourse.subjectId == sub.subjectId ? 'selected' : ''}>
                                ${sub.subjectCode} - ${sub.subjectName}
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-2">
                    <label for="maxStudents" class="form-label">Max Students</label>
                    <input type="number" class="form-control" id="maxStudents" name="maxStudents"
                           value="${not empty editCourse ? editCourse.maxStudents : 40}" min="1">
                </div>
                <div class="col-md-2 d-flex align-items-end">
                    <button type="submit" class="btn btn-gradient w-100">
                        <i class="bi bi-check-lg me-1"></i>
                        ${not empty editCourse ? 'Update' : 'Create'}
                    </button>
                </div>
            </div>
        </form>
        <c:if test="${not empty editCourse}">
            <a href="${pageContext.request.contextPath}/admin/courses" class="btn btn-outline-primary btn-sm mt-2">
                <i class="bi bi-x-lg me-1"></i> Cancel Edit
            </a>
        </c:if>
    </div>
</div>

<!-- Courses Table -->
<div class="data-card">
    <div class="card-header">
        <h5><i class="bi bi-book me-2"></i>All Classes (${courses.size()})</h5>
    </div>
    <div class="card-body p-0">
        <div class="table-responsive">
            <table class="table">
                <thead>
                    <tr>
                        <th>Code</th>
                        <th>Class Name</th>
                        <th>Subject</th>
                        <th>Semester</th>
                        <th>Instructor</th>
                        <th>Students</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="course" items="${courses}">
                        <tr>
                            <td><strong>${course.courseCode}</strong></td>
                            <td>${course.courseName}</td>
                            <td>
                                <c:if test="${not empty course.subjectName}">
                                    <span class="badge bg-secondary">${course.subjectName}</span>
                                </c:if>
                            </td>
                            <td>${course.semesterName}</td>
                            <td>${course.instructorName}</td>
                            <td>
                                <span class="badge bg-primary">${course.studentCount}</span> / ${course.maxStudents}
                            </td>
                            <td>
                                <span class="badge ${course.isActive ? 'bg-success' : 'bg-secondary'}">
                                    ${course.isActive ? 'Active' : 'Inactive'}
                                </span>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/admin/course-detail?id=${course.courseId}"
                                   class="btn btn-sm btn-outline-info" title="View Details">
                                    <i class="bi bi-eye"></i>
                                </a>
                                <a href="${pageContext.request.contextPath}/admin/courses?action=edit&id=${course.courseId}"
                                   class="btn btn-sm btn-outline-primary" title="Edit">
                                    <i class="bi bi-pencil"></i>
                                </a>
                                <a href="${pageContext.request.contextPath}/admin/courses?action=toggleActive&id=${course.courseId}"
                                   class="btn btn-sm ${course.isActive ? 'btn-outline-danger' : 'btn-outline-success'}"
                                   title="${course.isActive ? 'Deactivate' : 'Activate'}">
                                    <i class="bi bi-${course.isActive ? 'x-circle' : 'check-circle'}"></i>
                                </a>
                                <form method="post" action="${pageContext.request.contextPath}/admin/courses" style="display:inline;" onsubmit="return confirm('Are you sure you want to permanently delete this class?');">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="courseId" value="${course.courseId}">
                                    <button type="submit" class="btn btn-sm btn-outline-danger" title="Delete">
                                        <i class="bi bi-trash"></i>
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />


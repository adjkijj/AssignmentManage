<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="${not empty assignment ? 'Edit Assignment' : 'Create Assignment'}" />
</jsp:include>

<div class="data-card">
    <div class="card-header">
        <h5>
            <i class="bi bi-${not empty assignment ? 'pencil' : 'plus-circle'} me-2"></i>
            ${not empty assignment ? 'Edit Assignment' : 'Create New Assignment'}
        </h5>
    </div>
    <div class="card-body">
        <form method="post" action="${pageContext.request.contextPath}/assignments" enctype="multipart/form-data">
            <input type="hidden" name="action" value="${not empty assignment ? 'edit' : 'create'}">
            <c:if test="${not empty assignment}">
                <input type="hidden" name="assignmentId" value="${assignment.assignmentId}">
            </c:if>

            <div class="row">
                <div class="col-md-8">
                    <div class="mb-3">
                        <label for="title" class="form-label">Assignment Title *</label>
                        <input type="text" class="form-control" id="title" name="title"
                               value="${assignment.title}" placeholder="Enter assignment title" required>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="mb-3">
                        <label for="courseId" class="form-label">Course *</label>
                        <select class="form-select" id="courseId" name="courseId" required>
                            <option value="">Select a course</option>
                            <c:forEach var="course" items="${courses}">
                                <option value="${course.courseId}"
                                    ${assignment.courseId == course.courseId ? 'selected' : ''}>
                                    ${course.courseCode} - ${course.courseName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </div>

            <div class="mb-3">
                <label for="description" class="form-label">Description</label>
                <textarea class="form-control" id="description" name="description" rows="5"
                          placeholder="Describe the assignment requirements...">${assignment.description}</textarea>
            </div>

            <div class="row">
                <div class="col-md-4">
                    <div class="mb-3">
                        <label for="deadline" class="form-label">Deadline *</label>
                        <input type="datetime-local" class="form-control" id="deadline" name="deadline"
                               value="<fmt:formatDate value='${assignment.deadline}' pattern='yyyy-MM-dd\'T\'HH:mm' />"
                               required>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="mb-3">
                        <label for="softDeadline" class="form-label">Soft Deadline <small class="text-muted">(late after)</small></label>
                        <input type="datetime-local" class="form-control" id="softDeadline" name="softDeadline"
                               value="<fmt:formatDate value='${assignment.softDeadline}' pattern='yyyy-MM-dd\'T\'HH:mm' />">
                        <div class="form-text">Submissions after this are marked as late.</div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="mb-3">
                        <label for="hardDeadline" class="form-label">Hard Deadline <small class="text-muted">(blocked after)</small></label>
                        <input type="datetime-local" class="form-control" id="hardDeadline" name="hardDeadline"
                               value="<fmt:formatDate value='${assignment.hardDeadline}' pattern='yyyy-MM-dd\'T\'HH:mm' />">
                        <div class="form-text">Submissions are blocked after this.</div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-md-6">
                    <div class="mb-3">
                        <label for="attachment" class="form-label">Attachment (optional)</label>
                        <input type="file" class="form-control" id="attachment" name="attachment">
                        <c:if test="${not empty assignment.attachmentPath}">
                            <div class="form-text">
                                Current: ${assignment.attachmentPath}
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>

            <div class="mt-3">
                <button type="submit" class="btn btn-gradient">
                    <i class="bi bi-check-lg me-1"></i> ${not empty assignment ? 'Update' : 'Create'} Assignment
                </button>
                <a href="${pageContext.request.contextPath}/assignments" class="btn btn-outline-primary ms-2">
                    <i class="bi bi-x-lg me-1"></i> Cancel
                </a>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />

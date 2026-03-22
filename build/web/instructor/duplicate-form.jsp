<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="ct" uri="http://assignmentmanage/customtags"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Duplicate Assignment" />
</jsp:include>

<div class="data-card">
    <div class="card-header">
        <h5><i class="bi bi-copy me-2"></i>Nhân bản bài tập</h5>
    </div>
    <div class="card-body">
        <div class="alert alert-info mb-3">
            <i class="bi bi-info-circle me-1"></i>
            Bài gốc: <strong>${sourceAssignment.title}</strong>
            (Course ID: ${sourceAssignment.courseId})
        </div>

        <form method="post" action="${pageContext.request.contextPath}/assignments">
            <input type="hidden" name="action" value="duplicate">
            <input type="hidden" name="sourceId" value="${sourceAssignment.assignmentId}">

            <div class="mb-3">
                <label class="form-label">Khóa học đích *</label>
                <select name="targetCourseId" class="form-select" required>
                    <option value="">-- Chọn course đích --</option>
                    <c:forEach var="c" items="${targetCourses}">
                        <option value="${c.courseId}">${c.courseCode} — ${c.courseName}</option>
                    </c:forEach>
                </select>
                <small class="text-muted">Course gốc đã bị loại khỏi danh sách</small>
            </div>

            <div class="row">
                <div class="col-md-4 mb-3">
                    <label class="form-label">Deadline mới *</label>
                    <input type="datetime-local" class="form-control" name="newDeadline" required>
                </div>
                <div class="col-md-4 mb-3">
                    <label class="form-label">Soft Deadline (tùy chọn)</label>
                    <input type="datetime-local" class="form-control" name="newSoftDeadline">
                </div>
                <div class="col-md-4 mb-3">
                    <label class="form-label">Hard Deadline (tùy chọn)</label>
                    <input type="datetime-local" class="form-control" name="newHardDeadline">
                </div>
            </div>

            <div class="alert alert-secondary mb-3" style="font-size: 0.85rem;">
                <strong>Sẽ được copy:</strong> Title, Description, Attachment<br>
                <strong>Cần đặt mới:</strong> Deadline
            </div>

            <div class="d-flex gap-2">
                <button type="submit" class="btn btn-gradient">
                    <i class="bi bi-copy me-1"></i>Nhân bản
                </button>
                <a href="${pageContext.request.contextPath}/assignments" class="btn btn-outline-secondary">Hủy</a>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />

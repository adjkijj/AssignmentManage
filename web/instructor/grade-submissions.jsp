<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="ct" uri="http://assignmentmanage/customtags"%>
<%@taglib prefix="prev" tagdir="/WEB-INF/tags"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Grade Submissions" />
</jsp:include>

<div class="data-card">
    <div class="card-header">
        <h5><i class="bi bi-file-earmark-check me-2"></i>Submissions for: ${assignment.title}</h5>
        <div class="d-flex gap-1">
            <!-- Feature 4: Reminder button -->
            <c:if test="${notSubmittedCount > 0}">
                <a href="${pageContext.request.contextPath}/submissions?action=sendReminder&assignmentId=${assignment.assignmentId}"
                   class="btn btn-sm btn-outline-warning">
                    <i class="bi bi-bell me-1"></i>Nhắc nhở (${notSubmittedCount} chưa nộp)
                </a>
            </c:if>
            <a href="${pageContext.request.contextPath}/assignments?action=detail&id=${assignment.assignmentId}"
               class="btn btn-sm btn-outline-primary">
                <i class="bi bi-arrow-left me-1"></i> Back to Assignment
            </a>
        </div>
    </div>
    <div class="card-body">
        <!-- Success message for reminder sent -->
        <c:if test="${not empty param.reminderSent}">
            <div class="alert alert-success alert-dismissible fade show">
                <i class="bi bi-check-circle me-1"></i>Đã gửi nhắc nhở đến ${param.reminderSent} student.
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <c:if test="${empty submissions}">
            <div class="empty-state">
                <i class="bi bi-inbox"></i>
                <h5>No Submissions</h5>
                <p>No students have submitted this assignment yet.</p>
            </div>
        </c:if>

        <c:if test="${not empty submissions}">
            <div class="table-responsive">
                <table class="table">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Student</th>
                            <th>Version</th>
                            <th>Submitted</th>
                            <th>Status</th>
                            <th>File</th>
                            <th>Grade</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="s" items="${submissions}" varStatus="loop">
                            <tr>
                                <td>${loop.count}</td>
                                <td><strong>${s.studentName}</strong></td>
                                <td><span class="badge bg-secondary">v${s.version}</span></td>
                                <td><ct:dateFormat date="${s.submittedAt}" /></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${s.status == 'graded'}">
                                            <span class="badge bg-success">Graded</span>
                                        </c:when>
                                        <c:when test="${s.status == 'late'}">
                                            <span class="badge bg-warning text-dark">Late</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-primary">Submitted</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td><span class="text-muted">${s.filePath}</span></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${s.grade != null}">
                                            <span class="badge ${s.grade >= 5 ? 'bg-success' : 'bg-danger'}">
                                                ${s.grade}/10
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-warning text-dark">Not Graded</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <button class="btn btn-sm btn-gradient" data-bs-toggle="modal"
                                            data-bs-target="#gradeModal${s.submissionId}" title="Quick Grade">
                                        <i class="bi bi-pencil-square"></i>
                                    </button>
                                    <a href="${pageContext.request.contextPath}/rubric?action=grade&submissionId=${s.submissionId}"
                                       class="btn btn-sm btn-outline-info" title="Grade with Rubric">
                                        <i class="bi bi-ui-checks"></i>
                                    </a>
                                </td>
                            </tr>

                            <!-- Grade Modal with Feature 6: Feedback Templates -->
                            <div class="modal fade" id="gradeModal${s.submissionId}" tabindex="-1">
                                <div class="modal-dialog modal-lg">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <h5 class="modal-title">Grade: ${s.studentName} (v${s.version})</h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                        </div>
                                        <form method="post" action="${pageContext.request.contextPath}/submissions">
                                            <div class="modal-body">
                                                <input type="hidden" name="action" value="grade">
                                                <input type="hidden" name="submissionId" value="${s.submissionId}">
                                                <input type="hidden" name="assignmentId" value="${assignment.assignmentId}">

                                                <div class="mb-3">
                                                    <label class="form-label">File Submitted</label>
                                                    <c:if test="${not empty s.filePath}">
                                                        <prev:filePreview filePath="${s.filePath}" height="300px" />
                                                    </c:if>
                                                    <c:if test="${empty s.filePath}">
                                                        <p class="text-muted">No file attached.</p>
                                                    </c:if>
                                                </div>

                                                <c:if test="${s.isLate}">
                                                    <div class="alert alert-warning py-2">
                                                        <i class="bi bi-exclamation-triangle me-1"></i>
                                                        This submission was marked as <strong>late</strong>.
                                                    </div>
                                                </c:if>

                                                <div class="row">
                                                    <div class="col-md-6">
                                                        <div class="mb-3">
                                                            <label for="grade${s.submissionId}" class="form-label">Grade (0-10) *</label>
                                                            <input type="number" class="form-control"
                                                                   id="grade${s.submissionId}" name="grade"
                                                                   min="0" max="10" step="0.5"
                                                                   value="${s.grade}" required>
                                                        </div>

                                                        <div class="mb-3">
                                                            <label for="feedback${s.submissionId}" class="form-label">Feedback</label>
                                                            <textarea class="form-control"
                                                                      id="feedback${s.submissionId}" name="feedback"
                                                                      rows="5" placeholder="Provide feedback...">${s.feedback}</textarea>
                                                        </div>
                                                    </div>

                                                    <!-- Feature 6: Feedback Template Panel -->
                                                    <div class="col-md-6">
                                                        <label class="form-label"><i class="bi bi-lightning me-1"></i>Mẫu nhanh</label>
                                                        <c:if test="${not empty feedbackTemplates}">
                                                            <div style="max-height: 280px; overflow-y: auto; padding: 8px; background: var(--body-bg); border-radius: 8px;">
                                                                <c:set var="currentCat" value="" />
                                                                <c:forEach var="tpl" items="${feedbackTemplates}">
                                                                    <c:if test="${tpl.category != currentCat}">
                                                                        <c:if test="${not empty currentCat}"><hr class="my-1"></c:if>
                                                                        <small class="text-muted fw-bold text-uppercase">${tpl.category}</small><br>
                                                                        <c:set var="currentCat" value="${tpl.category}" />
                                                                    </c:if>
                                                                    <button type="button"
                                                                            class="btn btn-sm btn-outline-secondary mb-1 me-1 text-start template-btn"
                                                                            data-target="feedback${s.submissionId}"
                                                                            data-text="${tpl.content}"
                                                                            onclick="appendTemplate(this)"
                                                                            style="font-size: 0.78rem;">
                                                                        <c:choose>
                                                                            <c:when test="${tpl.category == 'positive'}"><i class="bi bi-plus-circle text-success me-1"></i></c:when>
                                                                            <c:when test="${tpl.category == 'negative'}"><i class="bi bi-dash-circle text-danger me-1"></i></c:when>
                                                                            <c:otherwise><i class="bi bi-circle text-secondary me-1"></i></c:otherwise>
                                                                        </c:choose>
                                                                        ${tpl.content}
                                                                    </button><br>
                                                                </c:forEach>
                                                            </div>
                                                            <button type="button" class="btn btn-sm btn-outline-danger mt-1"
                                                                    onclick="document.getElementById('feedback${s.submissionId}').value = ''">
                                                                <i class="bi bi-trash me-1"></i>Xóa hết
                                                            </button>
                                                        </c:if>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="modal-footer">
                                                <button type="button" class="btn btn-outline-primary" data-bs-dismiss="modal">Cancel</button>
                                                <button type="submit" class="btn btn-gradient">
                                                    <i class="bi bi-check-lg me-1"></i> Save Grade
                                                </button>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:if>
    </div>
</div>

<!-- Feature 6: Vanilla JS for template append -->
<script>
function appendTemplate(btn) {
    var targetId = btn.getAttribute('data-target');
    var text = btn.getAttribute('data-text');
    var textarea = document.getElementById(targetId);
    if (textarea.value.length > 0 && textarea.value.charAt(textarea.value.length - 1) !== '\n') {
        textarea.value += '\n';
    }
    textarea.value += text;
    textarea.focus();
}
</script>

<jsp:include page="/common/footer.jsp" />

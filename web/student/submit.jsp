<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="ct" uri="http://assignmentmanage/customtags"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Submit Assignment" />
</jsp:include>

<div class="data-card">
    <div class="card-header">
        <h5><i class="bi bi-cloud-arrow-up me-2"></i>Submit: ${assignment.title}</h5>
        <span class="badge bg-primary">${assignment.courseName}</span>
    </div>
    <div class="card-body">
        <div class="row mb-4">
            <div class="col-md-6">
                <p><strong>Deadline:</strong>
                    <ct:dateFormat date="${assignment.deadline}" pattern="dd/MM/yyyy HH:mm" />
                </p>
                <c:if test="${not empty assignment.softDeadline}">
                    <p><strong>Soft Deadline:</strong>
                        <span class="text-warning">
                            <ct:dateFormat date="${assignment.softDeadline}" pattern="dd/MM/yyyy HH:mm" />
                        </span>
                        <small class="text-muted">(late after this)</small>
                    </p>
                </c:if>
                <c:if test="${not empty assignment.hardDeadline}">
                    <p><strong>Hard Deadline:</strong>
                        <span class="text-danger">
                            <ct:dateFormat date="${assignment.hardDeadline}" pattern="dd/MM/yyyy HH:mm" />
                        </span>
                        <small class="text-muted">(blocked after this)</small>
                    </p>
                </c:if>
                <p><strong>Description:</strong></p>
                <p>${assignment.description}</p>
            </div>
            <div class="col-md-6">
                <c:if test="${not empty submission}">
                    <div class="alert ${submission.isLate ? 'alert-warning' : 'alert-success'}">
                        <i class="bi bi-check-circle me-2"></i>
                        <strong>Latest Submission (v${submission.version}):</strong><br>
                        File: ${submission.filePath}<br>
                        Submitted: <ct:dateFormat date="${submission.submittedAt}" />
                        <c:if test="${submission.isLate}">
                            <br><span class="badge bg-warning text-dark">Late</span>
                        </c:if>
                        <c:if test="${submission.status == 'graded'}">
                            <br><strong>Grade:</strong> ${submission.grade}/10
                            <br><strong>Feedback:</strong> ${submission.feedback}
                        </c:if>
                    </div>
                </c:if>
            </div>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <%-- Hidden data for JS --%>
        <input type="hidden" id="deadlineMs" value="${assignment.deadline.time}">
        <input type="hidden" id="softDeadlineMs" value="${not empty assignment.softDeadline ? assignment.softDeadline.time : ''}">
        <input type="hidden" id="assignmentTitle" value="${assignment.title}">
        <input type="hidden" id="currentVersion" value="${not empty submission ? submission.version : 0}">

        <form id="submitForm" method="post" action="${pageContext.request.contextPath}/submissions" enctype="multipart/form-data">
            <input type="hidden" name="action" value="submit">
            <input type="hidden" name="assignmentId" value="${assignment.assignmentId}">

            <div class="mb-3">
                <label for="file" class="form-label">Select File to Upload</label>
                <input type="file" class="form-control" id="file" name="file" required>
                <div class="form-text">Maximum file size: 50MB. Each upload creates a new version.</div>
            </div>

            <%-- Feature 3: Button opens modal instead of submitting directly --%>
            <button type="button" class="btn btn-gradient" id="openConfirmBtn" disabled>
                <i class="bi bi-cloud-arrow-up me-1"></i>
                ${not empty submission ? 'Submit New Version (v'.concat(submission.version + 1).concat(')') : 'Submit'}
            </button>
            <a href="${pageContext.request.contextPath}/assignments" class="btn btn-outline-primary ms-2">
                <i class="bi bi-arrow-left me-1"></i> Back
            </a>
        </form>
    </div>
</div>

<%-- ============== Feature 3: Confirmation Modal ============== --%>
<div class="modal fade" id="confirmModal" tabindex="-1" aria-labelledby="confirmModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="confirmModalLabel">
                    <i class="bi bi-shield-check me-2"></i>Xác nhận nộp bài
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <table class="table table-borderless mb-0">
                    <tr>
                        <td class="fw-semibold" style="width: 140px;">Bài tập:</td>
                        <td id="modalTitle"></td>
                    </tr>
                    <tr>
                        <td class="fw-semibold">File:</td>
                        <td id="modalFileName"></td>
                    </tr>
                    <tr>
                        <td class="fw-semibold">Kích thước:</td>
                        <td id="modalFileSize"></td>
                    </tr>
                    <tr>
                        <td class="fw-semibold">Deadline:</td>
                        <td id="modalDeadline"></td>
                    </tr>
                    <tr>
                        <td class="fw-semibold">Thời gian còn lại:</td>
                        <td id="modalTimeLeft"></td>
                    </tr>
                </table>
                <div id="modalWarnings"></div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">
                    <i class="bi bi-x-lg me-1"></i>Hủy
                </button>
                <button type="button" class="btn btn-gradient" id="confirmSubmitBtn">
                    <i class="bi bi-check-lg me-1"></i>Xác nhận nộp
                </button>
            </div>
        </div>
    </div>
</div>

<!-- Submission History -->
<c:if test="${not empty submissionHistory}">
    <div class="data-card mt-3">
        <div class="card-header">
            <h5><i class="bi bi-clock-history me-2"></i>Submission History</h5>
        </div>
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table">
                    <thead>
                        <tr>
                            <th>Version</th>
                            <th>File</th>
                            <th>Submitted</th>
                            <th>Status</th>
                            <th>Grade</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="h" items="${submissionHistory}">
                            <tr>
                                <td><span class="badge bg-secondary">v${h.version}</span></td>
                                <td><span class="text-muted">${h.filePath}</span></td>
                                <td><ct:dateFormat date="${h.submittedAt}" /></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${h.status == 'graded'}">
                                            <span class="badge bg-success">Graded</span>
                                        </c:when>
                                        <c:when test="${h.status == 'late'}">
                                            <span class="badge bg-warning text-dark">Late</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-primary">Submitted</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${h.grade != null}">
                                            <span class="badge ${h.grade >= 5 ? 'bg-success' : 'bg-danger'}">${h.grade}/10</span>
                                        </c:when>
                                        <c:otherwise><span class="text-muted">-</span></c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</c:if>

<%-- Feature 3: Vanilla JS for modal --%>
<script>
(function() {
    var fileInput = document.getElementById('file');
    var openBtn = document.getElementById('openConfirmBtn');
    var confirmBtn = document.getElementById('confirmSubmitBtn');
    var form = document.getElementById('submitForm');
    var deadlineMs = parseInt(document.getElementById('deadlineMs').value);
    var softDeadlineMs = document.getElementById('softDeadlineMs').value;
    var assTitle = document.getElementById('assignmentTitle').value;
    var currentVersion = parseInt(document.getElementById('currentVersion').value);

    // Enable button when file selected
    fileInput.addEventListener('change', function() {
        openBtn.disabled = !fileInput.files.length;
    });

    // Open modal
    openBtn.addEventListener('click', function() {
        if (!fileInput.files.length) return;
        var file = fileInput.files[0];
        var now = Date.now();
        var diff = deadlineMs - now;

        // Title & file info
        document.getElementById('modalTitle').textContent = assTitle;
        document.getElementById('modalFileName').textContent = file.name;
        document.getElementById('modalFileSize').textContent = formatSize(file.size);

        // Deadline
        var deadlineDate = new Date(deadlineMs);
        document.getElementById('modalDeadline').textContent =
            pad(deadlineDate.getDate()) + '/' + pad(deadlineDate.getMonth()+1) + '/' +
            deadlineDate.getFullYear() + ' ' + pad(deadlineDate.getHours()) + ':' +
            pad(deadlineDate.getMinutes());

        // Time left
        if (diff > 0) {
            var days = Math.floor(diff / 86400000);
            var hours = Math.floor((diff % 86400000) / 3600000);
            var mins = Math.floor((diff % 3600000) / 60000);
            document.getElementById('modalTimeLeft').innerHTML =
                '<span class="text-success">còn ' + days + ' ngày ' + hours + ' giờ ' + mins + ' phút</span>';
        } else {
            document.getElementById('modalTimeLeft').innerHTML =
                '<span class="text-danger fw-bold">Đã quá hạn!</span>';
        }

        // Warnings
        var warnings = '';
        if (softDeadlineMs && now > parseInt(softDeadlineMs)) {
            warnings += '<div class="alert alert-warning mt-2 mb-1 py-2">' +
                '<i class="bi bi-exclamation-triangle me-1"></i>Bài nộp này sẽ bị đánh dấu <strong>LATE</strong></div>';
        }
        if (currentVersion > 0) {
            warnings += '<div class="alert alert-info mt-2 mb-1 py-2">' +
                '<i class="bi bi-info-circle me-1"></i>Đây là lần nộp thứ <strong>' + (currentVersion + 1) +
                '</strong> — sẽ tạo version mới</div>';
        }
        document.getElementById('modalWarnings').innerHTML = warnings;

        // Show modal
        var modal = new bootstrap.Modal(document.getElementById('confirmModal'));
        modal.show();
    });

    // Confirm = submit the form
    confirmBtn.addEventListener('click', function() {
        form.submit();
    });

    function formatSize(bytes) {
        if (bytes < 1024) return bytes + ' B';
        if (bytes < 1048576) return (bytes / 1024).toFixed(1) + ' KB';
        return (bytes / 1048576).toFixed(2) + ' MB';
    }
    function pad(n) { return n < 10 ? '0' + n : n; }
})();
</script>

<jsp:include page="/common/footer.jsp" />

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="ct" uri="http://assignmentmanage/customtags"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Grading Queue" />
</jsp:include>

<c:if test="${totalCount == 0}">
    <div class="data-card">
        <div class="card-body text-center">
            <i class="bi bi-inbox" style="font-size: 3rem;"></i>
            <h5>Chưa có bài nộp</h5>
            <a href="${pageContext.request.contextPath}/submissions?action=view&assignmentId=${assignment.assignmentId}"
               class="btn btn-outline-primary mt-2">Quay về</a>
        </div>
    </div>
</c:if>

<c:if test="${totalCount > 0}">
    <!-- Progress Header -->
    <div class="data-card mb-3">
        <div class="card-body">
            <div class="d-flex justify-content-between align-items-center mb-2">
                <h5 class="mb-0"><i class="bi bi-stack me-2"></i>${assignment.title}</h5>
                <a href="${pageContext.request.contextPath}/submissions?action=view&assignmentId=${assignment.assignmentId}"
                   class="btn btn-sm btn-outline-primary">
                    <i class="bi bi-list-ul me-1"></i>Quay về danh sách
                </a>
            </div>
            <div class="d-flex align-items-center gap-2">
                <span class="fw-bold">Đang chấm bài ${currentIndex + 1} / ${totalCount}</span>
                <div class="progress flex-grow-1" style="height: 10px;">
                    <div class="progress-bar bg-success" style="width: ${(currentIndex + 1) * 100 / totalCount}%"></div>
                </div>
            </div>
        </div>
    </div>

    <!-- Current Submission -->
    <div class="data-card">
        <div class="card-header">
            <h5><i class="bi bi-person me-2"></i>${currentSubmission.studentName}</h5>
            <div>
                <c:choose>
                    <c:when test="${currentSubmission.grade != null}">
                        <span class="badge ${currentSubmission.grade >= 5 ? 'bg-success' : 'bg-danger'} fs-6">
                            Đã chấm: ${currentSubmission.grade}/10
                        </span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge bg-warning text-dark fs-6">Chưa chấm</span>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        <div class="card-body">
            <div class="row mb-3">
                <div class="col-md-4">
                    <strong>Version:</strong> v${currentSubmission.version}
                </div>
                <div class="col-md-4">
                    <strong>Nộp lúc:</strong> <ct:dateFormat date="${currentSubmission.submittedAt}" />
                </div>
                <div class="col-md-4">
                    <c:if test="${currentSubmission.isLate}">
                        <span class="badge bg-warning text-dark"><i class="bi bi-exclamation-triangle me-1"></i>Nộp muộn</span>
                    </c:if>
                    <c:if test="${!currentSubmission.isLate}">
                        <span class="badge bg-success"><i class="bi bi-check-circle me-1"></i>Đúng hạn</span>
                    </c:if>
                </div>
            </div>
            <div class="mb-3">
                <strong>File:</strong> <span class="text-muted">${currentSubmission.filePath}</span>
            </div>

            <!-- Grade Form -->
            <form method="post" action="${pageContext.request.contextPath}/submissions">
                <input type="hidden" name="action" value="gradeQueueSave">
                <input type="hidden" name="submissionId" value="${currentSubmission.submissionId}">
                <input type="hidden" name="assignmentId" value="${assignment.assignmentId}">
                <input type="hidden" name="nextIndex" value="${hasNext ? currentIndex + 1 : currentIndex}">

                <div class="row">
                    <div class="col-md-6">
                        <div class="mb-3">
                            <label for="grade" class="form-label">Điểm (0-10) *</label>
                            <input type="number" class="form-control" id="grade" name="grade"
                                   min="0" max="10" step="0.5" value="${currentSubmission.grade}" required>
                        </div>
                        <div class="mb-3">
                            <label for="feedback" class="form-label">Nhận xét</label>
                            <textarea class="form-control" id="feedback" name="feedback"
                                      rows="5" placeholder="Nhận xét...">${currentSubmission.feedback}</textarea>
                        </div>
                    </div>

                    <!-- Feature 6: Feedback templates in queue -->
                    <div class="col-md-6">
                        <c:if test="${not empty feedbackTemplates}">
                            <label class="form-label"><i class="bi bi-lightning me-1"></i>Mẫu nhanh</label>
                            <div style="max-height: 250px; overflow-y: auto; padding: 8px; background: var(--body-bg); border-radius: 8px;">
                                <c:set var="currentCat" value="" />
                                <c:forEach var="tpl" items="${feedbackTemplates}">
                                    <c:if test="${tpl.category != currentCat}">
                                        <c:if test="${not empty currentCat}"><hr class="my-1"></c:if>
                                        <small class="text-muted fw-bold text-uppercase">${tpl.category}</small><br>
                                        <c:set var="currentCat" value="${tpl.category}" />
                                    </c:if>
                                    <button type="button"
                                            class="btn btn-sm btn-outline-secondary mb-1 me-1 text-start"
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
                                    onclick="document.getElementById('feedback').value=''">
                                <i class="bi bi-trash me-1"></i>Xóa hết
                            </button>
                        </c:if>
                    </div>
                </div>

                <!-- Navigation Footer -->
                <hr>
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <c:if test="${hasPrev}">
                            <a href="${pageContext.request.contextPath}/submissions?action=gradeQueue&assignmentId=${assignment.assignmentId}&index=${currentIndex - 1}"
                               class="btn btn-outline-primary">
                                <i class="bi bi-arrow-left me-1"></i>Bài trước
                            </a>
                        </c:if>
                        <c:if test="${!hasPrev}">
                            <button type="button" class="btn btn-outline-secondary" disabled>
                                <i class="bi bi-arrow-left me-1"></i>Bài trước
                            </button>
                        </c:if>
                    </div>
                    <div class="d-flex gap-2">
                        <c:if test="${hasNext}">
                            <a href="${pageContext.request.contextPath}/submissions?action=gradeQueue&assignmentId=${assignment.assignmentId}&index=${currentIndex + 1}"
                               class="btn btn-outline-secondary">
                                Bỏ qua <i class="bi bi-arrow-right ms-1"></i>
                            </a>
                        </c:if>
                        <button type="submit" class="btn btn-gradient">
                            <i class="bi bi-check-lg me-1"></i>
                            ${hasNext ? 'Lưu & Bài tiếp →' : 'Lưu & Hoàn tất'}
                        </button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</c:if>

<script>
function appendTemplate(btn) {
    var text = btn.getAttribute('data-text');
    var textarea = document.getElementById('feedback');
    if (textarea.value.length > 0 && textarea.value.charAt(textarea.value.length - 1) !== '\n') {
        textarea.value += '\n';
    }
    textarea.value += text;
    textarea.focus();
}
</script>

<jsp:include page="/common/footer.jsp" />

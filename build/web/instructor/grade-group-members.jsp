<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="ct" uri="http://assignmentmanage/customtags"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Grade Group Members" />
</jsp:include>

<c:if test="${not empty sessionScope.success}">
    <div class="alert alert-success">${sessionScope.success}</div>
    <c:remove var="success" scope="session" />
</c:if>
<c:if test="${not empty sessionScope.error}">
    <div class="alert alert-danger">${sessionScope.error}</div>
    <c:remove var="error" scope="session" />
</c:if>

<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <h5 class="mb-0">Chấm điểm nhóm: ${group.groupName}</h5>
        <small class="text-muted">Bài tập: ${assignment.title}</small>
    </div>
    <a href="${pageContext.request.contextPath}/groups?assignmentId=${assignment.assignmentId}"
       class="btn btn-outline-primary btn-sm">
        <i class="bi bi-arrow-left me-1"></i> Quay lại
    </a>
</div>

<div class="data-card">
    <div class="card-header">
        <h5><i class="bi bi-pencil-square me-2"></i>Chấm điểm từng thành viên</h5>
        <span class="badge bg-secondary">${group.members.size()} thành viên</span>
    </div>
    <div class="card-body">
        <c:if test="${empty group.members}">
            <div class="empty-state">
                <i class="bi bi-person-x"></i>
                <h5>Chưa có thành viên</h5>
                <p>Nhóm chưa có thành viên nào.</p>
            </div>
        </c:if>

        <c:if test="${not empty group.members}">
            <form method="post" action="${pageContext.request.contextPath}/groups">
                <input type="hidden" name="action" value="gradeGroupMembers">
                <input type="hidden" name="groupId" value="${group.groupId}">

                <%-- Bulk Apply Section --%>
                <div class="row mb-3 align-items-end bg-light p-3 rounded mx-0">
                    <div class="col-md-3">
                        <label class="form-label mb-1 fw-bold text-primary">Chấm điểm chung</label>
                        <input type="number" id="bulkGrade" class="form-control" min="0" max="10" step="0.5" placeholder="0-10">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label mb-1 fw-bold text-primary">Nhận xét chung</label>
                        <input type="text" id="bulkFeedback" class="form-control" placeholder="Nhận xét cho cả nhóm...">
                    </div>
                    <div class="col-md-3">
                        <button type="button" class="btn btn-primary w-100" onclick="applyBulkGrade()">
                            <i class="bi bi-magic me-1"></i>Áp dụng tất cả
                        </button>
                    </div>
                </div>

                <div class="table-responsive">
                    <table class="table align-middle">
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>Sinh viên</th>
                                <th>Username</th>
                                <th>Trạng thái bài nộp</th>
                                <th>Điểm hiện tại</th>
                                <th style="width: 120px;">Điểm mới (0-10)</th>
                                <th style="width: 250px;">Nhận xét</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="member" items="${group.members}" varStatus="loop">
                                <c:set var="sub" value="${memberSubmissions[member.userId]}" />
                                <tr>
                                    <td>${loop.count}</td>
                                    <td><strong>${member.fullName}</strong></td>
                                    <td class="text-muted">${member.username}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${sub != null}">
                                                <c:choose>
                                                    <c:when test="${sub.status == 'graded'}">
                                                        <span class="badge bg-success">Đã chấm</span>
                                                    </c:when>
                                                    <c:when test="${sub.status == 'late'}">
                                                        <span class="badge bg-warning text-dark">Nộp muộn</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-primary">Đã nộp</span>
                                                    </c:otherwise>
                                                </c:choose>
                                                <small class="text-muted ms-1">v${sub.version}</small>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-secondary">Chưa nộp</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${sub != null && sub.grade != null}">
                                                <span class="badge ${sub.grade >= 5 ? 'bg-success' : 'bg-danger'}">
                                                    ${sub.grade}/10
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">—</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <input type="number" class="form-control form-control-sm"
                                               name="grade_${member.userId}"
                                               min="0" max="10" step="0.5"
                                               value="${sub != null ? sub.grade : ''}"
                                               placeholder="0-10">
                                    </td>
                                    <td>
                                        <textarea class="form-control form-control-sm"
                                                  name="feedback_${member.userId}"
                                                  rows="2" placeholder="Nhận xét...">${sub != null ? sub.feedback : ''}</textarea>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

                <div class="d-flex justify-content-end gap-2 mt-3">
                    <a href="${pageContext.request.contextPath}/groups?assignmentId=${assignment.assignmentId}"
                       class="btn btn-outline-primary">
                        <i class="bi bi-x-lg me-1"></i>Hủy
                    </a>
                    <button type="submit" class="btn btn-gradient">
                        <i class="bi bi-check-lg me-1"></i>Lưu điểm tất cả
                    </button>
                </div>
            </form>
        </c:if>
    </div>
</div>

<script>
    function applyBulkGrade() {
        const gradeVal = document.getElementById('bulkGrade').value;
        const feedbackVal = document.getElementById('bulkFeedback').value;
        
        // Find all grade inputs
        const gradeInputs = document.querySelectorAll('input[name^="grade_"]');
        gradeInputs.forEach(input => {
            input.value = gradeVal;
        });

        // Find all feedback textareas
        const feedbackInputs = document.querySelectorAll('textarea[name^="feedback_"]');
        feedbackInputs.forEach(input => {
            input.value = feedbackVal;
        });
    }
</script>

<jsp:include page="/common/footer.jsp" />

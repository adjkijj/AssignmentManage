<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="ct" uri="http://assignmentmanage/customtags"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Send Reminder" />
</jsp:include>

<div class="data-card">
    <div class="card-header">
        <h5><i class="bi bi-bell me-2"></i>Nhắc nhở student chưa nộp bài</h5>
        <a href="${pageContext.request.contextPath}/submissions?action=view&assignmentId=${assignment.assignmentId}"
           class="btn btn-sm btn-outline-primary">
            <i class="bi bi-arrow-left me-1"></i>Quay lại
        </a>
    </div>
    <div class="card-body">
        <div class="alert alert-info">
            <strong>Bài tập:</strong> ${assignment.title}<br>
            <strong>Deadline:</strong> <ct:dateFormat date="${assignment.deadline}" pattern="dd/MM/yyyy HH:mm" />
        </div>

        <c:if test="${empty notSubmittedStudents}">
            <div class="alert alert-success">
                <i class="bi bi-check-circle me-1"></i>Tất cả student đã nộp bài. Không cần gửi nhắc nhở.
            </div>
        </c:if>

        <c:if test="${not empty notSubmittedStudents}">
            <h6 class="mb-3">
                <i class="bi bi-people me-1"></i>Danh sách student sẽ nhận nhắc nhở
                <span class="badge bg-danger">${notSubmittedStudents.size()} người</span>
            </h6>
            <div class="table-responsive mb-4">
                <table class="table table-sm">
                    <thead>
                        <tr><th>#</th><th>Họ tên</th><th>Username</th><th>Email</th></tr>
                    </thead>
                    <tbody>
                        <c:forEach var="stu" items="${notSubmittedStudents}" varStatus="loop">
                            <tr>
                                <td>${loop.count}</td>
                                <td><strong>${stu.fullName}</strong></td>
                                <td>${stu.username}</td>
                                <td>${stu.email}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>

            <form method="post" action="${pageContext.request.contextPath}/submissions">
                <input type="hidden" name="action" value="sendReminder">
                <input type="hidden" name="assignmentId" value="${assignment.assignmentId}">

                <div class="mb-3">
                    <label for="reminderTitle" class="form-label">Tiêu đề thông báo</label>
                    <input type="text" class="form-control" id="reminderTitle" name="reminderTitle"
                           value="${defaultTitle}" required>
                </div>
                <div class="mb-3">
                    <label for="reminderMessage" class="form-label">Nội dung thông báo</label>
                    <textarea class="form-control" id="reminderMessage" name="reminderMessage"
                              rows="4" required>${defaultMessage}</textarea>
                </div>

                <div class="d-flex gap-2">
                    <button type="submit" class="btn btn-gradient">
                        <i class="bi bi-send me-1"></i>Gửi nhắc nhở đến ${notSubmittedStudents.size()} student
                    </button>
                    <a href="${pageContext.request.contextPath}/submissions?action=view&assignmentId=${assignment.assignmentId}"
                       class="btn btn-outline-primary">
                        Hủy
                    </a>
                </div>
            </form>
        </c:if>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />

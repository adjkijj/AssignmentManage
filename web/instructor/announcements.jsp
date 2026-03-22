<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="ct" uri="http://assignmentmanage/customtags"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Quản lý Thông báo" />
</jsp:include>

<div class="row">
    <div class="col-md-4 mb-4">
        <div class="card shadow-sm border-0">
            <div class="card-header bg-white">
                <h5 class="mb-0 text-primary"><i class="bi bi-pencil-square me-2"></i>Tạo thông báo mới</h5>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/announcements" method="POST">
                    <input type="hidden" name="action" value="add" />
                    <div class="mb-3">
                        <label class="form-label fw-bold">Chọn lớp học</label>
                        <select name="courseId" class="form-select" required>
                            <c:forEach var="c" items="${courses}">
                                <option value="${c.courseId}">${c.courseName}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Tiêu đề</label>
                        <input type="text" name="title" class="form-control" required />
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Nội dung</label>
                        <textarea name="content" class="form-control" rows="5" required></textarea>
                    </div>
                    <button type="submit" class="btn btn-primary w-100">
                        <i class="bi bi-send me-1"></i> Gửi thông báo
                    </button>
                </form>
            </div>
        </div>
    </div>
    
    <div class="col-md-8">
        <div class="card shadow-sm border-0">
            <div class="card-header bg-white">
                <h5 class="mb-0 text-primary"><i class="bi bi-megaphone me-2"></i>Lịch sử thông báo</h5>
            </div>
            <div class="card-body p-0">
                <div class="list-group list-group-flush">
                    <c:forEach var="a" items="${announcements}">
                        <div class="list-group-item p-3">
                            <div class="d-flex w-100 justify-content-between align-items-center mb-1">
                                <h5 class="mb-1 text-primary">${a.title}</h5>
                                <div>
                                    <small class="text-muted border rounded px-2 py-1"><i class="bi bi-clock me-1"></i><ct:dateFormat date="${a.createdAt}"/></small>
                                    <c:if test="${a.instructorId == sessionScope.currentUser.userId}">
                                        <form action="${pageContext.request.contextPath}/announcements" method="POST" class="d-inline" onsubmit="return confirm('Bạn có chắc muốn xóa?');">
                                            <input type="hidden" name="action" value="delete" />
                                            <input type="hidden" name="id" value="${a.announcementId}" />
                                            <button class="btn btn-sm btn-outline-danger ms-2" type="submit"><i class="bi bi-trash"></i></button>
                                        </form>
                                    </c:if>
                                </div>
                            </div>
                            <p class="mb-1 course-badge">
                                <c:choose>
                                    <c:when test="${a.courseId == 0}">
                                        <c:choose>
                                            <c:when test="${a.targetRole == 'instructor'}">
                                                <span class="badge bg-warning text-dark"><i class="bi bi-person-workspace me-1"></i>Hệ thống (Chỉ Giảng Viên)</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-danger"><i class="bi bi-globe me-1"></i>Hệ thống</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-secondary">${a.courseName}</span>
                                    </c:otherwise>
                                </c:choose>
                            </p>
                            <p class="mb-0 mt-2 text-dark" style="white-space: pre-wrap;">${a.content}</p>
                        </div>
                    </c:forEach>
                    <c:if test="${empty announcements}">
                        <div class="p-4 text-center text-muted">Chưa có thông báo nào.</div>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />

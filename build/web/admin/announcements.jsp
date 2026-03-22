<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Thông báo toàn hệ thống" />
</jsp:include>

<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h1 class="h2">Thông báo toàn hệ thống</h1>
</div>

<div class="row">
    <!-- Form tạo thông báo hệ thống -->
    <div class="col-md-4 mb-4">
        <div class="card shadow-sm border-0">
            <div class="card-header bg-danger text-white">
                <h5 class="mb-0"><i class="bi bi-broadcast me-2"></i>Tạo thông báo mới</h5>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/announcements" method="post">
                    <input type="hidden" name="action" value="add" />
                    <div class="mb-3">
                        <label class="form-label">Phạm vi</label>
                        <select class="form-select" name="targetRole" required>
                            <option value="all">Toàn hệ thống (Tất cả Giảng viên & Sinh viên)</option>
                            <option value="instructor">Toàn hệ thống (Chỉ dành cho Giảng viên)</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="title" class="form-label">Tiêu đề</label>
                        <input type="text" class="form-control" id="title" name="title" required maxlength="255">
                    </div>
                    <div class="mb-3">
                        <label for="content" class="form-label">Nội dung</label>
                        <textarea class="form-control" id="content" name="content" rows="4" required></textarea>
                    </div>
                    <button type="submit" class="btn btn-danger w-100">
                        <i class="bi bi-send-fill me-1"></i> Gửi Thông Báo
                    </button>
                </form>
            </div>
        </div>
    </div>

    <!-- Danh sách thông báo -->
    <div class="col-md-8">
        <h4 class="mb-3">Lịch sử thông báo</h4>
        <c:if test="${empty announcements}">
            <div class="alert alert-info border-0 shadow-sm text-center py-4">
                <i class="bi bi-info-circle fs-3 d-block mb-2"></i>
                Chưa có thông báo hệ thống nào được gửi.
            </div>
        </c:if>

        <div class="row">
            <c:forEach var="a" items="${announcements}">
                <div class="col-12 mb-3">
                    <div class="card shadow-sm border-0 border-start border-danger border-4">
                        <div class="card-body">
                            <div class="d-flex w-100 justify-content-between mb-2">
                                <h5 class="mb-1 text-danger">
                                    <i class="bi bi-megaphone-fill me-2"></i>${a.title}
                                </h5>
                                <small class="text-muted"><fmt:formatDate value="${a.createdAt}" pattern="dd/MM/yyyy HH:mm" /></small>
                            </div>
                            <p class="mb-2">
                                <c:choose>
                                    <c:when test="${a.targetRole == 'instructor'}">
                                        <span class="badge bg-warning text-dark"><i class="bi bi-person-workspace me-1"></i>Hệ thống (Chỉ Giảng Viên)</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-danger"><i class="bi bi-globe me-1"></i>Hệ thống (Tất cả)</span>
                                    </c:otherwise>
                                </c:choose>
                            </p>
                            <p class="mb-2 text-dark">${a.content}</p>
                            
                            <div class="d-flex justify-content-between align-items-center mt-3 pt-2 border-top">
                                <small class="text-muted">
                                    <i class="bi bi-person-badge"></i> Người gửi: ${a.instructorName}
                                </small>
                                <c:if test="${a.instructorId == sessionScope.currentUser.userId}">
                                    <form action="${pageContext.request.contextPath}/announcements" method="post" class="d-inline" onsubmit="return confirm('Bạn có chắc muốn xóa thông báo này?');">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="id" value="${a.announcementId}">
                                        <button type="submit" class="btn btn-sm btn-outline-secondary">
                                            <i class="bi bi-trash"></i> Xóa
                                        </button>
                                    </form>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />

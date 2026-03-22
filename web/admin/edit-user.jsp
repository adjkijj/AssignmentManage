<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Edit User - ${editUser.username}" />
</jsp:include>

<div class="container-fluid py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="h3 mb-0 text-gray-800">Cập nhật tài khoản</h2>
        <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-outline-secondary">
            <i class="bi bi-arrow-left"></i> Quay lại
        </a>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>
    <c:if test="${not empty sessionScope.flashSuccess}">
        <div class="alert alert-success">${sessionScope.flashSuccess}</div>
        <c:remove var="flashSuccess" scope="session" />
    </c:if>

    <div class="row">
        <!-- Update Info Form -->
        <div class="col-md-6 mb-4">
            <div class="data-card h-100">
                <div class="card-header">
                    <h5 class="mb-0"><i class="bi bi-person-lines-fill me-2"></i>Thông tin cá nhân</h5>
                </div>
                <div class="card-body">
                    <form method="post" action="${pageContext.request.contextPath}/admin/edit-user">
                        <input type="hidden" name="action" value="updateInfo">
                        <input type="hidden" name="userId" value="${editUser.userId}">
                        
                        <div class="mb-3">
                            <label class="form-label">Username</label>
                            <input type="text" class="form-control" value="${editUser.username}" disabled>
                            <small class="text-muted">Username không thể thay đổi.</small>
                        </div>
                        
                        <div class="mb-3">
                            <label for="fullName" class="form-label">Họ và tên *</label>
                            <input type="text" class="form-control" id="fullName" name="fullName" value="${editUser.fullName}" required>
                        </div>
                        
                        <div class="mb-3">
                            <label for="email" class="form-label">Email *</label>
                            <input type="email" class="form-control" id="email" name="email" value="${editUser.email}" required>
                        </div>
                        
                        <div class="mb-4">
                            <label for="role" class="form-label">Vai trò *</label>
                            <select class="form-select" id="role" name="role" required>
                                <option value="student" ${editUser.role == 'student' ? 'selected' : ''}>Student</option>
                                <option value="instructor" ${editUser.role == 'instructor' ? 'selected' : ''}>Instructor</option>
                                <option value="admin" ${editUser.role == 'admin' ? 'selected' : ''}>Admin</option>
                            </select>
                        </div>
                        
                        <button type="submit" class="btn btn-primary w-100"><i class="bi bi-save me-1"></i> Lưu thông tin</button>
                    </form>
                </div>
            </div>
        </div>

        <!-- Reset Password Form -->
        <div class="col-md-6 mb-4">
            <div class="data-card h-100">
                <div class="card-header bg-danger text-white">
                    <h5 class="mb-0"><i class="bi bi-shield-lock me-2"></i>Đặt lại mật khẩu</h5>
                </div>
                <div class="card-body">
                    <div class="alert alert-warning">
                        <i class="bi bi-exclamation-triangle-fill me-2"></i>
                        Mật khẩu sẽ được cập nhật ngay lập tức. Hãy chắc chắn thông báo cho người dùng mật khẩu mới này.
                    </div>
                    
                    <form method="post" action="${pageContext.request.contextPath}/admin/edit-user">
                        <input type="hidden" name="action" value="resetPassword">
                        <input type="hidden" name="userId" value="${editUser.userId}">
                        
                        <div class="mb-4">
                            <label for="newPassword" class="form-label">Mật khẩu mới *</label>
                            <input type="password" class="form-control" id="newPassword" name="newPassword" required minlength="6">
                            <div class="form-text">Ít nhất 6 ký tự. Cần được băm an toàn trong CSDL.</div>
                        </div>
                        
                        <button type="submit" class="btn btn-danger w-100" onclick="return confirm('Xác nhận đặt lại mật khẩu cho người dùng này?');">
                            <i class="bi bi-key-fill me-1"></i> Đặt lại mật khẩu
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />

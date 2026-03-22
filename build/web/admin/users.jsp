<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="ct" uri="http://assignmentmanage/customtags"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Manage Users" />
</jsp:include>

<c:if test="${not empty error}">
    <div class="alert alert-danger">${error}</div>
</c:if>
<c:if test="${not empty success}">
    <div class="alert alert-success">${success}</div>
</c:if>

<!-- Create / Edit User Form -->
<div class="data-card mb-4">
    <div class="card-header">
        <h5>
            <i class="bi bi-person-plus me-2"></i>
            Thêm người dùng mới
        </h5>
    </div>
    <div class="card-body">
        <form method="post" action="${pageContext.request.contextPath}/admin/users">
            <input type="hidden" name="action" value="create">

            <div class="row g-3">
                <div class="col-md-3">
                    <label for="username" class="form-label">Username *</label>
                    <input type="text" class="form-control" id="username" name="username" required>
                </div>
                <div class="col-md-3">
                    <label for="password" class="form-label">Password *</label>
                    <input type="password" class="form-control" id="password" name="password" required>
                </div>
                <div class="col-md-2">
                    <label for="fullName" class="form-label">Full Name *</label>
                    <input type="text" class="form-control" id="fullName" name="fullName" required>
                </div>
                <div class="col-md-2">
                    <label for="email" class="form-label">Email *</label>
                    <input type="email" class="form-control" id="email" name="email" required>
                </div>
                <div class="col-md-2">
                    <label for="role" class="form-label">Role *</label>
                    <select class="form-select" id="role" name="role" required>
                        <option value="student">Student</option>
                        <option value="instructor">Instructor</option>
                        <option value="admin">Admin</option>
                    </select>
                </div>
                <div class="col-12 text-end mt-3">
                    <button type="submit" class="btn btn-gradient">
                        <i class="bi bi-check-lg me-1"></i>
                        Thêm mới
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>

<!-- Filter Bar -->
<div class="data-card mb-4 mt-2">
    <div class="card-body">
        <form action="${pageContext.request.contextPath}/admin/users" method="get" class="row g-3">
            <div class="col-md-4">
                <label class="form-label">Tìm kiếm</label>
                <input type="text" name="keyword" class="form-control" placeholder="Username, email, tên..." value="${keyword}">
            </div>
            <div class="col-md-3">
                <label class="form-label">Vai trò</label>
                <select name="roleFilter" class="form-select">
                    <option value="">Tất cả</option>
                    <option value="student" ${roleFilter == 'student' ? 'selected' : ''}>Student</option>
                    <option value="instructor" ${roleFilter == 'instructor' ? 'selected' : ''}>Instructor</option>
                    <option value="admin" ${roleFilter == 'admin' ? 'selected' : ''}>Admin</option>
                </select>
            </div>
            <div class="col-md-3">
                <label class="form-label">Trạng thái</label>
                <select name="statusFilter" class="form-select">
                    <option value="">Tất cả</option>
                    <option value="active" ${statusFilter == 'active' ? 'selected' : ''}>Hoạt động</option>
                    <option value="inactive" ${statusFilter == 'inactive' ? 'selected' : ''}>Bị khóa</option>
                </select>
            </div>
            <div class="col-md-2 d-flex align-items-end gap-2">
                <button type="submit" class="btn btn-primary w-100"><i class="bi bi-funnel"></i> Lọc</button>
                <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-outline-secondary w-100"><i class="bi bi-arrow-counterclockwise"></i> Reset</a>
            </div>
        </form>
    </div>
</div>

<!-- Users Table -->
<div class="data-card mb-4">
    <div class="card-header">
        <h5><i class="bi bi-people me-2"></i>All Users (${users.size()})</h5>
    </div>
    <div class="card-body p-0">
        <div class="table-responsive">
            <table class="table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Username</th>
                        <th>Full Name</th>
                        <th>Email</th>
                        <th>Role</th>
                        <th>Status</th>
                        <th>Created</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="u" items="${users}">
                        <tr>
                            <td>${u.userId}</td>
                            <td><strong>${u.username}</strong></td>
                            <td>${u.fullName}</td>
                            <td>${u.email}</td>
                            <td><ct:roleBadge role="${u.role}" /></td>
                            <td>
                                <span class="badge ${u.isActive ? 'bg-success' : 'bg-secondary'}">
                                    ${u.isActive ? 'Active' : 'Inactive'}
                                </span>
                            </td>
                            <td><ct:dateFormat date="${u.createdAt}" pattern="dd/MM/yyyy" /></td>
                            <td>
                                <a href="${pageContext.request.contextPath}/admin/edit-user?id=${u.userId}"
                                   class="btn btn-sm btn-outline-primary" title="Edit">
                                    <i class="bi bi-pencil"></i>
                                </a>
                                <a href="${pageContext.request.contextPath}/admin/users?action=toggleActive&id=${u.userId}"
                                   class="btn btn-sm ${u.isActive ? 'btn-outline-danger' : 'btn-outline-success'}"
                                   title="${u.isActive ? 'Deactivate' : 'Activate'}">
                                    <i class="bi bi-${u.isActive ? 'x-circle' : 'check-circle'}"></i>
                                </a>
                                <form method="post" action="${pageContext.request.contextPath}/admin/users" style="display:inline;" onsubmit="return confirm('Are you sure you want to permanently delete this user?');">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="userId" value="${u.userId}">
                                    <button type="submit" class="btn btn-sm btn-outline-danger" title="Delete">
                                        <i class="bi bi-trash"></i>
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- Pagination -->
<div class="d-flex justify-content-between align-items-center mb-5 mt-3">
    <div>
        Trang <strong>${currentPage}</strong> / <strong>${totalPages > 0 ? totalPages : 1}</strong> — Tổng <strong>${totalRecords}</strong> người dùng
    </div>
    
    <c:if test="${totalPages > 1}">
        <nav>
            <ul class="pagination mb-0">
                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                    <a class="page-link" href="?page=${currentPage - 1}&keyword=${keyword}&roleFilter=${roleFilter}&statusFilter=${statusFilter}">Trước</a>
                </li>
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <c:if test="${i >= currentPage - 2 && i <= currentPage + 2}">
                        <li class="page-item ${currentPage == i ? 'active' : ''}">
                            <a class="page-link" href="?page=${i}&keyword=${keyword}&roleFilter=${roleFilter}&statusFilter=${statusFilter}">${i}</a>
                        </li>
                    </c:if>
                </c:forEach>
                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                    <a class="page-link" href="?page=${currentPage + 1}&keyword=${keyword}&roleFilter=${roleFilter}&statusFilter=${statusFilter}">Sau</a>
                </li>
            </ul>
        </nav>
    </c:if>
</div>

<jsp:include page="/common/footer.jsp" />

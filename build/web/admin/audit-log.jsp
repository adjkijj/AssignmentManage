<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="ct" uri="http://assignmentmanage/customtags"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Nhật ký hệ thống" />
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-4">
    <h4><i class="bi bi-shield-check me-2"></i>Nhật Ký Hệ Thống (Audit Log)</h4>
    <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-secondary">
        <i class="bi bi-arrow-left"></i> Quay lại Dashboard
    </a>
</div>

<!-- Filter Bar -->
<div class="data-card mb-4">
    <div class="card-body">
        <form action="${pageContext.request.contextPath}/admin/audit-log" method="get" class="row g-3 align-items-end">
            <div class="col-md-3">
                <label class="form-label">Tài khoản (Username)</label>
                <input type="text" name="usernameFilter" class="form-control" value="${usernameFilter}" placeholder="Nhập username...">
            </div>
            <div class="col-md-2">
                <label class="form-label">Hành động</label>
                <select name="actionFilter" class="form-select">
                    <option value="">Tất cả</option>
                    <option value="LOGIN" ${actionFilter == 'LOGIN' ? 'selected' : ''}>LOGIN</option>
                    <option value="LOGOUT" ${actionFilter == 'LOGOUT' ? 'selected' : ''}>LOGOUT</option>
                    <option value="CREATE_COURSE" ${actionFilter == 'CREATE_COURSE' ? 'selected' : ''}>CREATE_COURSE</option>
                    <option value="UPDATE_COURSE" ${actionFilter == 'UPDATE_COURSE' ? 'selected' : ''}>UPDATE_COURSE</option>
                    <option value="DELETE_COURSE" ${actionFilter == 'DELETE_COURSE' ? 'selected' : ''}>DELETE_COURSE</option>
                    <option value="CREATE_USER" ${actionFilter == 'CREATE_USER' ? 'selected' : ''}>CREATE_USER</option>
                    <option value="UPDATE_USER" ${actionFilter == 'UPDATE_USER' ? 'selected' : ''}>UPDATE_USER</option>
                    <option value="DELETE_USER" ${actionFilter == 'DELETE_USER' ? 'selected' : ''}>DELETE_USER</option>
                    <option value="CREATE_ASSIGNMENT" ${actionFilter == 'CREATE_ASSIGNMENT' ? 'selected' : ''}>CREATE_ASSIGNMENT</option>
                    <option value="DELETE_ASSIGNMENT" ${actionFilter == 'DELETE_ASSIGNMENT' ? 'selected' : ''}>DELETE_ASSIGNMENT</option>
                    <option value="SUBMIT_ASSIGNMENT" ${actionFilter == 'SUBMIT_ASSIGNMENT' ? 'selected' : ''}>SUBMIT_ASSIGNMENT</option>
                    <option value="GRADE_SUBMISSION" ${actionFilter == 'GRADE_SUBMISSION' ? 'selected' : ''}>GRADE_SUBMISSION</option>
                    <option value="EXPORT_GRADES" ${actionFilter == 'EXPORT_GRADES' ? 'selected' : ''}>EXPORT_GRADES</option>
                </select>
            </div>
            <div class="col-md-2">
                <label class="form-label">Từ ngày</label>
                <input type="date" name="dateFrom" class="form-control" value="${dateFrom}">
            </div>
            <div class="col-md-2">
                <label class="form-label">Đến ngày</label>
                <input type="date" name="dateTo" class="form-control" value="${dateTo}">
            </div>
            <div class="col-md-3">
                <button type="submit" class="btn btn-primary w-100 mb-2"><i class="bi bi-filter"></i> Lọc</button>
                <a href="${pageContext.request.contextPath}/admin/audit-log" class="btn btn-outline-secondary w-100">Reset</a>
            </div>
        </form>
    </div>
</div>

<!-- Results Table -->
<div class="data-card mb-4">
    <div class="card-body p-0">
        <div class="table-responsive">
            <table class="table table-striped table-hover mb-0">
                <thead class="table-dark">
                    <tr>
                        <th style="width: 5%">STT</th>
                        <th style="width: 15%">Thời gian</th>
                        <th style="width: 15%">Người dùng</th>
                        <th style="width: 15%">Hành động</th>
                        <th style="width: 10%">Đối tượng</th>
                        <th style="width: 30%">Mô tả</th>
                        <th style="width: 10%">IP</th>
                    </tr>
                </thead>
                <tbody>
                    <c:if test="${empty logs}">
                        <tr>
                            <td colspan="7" class="text-center py-4 text-muted">Không tìm thấy nhật ký hệ thống nào.</td>
                        </tr>
                    </c:if>
                    <c:forEach var="log" items="${logs}" varStatus="status">
                        <tr>
                            <td>${status.index + 1 + (currentPage - 1) * pageSize}</td>
                            <td><ct:dateFormat date="${log.createdAt}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
                            <td>
                                <strong>${not empty log.username ? log.username : 'System'}</strong>
                            </td>
                            <td>
                                <span class="badge ${log.action.contains('DELETE') ? 'bg-danger' : 
                                                    log.action.contains('CREATE') ? 'bg-success' : 
                                                    log.action.contains('UPDATE') ? 'bg-warning text-dark' : 'bg-primary'}">
                                    ${log.action}
                                </span>
                            </td>
                            <td>
                                <c:if test="${not empty log.entityType && log.entityType != 'Unknown'}">
                                    <span class="badge bg-secondary">${log.entityType} ${not empty log.entityId ? ('#' += log.entityId) : ''}</span>
                                </c:if>
                            </td>
                            <td class="text-break">${log.description}</td>
                            <td><small class="text-muted">${not empty log.ipAddress ? log.ipAddress : '-'}</small></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- Pagination -->
<div class="d-flex justify-content-between align-items-center mb-5">
    <div>
        Trang <strong>${currentPage}</strong> / <strong>${totalPages > 0 ? totalPages : 1}</strong> — Tổng <strong>${totalRecords}</strong> bản ghi
    </div>
    
    <c:if test="${totalPages > 1}">
        <nav>
            <ul class="pagination mb-0">
                <%-- Prev Button --%>
                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                    <a class="page-link" href="?page=${currentPage - 1}&usernameFilter=${usernameFilter}&actionFilter=${actionFilter}&dateFrom=${dateFrom}&dateTo=${dateTo}">Trước</a>
                </li>
                
                <%-- Page Numbers --%>
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <c:if test="${i >= currentPage - 2 && i <= currentPage + 2}">
                        <li class="page-item ${currentPage == i ? 'active' : ''}">
                            <a class="page-link" href="?page=${i}&usernameFilter=${usernameFilter}&actionFilter=${actionFilter}&dateFrom=${dateFrom}&dateTo=${dateTo}">${i}</a>
                        </li>
                    </c:if>
                </c:forEach>
                
                <%-- Next Button --%>
                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                    <a class="page-link" href="?page=${currentPage + 1}&usernameFilter=${usernameFilter}&actionFilter=${actionFilter}&dateFrom=${dateFrom}&dateTo=${dateTo}">Sau</a>
                </li>
            </ul>
        </nav>
    </c:if>
</div>

<jsp:include page="/common/footer.jsp" />

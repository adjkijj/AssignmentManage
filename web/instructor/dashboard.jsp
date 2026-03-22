<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="ct" uri="http://assignmentmanage/customtags"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Instructor Dashboard" />
</jsp:include>

<!-- Stat Cards -->
<div class="row g-4 mb-4">
    <div class="col-md-4">
        <div class="stat-card">
            <div class="stat-icon blue"><i class="bi bi-journal-text"></i></div>
            <div class="stat-number">${assignmentCount}</div>
            <div class="stat-label">Total Assignments</div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="stat-card">
            <div class="stat-icon orange"><i class="bi bi-hourglass-split"></i></div>
            <div class="stat-number">${pendingCount}</div>
            <div class="stat-label">Pending Grading</div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="stat-card">
            <div class="stat-icon purple"><i class="bi bi-book"></i></div>
            <div class="stat-number">${courses.size()}</div>
            <div class="stat-label">My Courses</div>
        </div>
    </div>
</div>

<!-- Feature 1: Course Breakdown Cards -->
<c:if test="${not empty courseStatsList}">
<div class="data-card mb-4">
    <div class="card-header">
        <h5><i class="bi bi-bar-chart-line me-2"></i>Course Breakdown</h5>
    </div>
    <div class="card-body">
        <div class="row g-3">
            <c:forEach var="cs" items="${courseStatsList}">
                <div class="col-md-6 col-lg-4">
                    <div class="p-3" style="background: var(--body-bg); border-radius: 12px; border-left: 4px solid ${cs.pendingGrading > 0 ? '#f97316' : 'var(--primary)'};">
                        <div class="d-flex justify-content-between align-items-start mb-2">
                            <div>
                                <strong style="color: var(--text-primary); font-size: 1.05rem;">${cs.courseCode}</strong>
                                <br><small class="text-muted">${cs.courseName}</small>
                            </div>
                            <c:if test="${cs.pendingGrading > 0}">
                                <span class="badge bg-warning text-dark">
                                    <i class="bi bi-exclamation-circle me-1"></i>${cs.pendingGrading} chờ chấm
                                </span>
                            </c:if>
                        </div>
                        <div class="d-flex gap-3 mt-2 mb-2" style="font-size: 0.85rem;">
                            <span><i class="bi bi-journal me-1 text-primary"></i>${cs.totalAssignments} bài tập</span>
                            <span><i class="bi bi-people me-1 text-success"></i>${cs.totalStudents} SV</span>
                        </div>
                        <c:if test="${cs.nearestDeadline != null}">
                            <div style="font-size: 0.8rem;" class="text-muted mb-2">
                                <i class="bi bi-clock me-1"></i>Deadline gần nhất:
                                <ct:dateFormat date="${cs.nearestDeadline}" pattern="dd/MM/yyyy HH:mm" />
                            </div>
                        </c:if>
                        <a href="${pageContext.request.contextPath}/assignments?courseId=${cs.courseId}"
                           class="btn btn-sm btn-outline-primary w-100">
                            <i class="bi bi-eye me-1"></i>Xem lớp
                        </a>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
</div>
</c:if>

<div class="row g-4 mb-4">
    <!-- Quick Actions -->
    <div class="col-md-6">
        <div class="data-card mb-4">
            <div class="card-header">
                <h5><i class="bi bi-lightning-charge me-2"></i>Quick Actions</h5>
            </div>
            <div class="card-body">
                <div class="d-grid gap-2">
                    <a href="${pageContext.request.contextPath}/gradebook" class="btn btn-success py-3">
                        <i class="bi bi-table me-2"></i> Gradebook
                    </a>
                    <a href="${pageContext.request.contextPath}/assignments?action=create" class="btn btn-gradient py-3">
                        <i class="bi bi-plus-circle me-2"></i> Create New Assignment
                    </a>
                    <a href="${pageContext.request.contextPath}/assignments" class="btn btn-outline-primary py-3">
                        <i class="bi bi-list-ul me-2"></i> View All Assignments
                    </a>
                </div>
            </div>
        </div>

        <!-- Feature 12: At-Risk Students -->
        <div class="data-card">
            <div class="card-header" style="border-bottom-color: #ef4444;">
                <h5 class="text-danger mb-0"><i class="bi bi-exclamation-triangle-fill me-2"></i>At-Risk Students</h5>
            </div>
            <div class="card-body" style="max-height: 300px; overflow-y: auto;">
                <c:if test="${empty atRiskList}">
                    <div class="text-center text-success py-2">
                        <i class="bi bi-check-circle fs-4 d-block mb-1"></i>
                        <small>Không có sinh viên nào trong diện cảnh báo.</small>
                    </div>
                </c:if>
                <c:forEach var="stu" items="${atRiskList}">
                    <div class="alert alert-danger p-2 mb-2" style="font-size: 0.85rem;">
                        <strong><i class="bi bi-person me-1"></i>${stu.studentName}</strong> 
                        <span class="text-muted ms-1">(${stu.courseName})</span>
                        <ul class="mb-0 ps-3 mt-1 text-danger">
                            <c:forEach var="reason" items="${stu.reasons}">
                                <li>${reason}</li>
                            </c:forEach>
                        </ul>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>

    <!-- Recent Notifications -->
    <div class="col-md-6">
        <div class="data-card">
            <div class="card-header">
                <h5><i class="bi bi-bell me-2"></i>Recent Notifications</h5>
            </div>
            <div class="card-body">
                <c:if test="${empty recentNotifications}">
                    <p class="text-muted text-center">No notifications.</p>
                </c:if>
                <c:forEach var="n" items="${recentNotifications}">
                    <div class="d-flex align-items-start p-2 mb-2 ${!n.isRead ? 'border-start border-primary border-3' : ''}"
                         style="background: var(--body-bg); border-radius: 8px;">
                        <i class="bi bi-bell${n.isRead ? '' : '-fill text-primary'} me-2 mt-1"></i>
                        <div>
                            <strong>${n.title}</strong>
                            <br><small class="text-muted">${n.message}</small>
                            <br><small class="text-muted"><ct:dateFormat date="${n.createdAt}" /></small>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="ct" uri="http://assignmentmanage/customtags"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Student Dashboard" />
</jsp:include>

<!-- Stat Cards -->
<div class="row g-4 mb-4">
    <div class="col-md-3 col-sm-6">
        <div class="stat-card">
            <div class="stat-icon blue"><i class="bi bi-book"></i></div>
            <div class="stat-number">${courseCount}</div>
            <div class="stat-label">Enrolled Courses</div>
        </div>
    </div>
    <div class="col-md-3 col-sm-6">
        <div class="stat-card">
            <div class="stat-icon purple"><i class="bi bi-journal-text"></i></div>
            <div class="stat-number">${assignmentCount}</div>
            <div class="stat-label">Total Assignments</div>
        </div>
    </div>
    <div class="col-md-3 col-sm-6">
        <div class="stat-card">
            <div class="stat-icon green"><i class="bi bi-cloud-arrow-up"></i></div>
            <div class="stat-number">${submissionCount}</div>
            <div class="stat-label">Submissions</div>
        </div>
    </div>
    <div class="col-md-3 col-sm-6">
        <div class="stat-card">
            <div class="stat-icon pink"><i class="bi bi-award"></i></div>
            <div class="stat-number">${gradedCount}</div>
            <div class="stat-label">Graded</div>
        </div>
    </div>
</div>

<!-- ============== Feature 4: Todo List ============== -->
<div class="data-card mb-4">
    <div class="card-header">
        <h5><i class="bi bi-list-check me-2"></i>Việc cần làm</h5>
    </div>
    <div class="card-body">
        <c:if test="${empty todoItems}">
            <div class="text-center text-muted py-3">
                <i class="bi bi-emoji-smile fs-1"></i>
                <p class="mt-2 mb-0">Bạn đã hoàn thành tất cả bài tập!</p>
            </div>
        </c:if>
        <c:forEach var="item" items="${todoItems}">
            <div class="d-flex justify-content-between align-items-center p-2 mb-2"
                 style="background: var(--body-bg); border-radius: 8px;
                        border-left: 4px solid ${item[4] == 1 ? '#dc3545' : item[4] == 2 ? '#ffc107' : item[4] == 3 ? '#198754' : '#6c757d'};">
                <div class="d-flex align-items-center">
                    <c:choose>
                        <c:when test="${item[4] == 1}"><i class="bi bi-exclamation-circle-fill text-danger fs-5 me-2"></i></c:when>
                        <c:when test="${item[4] == 2}"><i class="bi bi-clock-fill text-warning fs-5 me-2"></i></c:when>
                        <c:when test="${item[4] == 3}"><i class="bi bi-star-fill text-success fs-5 me-2"></i></c:when>
                        <c:otherwise><i class="bi bi-journal-text text-secondary fs-5 me-2"></i></c:otherwise>
                    </c:choose>
                    <div>
                        <strong>${item[1]}</strong>
                        <br><small class="text-muted">${item[2]} · ${item[5]}</small>
                    </div>
                </div>
                <div class="text-end">
                    <c:if test="${item[4] != 3}">
                        <span class="badge bg-warning text-dark d-block mb-1">
                            <ct:dateFormat date="${item[3]}" pattern="dd/MM HH:mm" />
                        </span>
                        <a href="${pageContext.request.contextPath}/submissions?action=submit&assignmentId=${item[0]}"
                           class="btn btn-sm btn-gradient">Nộp bài</a>
                    </c:if>
                    <c:if test="${item[4] == 3}">
                        <a href="${pageContext.request.contextPath}/submissions?action=grades"
                           class="btn btn-sm btn-outline-success">Xem điểm</a>
                    </c:if>
                </div>
            </div>
        </c:forEach>
    </div>
</div>

<div class="row g-4 mb-4">
    <!-- ============== Feature 2: Progress Bars ============== -->
    <div class="col-md-6">
        <div class="data-card">
            <div class="card-header">
                <h5><i class="bi bi-bar-chart-line me-2"></i>Tiến độ theo môn học</h5>
            </div>
            <div class="card-body">
                <c:if test="${empty courseProgress}">
                    <p class="text-muted text-center">Chưa enroll môn nào.</p>
                </c:if>
                <c:forEach var="p" items="${courseProgress}">
                    <c:set var="total" value="${p[3]}" />
                    <c:set var="done" value="${p[4]}" />
                    <c:set var="pct" value="${total > 0 ? (done * 100 / total) : 0}" />
                    <div class="mb-3">
                        <div class="d-flex justify-content-between mb-1">
                            <span class="fw-semibold">${p[2]} — ${p[1]}</span>
                            <span class="text-muted">${done}/${total}</span>
                        </div>
                        <div class="progress" style="height: 10px;">
                            <div class="progress-bar ${pct >= 80 ? 'bg-success' : pct >= 40 ? 'bg-warning' : 'bg-danger'}"
                                 role="progressbar" style="width: ${pct}%"
                                 aria-valuenow="${pct}" aria-valuemin="0" aria-valuemax="100">
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>

    <!-- Upcoming Deadlines -->
    <div class="col-md-6">
        <div class="data-card">
            <div class="card-header">
                <h5><i class="bi bi-alarm me-2"></i>Upcoming Deadlines</h5>
            </div>
            <div class="card-body">
                <c:if test="${empty upcomingDeadlines}">
                    <p class="text-muted text-center">No upcoming deadlines.</p>
                </c:if>
                <c:forEach var="a" items="${upcomingDeadlines}">
                    <div class="d-flex justify-content-between align-items-center p-2 mb-2"
                         style="background: var(--body-bg); border-radius: 8px;">
                        <div>
                            <strong>${a.title}</strong>
                            <br><small class="text-muted">${a.courseName}</small>
                        </div>
                        <div class="text-end">
                            <span class="badge bg-warning text-dark">
                                <ct:dateFormat date="${a.deadline}" pattern="dd/MM HH:mm" />
                            </span>
                            <br>
                            <a href="${pageContext.request.contextPath}/submissions?action=submit&assignmentId=${a.assignmentId}"
                               class="btn btn-sm btn-gradient mt-1">Submit</a>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>
</div>

<div class="row g-4 mb-4">
    <!-- Recent Notifications -->
    <div class="col-md-6">
        <div class="data-card">
            <div class="card-header">
                <h5>
                    <i class="bi bi-bell me-2"></i>Notifications
                    <c:if test="${unreadCount > 0}">
                        <span class="badge bg-danger ms-1">${unreadCount}</span>
                    </c:if>
                </h5>
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

    <!-- Quick Actions -->
    <div class="col-md-6">
        <div class="data-card">
            <div class="card-header">
                <h5><i class="bi bi-lightning-charge me-2"></i>Quick Actions</h5>
            </div>
            <div class="card-body">
                <div class="row g-3">
                    <div class="col-6">
                        <a href="${pageContext.request.contextPath}/assignments" class="btn btn-gradient w-100 py-3">
                            <i class="bi bi-journal-text me-2"></i> View Assignments
                        </a>
                    </div>
                    <div class="col-6">
                        <a href="${pageContext.request.contextPath}/submissions?action=grades" class="btn btn-outline-primary w-100 py-3">
                            <i class="bi bi-award me-2"></i> View Grades
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="ct" uri="http://assignmentmanage/customtags"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Admin Dashboard" />
</jsp:include>

<!-- Hidden inputs for Chart.js data from Servlet -->
<input type="hidden" id="trendData" value='${trendDataJson}'>
<input type="hidden" id="topCoursesData" value='${topCoursesJson}'>

<div class="d-flex justify-content-between align-items-center mb-4">
    <h4><i class="bi bi-speedometer2 me-2"></i>Tổng Quan Hệ Thống</h4>
</div>

<!-- Row 1: Stat Cards -->
<div class="row g-3 mb-4">
    <div class="col-md-3">
        <div class="stat-card">
            <div class="stat-icon blue"><i class="bi bi-people-fill"></i></div>
            <div class="stat-number">${dashboardData.totalUsers}</div>
            <div class="stat-label">Tổng người dùng</div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="stat-card">
            <div class="stat-icon purple"><i class="bi bi-book-half"></i></div>
            <div class="stat-number">${dashboardData.totalCourses}</div>
            <div class="stat-label">Tổng khóa học</div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="stat-card">
            <div class="stat-icon orange"><i class="bi bi-file-earmark-arrow-up"></i></div>
            <div class="stat-number">${dashboardData.submissionsToday}</div>
            <div class="stat-label">Bài nộp hôm nay</div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="stat-card">
            <div class="stat-icon green"><i class="bi bi-play-circle-fill"></i></div>
            <div class="stat-number">${dashboardData.activeCoursesThisSemester}</div>
            <div class="stat-label">Khóa học đang chạy</div>
        </div>
    </div>
</div>

<!-- Row 2: Charts -->
<div class="row g-4 mb-4">
    <div class="col-md-6">
        <div class="data-card h-100">
            <div class="card-header">
                <h5><i class="bi bi-graph-up me-2"></i>Xu Hướng Nộp Bài (14 ngày qua)</h5>
            </div>
            <div class="card-body">
                <canvas id="trendChart" height="250"></canvas>
            </div>
        </div>
    </div>
    <div class="col-md-6">
        <div class="data-card h-100">
            <div class="card-header">
                <h5><i class="bi bi-bar-chart-fill me-2"></i>Top 5 Khóa Học (Nhiều bài nộp nhất)</h5>
            </div>
            <div class="card-body">
                <canvas id="topCoursesChart" height="250"></canvas>
            </div>
        </div>
    </div>
</div>

<!-- Row 3: Recent Activity -->
<div class="row g-4 mb-4">
    <div class="col-md-6">
        <div class="data-card h-100">
            <div class="card-header border-bottom">
                <h5 class="mb-0"><i class="bi bi-box-arrow-in-right me-2 text-primary"></i>10 Lượt Đăng Nhập Gần Nhất</h5>
            </div>
            <div class="card-body p-0">
                <c:if test="${empty dashboardData.recentLogins}">
                    <div class="p-3 text-center text-muted">
                        <i class="bi bi-info-circle me-1"></i> Chưa có dữ liệu AuditLog.
                    </div>
                </c:if>
                <c:if test="${not empty dashboardData.recentLogins}">
                    <table class="table table-hover table-sm mb-0">
                        <thead class="table-light">
                            <tr>
                                <th>Username</th>
                                <th>Thời gian</th>
                                <th>IP Address</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="l" items="${dashboardData.recentLogins}">
                                <tr>
                                    <td><strong>${l.username}</strong></td>
                                    <td><ct:dateFormat date="${l.time}" pattern="dd/MM/yyyy HH:mm" /></td>
                                    <td><small class="text-muted"><i class="bi bi-geo-alt me-1"></i>${l.ip}</small></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
            </div>
        </div>
    </div>
    <div class="col-md-6">
        <div class="data-card h-100">
            <div class="card-header border-bottom">
                <h5 class="mb-0"><i class="bi bi-cloud-arrow-up me-2 text-success"></i>10 Bài Nộp Gần Nhất</h5>
            </div>
            <div class="card-body p-0">
                <c:if test="${empty dashboardData.recentSubmissions}">
                    <div class="p-3 text-center text-muted">Chưa có dữ liệu bài nộp.</div>
                </c:if>
                <c:if test="${not empty dashboardData.recentSubmissions}">
                    <table class="table table-hover table-sm mb-0" style="font-size: 0.9rem;">
                        <thead class="table-light">
                            <tr>
                                <th>Sinh viên</th>
                                <th>Khóa học</th>
                                <th>Bài tập</th>
                                <th>Thời gian</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="s" items="${dashboardData.recentSubmissions}">
                                <tr>
                                    <td>
                                        <strong>${s.studentName}</strong><br>
                                        <small class="text-muted">${s.username}</small>
                                    </td>
                                    <td><span class="badge bg-secondary">${s.courseCode}</span></td>
                                    <td class="text-truncate" style="max-width: 150px;" title="${s.assignmentTitle}">${s.assignmentTitle}</td>
                                    <td><ct:dateFormat date="${s.time}" pattern="dd/MM/yyyy HH:mm" /></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
            </div>
        </div>
    </div>
</div>

<!-- Administration Quick Actions (Kept from existing dashboard) -->
<div class="data-card mb-4 mt-4">
    <div class="card-header">
        <h5><i class="bi bi-gear me-2"></i>Administration Shortcuts</h5>
    </div>
    <div class="card-body">
        <div class="row g-3">
            <div class="col-md-3">
                <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-gradient w-100 py-3">
                    <i class="bi bi-people me-2"></i> Manage Users
                </a>
            </div>
            <div class="col-md-3">
                <a href="${pageContext.request.contextPath}/admin/courses" class="btn btn-outline-primary w-100 py-3">
                    <i class="bi bi-book me-2"></i> Manage Courses
                </a>
            </div>
            <div class="col-md-3">
                <a href="${pageContext.request.contextPath}/admin/enrollments" class="btn btn-outline-primary w-100 py-3">
                    <i class="bi bi-person-check me-2"></i> Manage Enrollments
                </a>
            </div>
            <div class="col-md-3">
                <a href="${pageContext.request.contextPath}/admin/import-users" class="btn btn-outline-primary w-100 py-3">
                    <i class="bi bi-file-earmark-arrow-up me-2"></i> Import Users (CSV)
                </a>
            </div>
        </div>
    </div>
</div>

<!-- Chart.js CDN -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/4.4.1/chart.umd.js"></script>
<script>
document.addEventListener("DOMContentLoaded", function() {
    // Render Trend Chart
    const trendRaw = document.getElementById('trendData').value;
    if (trendRaw && trendRaw !== '[]') {
        const trendData = JSON.parse(trendRaw);
        const labels = trendData.map(d => d.date);
        const data = trendData.map(d => d.count);
        
        new Chart(document.getElementById('trendChart'), {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Số lượng nộp bài',
                    data: data,
                    borderColor: '#3b82f6',
                    backgroundColor: 'rgba(59, 130, 246, 0.1)',
                    borderWidth: 2,
                    fill: true,
                    tension: 0.3,
                    pointBackgroundColor: '#2563eb'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } },
                plugins: { legend: { display: false } }
            }
        });
    }

    // Render Top Courses Chart
    const topRaw = document.getElementById('topCoursesData').value;
    if (topRaw && topRaw !== '[]') {
        const topData = JSON.parse(topRaw);
        const topLabels = topData.map(d => d.courseCode);
        const topCounts = topData.map(d => d.count);
        
        // Generate nice colors
        const bgColors = ['#f59e0b', '#3b82f6', '#10b981', '#8b5cf6', '#ef4444'];
        
        new Chart(document.getElementById('topCoursesChart'), {
            type: 'bar',
            data: {
                labels: topLabels,
                datasets: [{
                    label: 'Số bài nộp',
                    data: topCounts,
                    backgroundColor: bgColors,
                    borderRadius: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } },
                plugins: { legend: { display: false } }
            }
        });
    }
});
</script>

<jsp:include page="/common/footer.jsp" />

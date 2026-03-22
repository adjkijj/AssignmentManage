<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="ct" uri="http://assignmentmanage/customtags"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Assignment Statistics" />
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-4">
    <h5><i class="bi bi-bar-chart-line me-2"></i>Thống kê: ${assignment.title}</h5>
    <a href="${pageContext.request.contextPath}/submissions?action=view&assignmentId=${assignment.assignmentId}"
       class="btn btn-sm btn-outline-primary">
        <i class="bi bi-arrow-left me-1"></i>Quay về
    </a>
</div>

<!-- Metric Cards -->
<div class="row g-3 mb-4">
    <div class="col-md-3">
        <div class="stat-card">
            <div class="stat-icon blue"><i class="bi bi-people"></i></div>
            <div class="stat-number">${stats.graded}/${stats.totalSubmitted}/${stats.totalEnrolled}</div>
            <div class="stat-label">Đã chấm / Đã nộp / Enrolled</div>
        </div>
    </div>
    <div class="col-md-2">
        <div class="stat-card">
            <div class="stat-icon purple"><i class="bi bi-calculator"></i></div>
            <div class="stat-number">${stats.mean}</div>
            <div class="stat-label">Điểm TB</div>
        </div>
    </div>
    <div class="col-md-2">
        <div class="stat-card">
            <div class="stat-icon green"><i class="bi bi-arrow-up-circle"></i></div>
            <div class="stat-number">${stats.max}</div>
            <div class="stat-label">Cao nhất</div>
        </div>
    </div>
    <div class="col-md-2">
        <div class="stat-card">
            <div class="stat-icon orange"><i class="bi bi-arrow-down-circle"></i></div>
            <div class="stat-number">${stats.min}</div>
            <div class="stat-label">Thấp nhất</div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="stat-card">
            <div class="stat-icon blue"><i class="bi bi-check-circle"></i></div>
            <div class="stat-number">${stats.passRate}%</div>
            <div class="stat-label">Tỉ lệ pass (≥5): ${stats.passCount}/${stats.graded}</div>
        </div>
    </div>
</div>

<div class="row g-3 mb-4">
    <div class="col-md-4">
        <div class="stat-card">
            <div class="stat-number">${stats.stdDev}</div>
            <div class="stat-label">Độ lệch chuẩn</div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="stat-card">
            <div class="stat-number">${stats.lateRate}%</div>
            <div class="stat-label">Tỉ lệ nộp muộn: ${stats.lateCount}/${stats.totalSubmitted}</div>
        </div>
    </div>
</div>

<!-- Chart.js Histogram -->
<div class="data-card mb-4">
    <div class="card-header"><h5><i class="bi bi-bar-chart me-2"></i>Phân phối điểm</h5></div>
    <div class="card-body">
        <canvas id="gradeHistogram" height="80"></canvas>
    </div>
</div>

<!-- Top 5 / Bottom 5 + Not Submitted -->
<div class="row g-3">
    <div class="col-md-4">
        <div class="data-card">
            <div class="card-header"><h6><i class="bi bi-trophy text-success me-1"></i>Top 5 cao nhất</h6></div>
            <div class="card-body p-0">
                <table class="table table-sm mb-0">
                    <c:set var="topCount" value="0" />
                    <c:forEach var="s" items="${allSubmissions}">
                        <c:if test="${s.grade != null && topCount < 5}">
                            <tr><td>${s.studentName}</td><td class="text-end fw-bold text-success">${s.grade}</td></tr>
                            <c:set var="topCount" value="${topCount + 1}" />
                        </c:if>
                    </c:forEach>
                </table>
            </div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="data-card">
            <div class="card-header"><h6><i class="bi bi-exclamation-triangle text-danger me-1"></i>Bottom 5 thấp nhất</h6></div>
            <div class="card-body p-0">
                <table class="table table-sm mb-0">
                    <c:set var="botCount" value="0" />
                    <c:set var="totalGraded" value="0" />
                    <c:forEach var="s" items="${allSubmissions}">
                        <c:if test="${s.grade != null}"><c:set var="totalGraded" value="${totalGraded + 1}" /></c:if>
                    </c:forEach>
                    <c:set var="skipCount" value="${totalGraded > 5 ? totalGraded - 5 : 0}" />
                    <c:set var="idx" value="0" />
                    <c:forEach var="s" items="${allSubmissions}">
                        <c:if test="${s.grade != null}">
                            <c:if test="${idx >= skipCount}">
                                <tr><td>${s.studentName}</td><td class="text-end fw-bold text-danger">${s.grade}</td></tr>
                            </c:if>
                            <c:set var="idx" value="${idx + 1}" />
                        </c:if>
                    </c:forEach>
                </table>
            </div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="data-card">
            <div class="card-header"><h6><i class="bi bi-x-circle text-warning me-1"></i>Chưa nộp (${notSubmittedStudents.size()})</h6></div>
            <div class="card-body p-0">
                <c:if test="${empty notSubmittedStudents}">
                    <p class="text-muted text-center p-2 mb-0">Tất cả đã nộp!</p>
                </c:if>
                <table class="table table-sm mb-0">
                    <c:forEach var="stu" items="${notSubmittedStudents}">
                        <tr><td>${stu.fullName}</td><td class="text-end text-muted">${stu.username}</td></tr>
                    </c:forEach>
                </table>
            </div>
        </div>
    </div>
</div>

<!-- Chart.js CDN + Script -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/4.4.1/chart.umd.js"></script>
<script>
var dist = [
    <c:forEach var="bucket" items="${stats.distribution}" varStatus="loop">
        ${bucket[0]}${!loop.last ? ',' : ''}
    </c:forEach>
];
var labels = ['0-1','1-2','2-3','3-4','4-5','5-6','6-7','7-8','8-9','9-10'];
var colors = dist.map(function(v, i) { return i < 5 ? '#ef4444' : (i < 7 ? '#f59e0b' : '#22c55e'); });
var meanVal = ${stats.mean};

var ctx = document.getElementById('gradeHistogram').getContext('2d');
new Chart(ctx, {
    type: 'bar',
    data: {
        labels: labels,
        datasets: [{
            label: 'Số sinh viên',
            data: dist,
            backgroundColor: colors,
            borderRadius: 4
        }]
    },
    options: {
        responsive: true,
        scales: {
            y: { beginAtZero: true, ticks: { stepSize: 1 } }
        },
        plugins: {
            annotation: {
                annotations: {
                    meanLine: {
                        type: 'line',
                        xMin: meanVal,
                        xMax: meanVal,
                        borderColor: '#3b82f6',
                        borderWidth: 2,
                        borderDash: [5, 5],
                        label: { content: 'TB: ' + meanVal, enabled: true }
                    }
                }
            }
        }
    }
});
</script>

<jsp:include page="/common/footer.jsp" />

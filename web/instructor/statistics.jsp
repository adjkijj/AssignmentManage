<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Thống kê điểm" />
</jsp:include>

<div class="mb-3">
    <a href="${pageContext.request.contextPath}/assignments" class="btn btn-outline-secondary btn-sm">
        <i class="bi bi-arrow-left"></i> Quay lại
    </a>
</div>

<div class="card shadow-sm border-0 mb-4">
    <div class="card-header bg-white">
        <h5 class="mb-0 text-primary">
            <i class="bi bi-bar-chart-fill me-2"></i>Thống kê phổ điểm: ${assignment.title}
        </h5>
    </div>
    <div class="card-body">
        <div class="row">
            <div class="col-md-8 mx-auto">
                <canvas id="gradeChart" height="250"></canvas>
            </div>
        </div>
    </div>
</div>

<!-- Include Chart.js from CDN -->
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
    document.addEventListener("DOMContentLoaded", function() {
        const assignmentId = ${assignment.assignmentId};
        fetch('${pageContext.request.contextPath}/statistics?action=api&assignmentId=' + assignmentId)
            .then(res => res.json())
            .then(data => {
                const ctx = document.getElementById('gradeChart').getContext('2d');
                new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels: Object.keys(data),
                        datasets: [{
                            label: 'Số lượng sinh viên',
                            data: Object.values(data),
                            backgroundColor: 'rgba(54, 162, 235, 0.6)',
                            borderColor: 'rgba(54, 162, 235, 1)',
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        scales: {
                            y: {
                                beginAtZero: true,
                                ticks: { stepSize: 1 }
                            }
                        },
                        plugins: {
                            legend: { display: false },
                            title: {
                                display: true,
                                text: 'Phân bố điểm số'
                            }
                        }
                    }
                });
            })
            .catch(error => console.error("Error fetching statistics:", error));
    });
</script>

<jsp:include page="/common/footer.jsp" />

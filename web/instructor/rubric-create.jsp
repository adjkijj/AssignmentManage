<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Cấu hình Rubric" />
</jsp:include>

<div class="mb-3">
    <a href="${pageContext.request.contextPath}/assignments?action=view&id=${assignment.assignmentId}" class="btn btn-outline-secondary btn-sm">
        <i class="bi bi-arrow-left"></i> Quay lại Bài tập
    </a>
</div>

<div class="card shadow-sm border-0 mb-4">
    <div class="card-header bg-white">
        <h4 class="mb-0 text-primary">Cấu hình Rubric Chấm điểm: ${assignment.title}</h4>
    </div>
    <div class="card-body">
        <form method="post" action="${pageContext.request.contextPath}/rubric">
            <input type="hidden" name="action" value="save">
            <input type="hidden" name="assignmentId" value="${assignment.assignmentId}">
            
            <div class="mb-4">
                <label for="totalPoints" class="form-label fw-bold">Tổng điểm của phần này (Quy đổi)</label>
                <div class="input-group" style="max-width:200px;">
                    <input type="number" step="0.5" class="form-control" id="totalPoints" name="totalPoints" value="${empty rubric ? 10.0 : rubric.totalPoints}" required>
                    <span class="input-group-text">điểm</span>
                </div>
            </div>
            
            <h5 class="fw-bold mb-3 border-bottom pb-2">Tiêu chí đánh giá</h5>
            
            <div id="criteria-container">
                <c:if test="${empty criteriaList}">
                    <!-- Default 1 empty row -->
                    <div class="row g-2 mb-3 criteria-row border p-3 rounded bg-light position-relative">
                        <button type="button" class="btn btn-sm btn-outline-danger position-absolute top-0 end-0 m-2 btn-remove-row" style="width:auto;"><i class="bi bi-x"></i></button>
                        <div class="col-md-3">
                            <label class="form-label">Tên tiêu chí</label>
                            <input type="text" class="form-control" name="criteriaName" required>
                        </div>
                        <div class="col-md-5">
                            <label class="form-label">Mô tả (Hướng dẫn chấm)</label>
                            <input type="text" class="form-control" name="description">
                        </div>
                        <div class="col-md-2">
                            <label class="form-label">Điểm tối đa</label>
                            <input type="number" step="0.5" class="form-control" name="maxPoints" value="10" required>
                        </div>
                        <div class="col-md-2">
                            <label class="form-label">Trọng số (%)</label>
                            <input type="number" step="1" class="form-control weight-input" name="weightPercent" value="100" required>
                        </div>
                    </div>
                </c:if>
                <c:if test="${not empty criteriaList}">
                    <c:forEach var="c" items="${criteriaList}">
                        <div class="row g-2 mb-3 criteria-row border p-3 rounded bg-light position-relative">
                            <button type="button" class="btn btn-sm btn-outline-danger position-absolute top-0 end-0 m-2 btn-remove-row" style="width:auto;"><i class="bi bi-x"></i></button>
                            <div class="col-md-3">
                                <label class="form-label">Tên tiêu chí</label>
                                <input type="text" class="form-control" name="criteriaName" value="${c.criteriaName}" required>
                            </div>
                            <div class="col-md-5">
                                <label class="form-label">Mô tả</label>
                                <input type="text" class="form-control" name="description" value="${c.description}">
                            </div>
                            <div class="col-md-2">
                                <label class="form-label">Điểm tối đa</label>
                                <input type="number" step="0.5" class="form-control" name="maxPoints" value="${c.maxPoints}" required>
                            </div>
                            <div class="col-md-2">
                                <label class="form-label">Trọng số (%)</label>
                                <input type="number" step="1" class="form-control weight-input" name="weightPercent" value="${c.weightPercent}" required>
                            </div>
                        </div>
                    </c:forEach>
                </c:if>
            </div>
            
            <div class="d-flex justify-content-between align-items-center mt-4">
                <button type="button" class="btn btn-outline-primary" id="btn-add-row">
                    <i class="bi bi-plus-circle"></i> Thêm tiêu chí
                </button>
                <div class="text-end">
                    <span id="weight-total" class="fw-bold me-3 text-success">Tổng trọng số: 100%</span>
                    <button type="submit" class="btn btn-primary" id="btn-save"><i class="bi bi-save"></i> Lưu Rubric</button>
                </div>
            </div>
        </form>
    </div>
</div>

<script>
    document.addEventListener("DOMContentLoaded", function() {
        const container = document.getElementById('criteria-container');
        const btnAdd = document.getElementById('btn-add-row');
        const weightTotalSpan = document.getElementById('weight-total');
        const btnSave = document.getElementById('btn-save');

        function updateTotal() {
            let total = 0;
            document.querySelectorAll('.weight-input').forEach(inp => {
                total += parseFloat(inp.value) || 0;
            });
            weightTotalSpan.innerText = 'Tổng trọng số: ' + total + '%';
            if (total !== 100) {
                weightTotalSpan.classList.replace('text-success', 'text-danger');
            } else {
                weightTotalSpan.classList.replace('text-danger', 'text-success');
            }
        }

        container.addEventListener('input', function(e) {
            if (e.target.classList.contains('weight-input')) {
                updateTotal();
            }
        });

        container.addEventListener('click', function(e) {
            let btn = e.target.closest('.btn-remove-row');
            if (btn) {
                if (container.querySelectorAll('.criteria-row').length > 1) {
                    btn.closest('.criteria-row').remove();
                    updateTotal();
                } else {
                    alert("Cần ít nhất 1 tiêu chí.");
                }
            }
        });

        btnAdd.addEventListener('click', function() {
            let firstRow = container.querySelector('.criteria-row');
            let newRow = firstRow.cloneNode(true);
            newRow.querySelectorAll('input').forEach(inp => inp.value = '');
            newRow.querySelector('.weight-input').value = '0';
            container.appendChild(newRow);
            updateTotal();
        });

        updateTotal(); // init run
        
        document.querySelector('form').addEventListener('submit', function(e) {
            let total = 0;
            document.querySelectorAll('.weight-input').forEach(inp => {
                total += parseFloat(inp.value) || 0;
            });
            if (Math.abs(total - 100) > 0.01) {
                e.preventDefault();
                alert("Tổng trọng số phải đúng bằng 100%. Hiện tại: " + total + "%");
            }
        });
    });
</script>

<jsp:include page="/common/footer.jsp" />

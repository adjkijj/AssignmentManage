<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Chấm điểm Rubric" />
</jsp:include>

<div class="mb-3">
    <a href="${pageContext.request.contextPath}/submissions?action=view&assignmentId=${submission.assignmentId}" class="btn btn-outline-secondary btn-sm">
        <i class="bi bi-arrow-left"></i> Quay lại Danh sách Nộp bài
    </a>
</div>

<div class="row">
    <div class="col-md-8">
        <div class="card shadow-sm border-0 mb-4">
            <div class="card-header bg-white">
                <h5 class="mb-0 text-primary">Chấm điểm: ${submission.studentName}</h5>
            </div>
            <div class="card-body">
                <form method="post" action="${pageContext.request.contextPath}/rubric">
                    <input type="hidden" name="action" value="saveGrade">
                    <input type="hidden" name="submissionId" value="${submission.submissionId}">
                    <input type="hidden" name="rubricId" value="${rubric.id}">
                    
                    <div class="table-responsive">
                        <table class="table table-bordered">
                            <thead class="table-light">
                                <tr>
                                    <th style="width: 25%">Tiêu chí</th>
                                    <th style="width: 35%">Hướng dẫn chấm</th>
                                    <th style="width: 10%">Trọng số</th>
                                    <th style="width: 15%">Điểm đạt</th>
                                    <th style="width: 15%">Nhận xét</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="c" items="${criteriaList}">
                                    <c:set var="pts" value="" />
                                    <c:set var="cmt" value="" />
                                    <c:if test="${not empty grades}">
                                        <c:forEach var="g" items="${grades}">
                                            <c:if test="${g.criteriaId == c.id}">
                                                <c:set var="pts" value="${g.pointsEarned}" />
                                                <c:set var="cmt" value="${g.comment}" />
                                            </c:if>
                                        </c:forEach>
                                    </c:if>
                                    
                                    <tr>
                                        <td><strong>${c.criteriaName}</strong></td>
                                        <td><small class="text-muted">${c.description}</small></td>
                                        <td>${c.weightPercent}%</td>
                                        <td>
                                            <input type="hidden" name="criteriaId" value="${c.id}">
                                            <div class="input-group input-group-sm">
                                                <input type="number" step="0.5" max="${c.maxPoints}" min="0" 
                                                       class="form-control pts-input" name="points_${c.id}" 
                                                       value="${pts}" required data-weight="${c.weightPercent}" data-max="${c.maxPoints}">
                                                <span class="input-group-text">/ ${c.maxPoints}</span>
                                            </div>
                                        </td>
                                        <td>
                                            <textarea class="form-control form-control-sm" name="comment_${c.id}" rows="2" placeholder="Tùy chọn">${cmt}</textarea>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                            <tfoot class="table-light fw-bold">
                                <tr>
                                    <td colspan="3" class="text-end">Tổng điểm Rubric (dự kiến):</td>
                                    <td colspan="2" class="text-success fs-5" id="live-total">0.00 / ${rubric.totalPoints}</td>
                                </tr>
                            </tfoot>
                        </table>
                    </div>
                    
                    <div class="text-end mt-3">
                        <button type="submit" class="btn btn-primary"><i class="bi bi-check-circle"></i> Lưu điểm & Hoàn tất</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    
    <div class="col-md-4">
        <div class="card shadow-sm border-0 mb-4 h-100">
            <div class="card-header bg-white">
                <h5 class="mb-0"><i class="bi bi-info-circle"></i> Thông tin Bài nộp</h5>
            </div>
            <div class="card-body">
                <p><strong>Ngày nộp:</strong> <fmt:formatDate value="${submission.submittedAt}" pattern="dd/MM/yyyy HH:mm" /></p>
                <p><strong>File đính kèm:</strong></p>
                <c:if test="${not empty submission.attachmentPath}">
                    <a href="${pageContext.request.contextPath}/uploads/submissions/${submission.attachmentPath}" class="btn btn-sm btn-outline-primary" target="_blank">
                        <i class="bi bi-download"></i> Tải về
                    </a>
                </c:if>
                <c:if test="${empty submission.attachmentPath}">
                    <span class="text-muted">Không có file</span>
                </c:if>
                <hr>
                <p><strong>Comments học sinh:</strong></p>
                <p class="text-muted"><small>${empty submission.comments ? 'Không có' : submission.comments}</small></p>
            </div>
        </div>
    </div>
</div>

<script>
    document.addEventListener("DOMContentLoaded", function() {
        const ptsInputs = document.querySelectorAll('.pts-input');
        const liveTotal = document.getElementById('live-total');
        const totalPoints = ${rubric.totalPoints};
        
        function calculateTotal() {
            let percentage = 0;
            ptsInputs.forEach(inp => {
                let p = parseFloat(inp.value) || 0;
                let w = parseFloat(inp.dataset.weight) || 0;
                let m = parseFloat(inp.dataset.max) || 1;
                percentage += (p / m) * (w / 100.0);
            });
            let finalGrade = percentage * totalPoints;
            liveTotal.innerText = finalGrade.toFixed(2) + ' / ' + totalPoints;
        }
        
        ptsInputs.forEach(inp => inp.addEventListener('input', calculateTotal));
        calculateTotal();
    });
</script>

<jsp:include page="/common/footer.jsp" />

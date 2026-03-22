<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Đánh giá bằng Rubric" />
</jsp:include>

<div class="mb-3">
    <a href="${pageContext.request.contextPath}/assignments" class="btn btn-outline-secondary btn-sm">
        <i class="bi bi-arrow-left"></i> Quay lại Danh sách Bài tập
    </a>
</div>

<div class="card shadow-sm border-0 mb-4">
    <div class="card-header bg-white">
        <h5 class="mb-0 text-primary"><i class="bi bi-table me-2"></i> Chi tiết đánh giá Rubric</h5>
    </div>
    <div class="card-body">
        <c:if test="${empty rubric}">
            <div class="alert alert-warning">Bài tập này chưa có cấu hình Rubric.</div>
        </c:if>
        
        <c:if test="${not empty rubric}">
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
                            <c:set var="pts" value="0.0" />
                            <c:set var="cmt" value="-" />
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
                                <td class="fw-bold text-primary">
                                    ${pts} / ${c.maxPoints}
                                </td>
                                <td>
                                    <small>${empty cmt ? '-' : cmt}</small>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                    <tfoot class="table-light fw-bold">
                        <tr>
                            <td colspan="3" class="text-end">Tổng điểm Rubric (quy đổi):</td>
                            <td colspan="2" class="text-success fs-5">
                                ${submission.grade} / ${rubric.totalPoints}
                            </td>
                        </tr>
                    </tfoot>
                </table>
            </div>
            <div class="mt-3">
                <p><strong>Đánh giá chung:</strong> ${submission.feedback}</p>
            </div>
        </c:if>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="ct" uri="http://assignmentmanage/customtags"%>
<%@taglib prefix="prev" tagdir="/WEB-INF/tags"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Submission History" />
</jsp:include>

<div class="data-card">
    <div class="card-header">
        <h5><i class="bi bi-clock-history me-2"></i>Submission History: ${assignment.title}</h5>
        <a href="${pageContext.request.contextPath}/submissions?action=grades" class="btn btn-sm btn-outline-primary">
            <i class="bi bi-arrow-left me-1"></i> Back to Grades
        </a>
    </div>
    <div class="card-body">
        <c:if test="${empty submissionHistory}">
            <div class="empty-state">
                <i class="bi bi-inbox"></i>
                <h5>No Submissions</h5>
                <p>You haven't submitted this assignment yet.</p>
            </div>
        </c:if>

        <c:if test="${not empty submissionHistory}">
            <div class="table-responsive">
                <table class="table">
                    <thead>
                        <tr>
                            <th>Version</th>
                            <th>File</th>
                            <th>Submitted</th>
                            <th>Status</th>
                            <th>Grade</th>
                            <th>Feedback</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="h" items="${submissionHistory}">
                            <tr>
                                <td><span class="badge bg-secondary">v${h.version}</span></td>
                                <td>
                                    <span class="text-muted">${h.filePath}</span>
                                    <c:if test="${not empty h.filePath}">
                                        <div class="mt-2">
                                            <prev:filePreview filePath="${h.filePath}" height="150px" />
                                        </div>
                                    </c:if>
                                </td>
                                <td><ct:dateFormat date="${h.submittedAt}" /></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${h.status == 'graded'}">
                                            <span class="badge bg-success">Graded</span>
                                        </c:when>
                                        <c:when test="${h.status == 'late'}">
                                            <span class="badge bg-warning text-dark">Late</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-primary">Submitted</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${h.grade != null}">
                                            <span class="badge ${h.grade >= 5 ? 'bg-success' : 'bg-danger'}">${h.grade}/10</span>
                                            <a href="${pageContext.request.contextPath}/rubric?action=view&submissionId=${h.submissionId}" class="btn btn-sm btn-link p-0 ms-1" title="Chi tiết Rubric"><i class="bi bi-ui-checks"></i></a>
                                        </c:when>
                                        <c:otherwise><span class="text-muted">-</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty h.feedback}">${h.feedback}</c:when>
                                        <c:otherwise><span class="text-muted">-</span></c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:if>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />

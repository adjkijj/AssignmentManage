<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="ct" uri="http://assignmentmanage/customtags"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="My Grades" />
</jsp:include>

<div class="data-card">
    <div class="card-header">
        <h5><i class="bi bi-award me-2"></i>Submission Grades</h5>
    </div>
    <div class="card-body">
        <c:if test="${empty submissions}">
            <div class="empty-state">
                <i class="bi bi-inbox"></i>
                <h5>No Submissions Yet</h5>
                <p>Submit your assignments to view your grades here.</p>
            </div>
        </c:if>

        <c:if test="${not empty submissions}">
            <c:forEach var="s" items="${submissions}">
                <div class="data-card mb-3" style="border-left: 4px solid ${s.grade != null ? (s.grade >= 8 ? '#198754' : s.grade >= 5 ? '#ffc107' : '#dc3545') : '#6c757d'};">
                    <div class="card-body">
                        <div class="row">
                            <!-- Left: Assignment info -->
                            <div class="col-md-3">
                                <h6 class="mb-1">${s.assignmentTitle}</h6>
                                <small class="text-muted">
                                    v${s.version} · <ct:dateFormat date="${s.submittedAt}" />
                                </small>
                                <br>
                                <c:choose>
                                    <c:when test="${s.status == 'graded'}">
                                        <span class="badge bg-success">Graded</span>
                                    </c:when>
                                    <c:when test="${s.status == 'late'}">
                                        <span class="badge bg-warning text-dark">Late</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-primary">Submitted</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <%-- Feature 5: Grade display --%>
                            <div class="col-md-2 text-center">
                                <c:choose>
                                    <c:when test="${s.grade != null}">
                                        <div style="font-size: 2rem; font-weight: 700; line-height: 1;
                                             color: ${s.grade >= 8 ? '#198754' : s.grade >= 5 ? '#e6a817' : '#dc3545'};">
                                            ${s.grade}
                                        </div>
                                        <small class="text-muted">/ 10</small>
                                    </c:when>
                                    <c:otherwise>
                                        <div style="font-size: 1.5rem; color: #6c757d;">—</div>
                                        <small class="text-muted">Pending</small>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <%-- Feature 5: Formatted feedback --%>
                            <div class="col-md-5">
                                <c:choose>
                                    <c:when test="${not empty s.feedback}">
                                        <% pageContext.setAttribute("newLineChar", "\n"); %>
                                        <c:set var="feedbackLines" value="${fn:split(s.feedback, newLineChar)}" />
                                        <div style="font-size: 0.9rem;">
                                            <c:forEach var="line" items="${feedbackLines}">
                                                <c:set var="trimLine" value="${fn:trim(line)}" />
                                                <c:choose>
                                                    <c:when test="${fn:startsWith(trimLine, '[+]') || fn:startsWith(trimLine, '+')}">
                                                        <div class="text-success mb-1">
                                                            <i class="bi bi-check-circle-fill me-1"></i>${trimLine}
                                                        </div>
                                                    </c:when>
                                                    <c:when test="${fn:startsWith(trimLine, '[-]') || (fn:startsWith(trimLine, '-') && fn:length(trimLine) > 1)}">
                                                        <div class="text-danger mb-1">
                                                            <i class="bi bi-exclamation-circle-fill me-1"></i>${trimLine}
                                                        </div>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:if test="${fn:length(trimLine) > 0}">
                                                            <div class="mb-1">${trimLine}</div>
                                                        </c:if>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:forEach>
                                        </div>
                                    </c:when>
                                    <c:when test="${s.grade != null}">
                                        <span class="text-muted fst-italic">
                                            <i class="bi bi-chat-left-text me-1"></i>Giảng viên chưa để lại nhận xét
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-muted fst-italic">Đang chờ chấm điểm...</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <%-- Graded at + action --%>
                            <div class="col-md-2 text-end">
                                <c:if test="${s.gradedAt != null}">
                                    <small class="text-muted d-block">
                                        Chấm lúc:<br><ct:dateFormat date="${s.gradedAt}" pattern="dd/MM/yyyy HH:mm" />
                                    </small>
                                </c:if>
                                <a href="${pageContext.request.contextPath}/submissions?action=history&assignmentId=${s.assignmentId}"
                                   class="btn btn-sm btn-outline-primary mt-1" title="View History">
                                    <i class="bi bi-clock-history"></i>
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </c:if>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />

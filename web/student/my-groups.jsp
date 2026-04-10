<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="My Groups" />
</jsp:include>

<c:if test="${empty myGroups}">
    <div class="data-card">
        <div class="card-body">
            <div class="empty-state">
                <i class="bi bi-people"></i>
                <h5>Chưa có nhóm</h5>
                <p>Bạn chưa được thêm vào nhóm nào. Hãy liên hệ giảng viên.</p>
            </div>
        </div>
    </div>
</c:if>

<c:if test="${not empty myGroups}">
    <c:forEach var="group" items="${myGroups}">
        <div class="data-card mb-3">
            <div class="card-header">
                <h5><i class="bi bi-people-fill me-2"></i>${group.groupName}</h5>
                <span class="badge bg-primary">${assignmentTitles[group.assignmentId]}</span>
            </div>
            <div class="card-body">
                <p class="text-muted mb-2">
                    <i class="bi bi-person-lines-fill me-1"></i>
                    <strong>${group.members.size()}</strong> thành viên
                </p>
                <div class="table-responsive">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>Họ tên</th>
                                <th>Username</th>
                                <th>Email</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="member" items="${group.members}" varStatus="loop">
                                <tr class="${member.userId == sessionScope.currentUser.userId ? 'table-active' : ''}">
                                    <td>${loop.count}</td>
                                    <td>
                                        <strong>${member.fullName}</strong>
                                        <c:if test="${member.userId == sessionScope.currentUser.userId}">
                                            <span class="badge bg-success ms-1">Bạn</span>
                                        </c:if>
                                    </td>
                                    <td class="text-muted">${member.username}</td>
                                    <td class="text-muted">${member.email}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

                <%-- Display student's own grade and feedback --%>
                <hr>
                <div class="mt-3">
                    <h6 class="mb-3"><i class="bi bi-award me-2"></i>Điểm cá nhân của bạn</h6>
                    <c:set var="mySub" value="${myGrades[group.groupId]}" />
                    
                    <c:choose>
                        <c:when test="${mySub != null}">
                            <div class="row">
                                <div class="col-md-3 border-end">
                                    <div class="text-muted mb-1 small">Điểm số</div>
                                    <c:choose>
                                        <c:when test="${mySub.grade != null}">
                                            <div style="font-size: 2rem; font-weight: 700; line-height: 1;
                                                 color: ${mySub.grade >= 8 ? '#198754' : mySub.grade >= 5 ? '#e6a817' : '#dc3545'};">
                                                ${mySub.grade}
                                            </div>
                                            <small class="text-muted">/ 10</small>
                                        </c:when>
                                        <c:otherwise>
                                            <div style="font-size: 1.5rem; color: #6c757d;">—</div>
                                            <span class="badge bg-primary mt-1">Đã nộp, chờ chấm</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="col-md-9">
                                    <div class="text-muted mb-1 small">Nhận xét từ giáo viên</div>
                                    <c:choose>
                                        <c:when test="${not empty mySub.feedback}">
                                            <div class="p-3 bg-light rounded" style="font-size: 0.95rem;">
                                                <% pageContext.setAttribute("newLineChar", "\n"); %>
                                                <c:forEach var="line" items="${fn:split(mySub.feedback, newLineChar)}">
                                                    <c:set var="trimLine" value="${fn:trim(line)}" />
                                                    <c:if test="${fn:length(trimLine) > 0}">
                                                        <div>${trimLine}</div>
                                                    </c:if>
                                                </c:forEach>
                                            </div>
                                        </c:when>
                                        <c:when test="${mySub.grade != null}">
                                            <span class="text-muted fst-italic">Không có nhận xét.</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted fst-italic">Chưa có nhận xét.</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="alert alert-secondary py-2 mb-0 border-0 d-inline-block">
                                <i class="bi bi-info-circle me-2"></i>Bạn chưa nộp bài tập này.
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </c:forEach>
</c:if>

<jsp:include page="/common/footer.jsp" />

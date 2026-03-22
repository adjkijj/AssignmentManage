<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="ct" uri="http://assignmentmanage/customtags"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Browse & Enroll in Classes" />
</jsp:include>

<c:if test="${not empty sessionScope.success}">
    <div class="alert alert-success">${sessionScope.success}</div>
    <c:remove var="success" scope="session" />
</c:if>
<c:if test="${not empty sessionScope.error}">
    <div class="alert alert-danger">${sessionScope.error}</div>
    <c:remove var="error" scope="session" />
</c:if>

<div class="data-card">
    <div class="card-header">
        <h5><i class="bi bi-mortarboard me-2"></i>Available Classes</h5>
    </div>
    <div class="card-body">
        <c:if test="${empty openClasses}">
            <div class="empty-state">
                <i class="bi bi-check-circle"></i>
                <h5>No Available Classes</h5>
                <p>You are enrolled in all active classes, or no classes are currently open.</p>
            </div>
        </c:if>

        <c:forEach var="cls" items="${openClasses}">
            <div class="d-flex justify-content-between align-items-center p-3 mb-3"
                 style="background: var(--body-bg); border-radius: 10px; border-left: 4px solid var(--primary);">
                <div>
                    <div class="d-flex align-items-center gap-2 mb-1">
                        <strong>${cls.courseName}</strong>
                        <span class="badge bg-primary">${cls.courseCode}</span>
                        <c:if test="${not empty cls.subjectName}">
                            <span class="badge bg-secondary">${cls.subjectName}</span>
                        </c:if>
                    </div>
                    <small class="text-muted">
                        <i class="bi bi-person me-1"></i>${cls.instructorName}
                        <c:if test="${not empty cls.semesterName}">
                            &bull; <i class="bi bi-calendar me-1"></i>${cls.semesterName}
                        </c:if>
                        &bull; <i class="bi bi-people me-1"></i>${cls.studentCount} / ${cls.maxStudents} students
                    </small>
                    <c:if test="${not empty cls.description}">
                        <br><small class="text-muted">${cls.description}</small>
                    </c:if>
                </div>
                <div>
                    <c:choose>
                        <c:when test="${cls.maxStudents > 0 && cls.studentCount >= cls.maxStudents}">
                            <span class="badge bg-danger">Full</span>
                        </c:when>
                        <c:otherwise>
                            <form method="post" action="${pageContext.request.contextPath}/student/enroll"
                                  onsubmit="return confirm('Enroll in ${cls.courseName}?');">
                                <input type="hidden" name="courseId" value="${cls.courseId}">
                                <button type="submit" class="btn btn-sm btn-gradient">
                                    <i class="bi bi-plus-circle me-1"></i> Enroll
                                </button>
                            </form>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </c:forEach>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />

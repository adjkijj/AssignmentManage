<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="ct" uri="http://assignmentmanage/customtags"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Các lớp của tôi (Managed Classes)" />
</jsp:include>

<div class="data-card">
    <div class="card-header d-flex justify-content-between align-items-center">
        <h5><i class="bi bi-journal-bookmark me-2"></i>My Teaching Classes</h5>
        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-sm btn-outline-primary">
            <i class="bi bi-grid-1x2 me-1"></i> Dashboard
        </a>
    </div>
    
    <div class="card-body">
        <c:if test="${empty myCourses}">
            <div class="empty-state">
                <i class="bi bi-inbox fs-1 text-muted"></i>
                <h5 class="mt-3">No Classes Found</h5>
                <p class="text-muted">You are not currently assigned to teach any active classes.</p>
            </div>
        </c:if>

        <c:forEach var="course" items="${myCourses}">
            <div class="d-flex justify-content-between align-items-center p-3 mb-3"
                 style="background: var(--body-bg); border-radius: 10px; border-left: 4px solid var(--primary);">
                <div>
                    <div class="d-flex align-items-center gap-2 mb-1">
                        <strong class="fs-5">${course.courseName}</strong>
                        <span class="badge bg-primary">${course.courseCode}</span>
                        <c:if test="${not empty course.subjectName}">
                            <span class="badge bg-secondary">${course.subjectName}</span>
                        </c:if>
                    </div>
                    <small class="text-muted d-block mb-2">
                        <i class="bi bi-people me-1"></i> Students: ${course.studentCount} / ${course.maxStudents}
                        <c:if test="${not empty course.semesterName}">
                            &bull; <i class="bi bi-calendar me-1"></i> ${course.semesterName}
                        </c:if>
                    </small>
                    <c:if test="${not empty course.description}">
                        <p class="text-muted small mb-0">${course.description}</p>
                    </c:if>
                </div>
                <div class="d-flex flex-column gap-2 text-end">
                    <a href="${pageContext.request.contextPath}/course-materials?action=manage&courseId=${course.courseId}" 
                       class="btn btn-sm btn-outline-info">
                        <i class="bi bi-folder2-open me-1"></i> Manage Materials
                    </a>
                    <a href="${pageContext.request.contextPath}/assignments?courseId=${course.courseId}" 
                       class="btn btn-sm btn-outline-primary">
                        <i class="bi bi-journal-text me-1"></i> Assignments
                    </a>
                    <a href="${pageContext.request.contextPath}/gradebook?courseId=${course.courseId}" 
                       class="btn btn-sm btn-success">
                        <i class="bi bi-table me-1"></i> Gradebook
                    </a>
                </div>
            </div>
        </c:forEach>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />

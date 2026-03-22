<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="ct" uri="http://assignmentmanage/customtags"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Course Materials" />
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <h5 class="mb-0">Materials: ${course.courseName}</h5>
        <small class="text-muted">${course.courseCode}</small>
    </div>
    <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-outline-primary btn-sm">
        <i class="bi bi-arrow-left me-1"></i> Back
    </a>
</div>

<c:if test="${empty materials}">
    <div class="data-card">
        <div class="card-body">
            <div class="empty-state">
                <i class="bi bi-folder-x"></i>
                <h5>No Materials Available</h5>
                <p>Your instructor has not uploaded any materials for this course yet.</p>
            </div>
        </div>
    </div>
</c:if>

<!-- Group by week using a simple approach -->
<c:set var="currentWeek" value="-1" />
<c:forEach var="m" items="${materials}">
    <c:if test="${m.weekNumber != currentWeek}">
        <c:if test="${currentWeek != -1}">
            </div></div>
        </c:if>
        <c:set var="currentWeek" value="${m.weekNumber}" />
        <div class="data-card mb-4">
            <div class="card-header">
                <h5>
                    <i class="bi bi-calendar-week me-2"></i>
                    <c:choose>
                        <c:when test="${m.weekNumber > 0}">Week ${m.weekNumber}</c:when>
                        <c:otherwise>General</c:otherwise>
                    </c:choose>
                    <c:if test="${not empty m.topic}">
                        <small class="text-muted ms-2">— ${m.topic}</small>
                    </c:if>
                </h5>
            </div>
            <div class="card-body">
    </c:if>

    <!-- Material Card -->
    <div class="d-flex justify-content-between align-items-center p-3 mb-2"
         style="background: var(--body-bg); border-radius: 8px;">
        <div>
            <c:choose>
                <c:when test="${m.materialType == 'PDF'}"><i class="bi bi-file-earmark-pdf text-danger fs-4 me-2"></i></c:when>
                <c:when test="${m.materialType == 'SLIDE'}"><i class="bi bi-file-earmark-slides text-primary fs-4 me-2"></i></c:when>
                <c:when test="${m.materialType == 'VIDEO'}"><i class="bi bi-play-circle text-success fs-4 me-2"></i></c:when>
                <c:otherwise><i class="bi bi-file-earmark text-secondary fs-4 me-2"></i></c:otherwise>
            </c:choose>
            <strong>${m.title}</strong>
            <span class="badge bg-secondary ms-2">${m.materialType}</span>
            <c:if test="${not empty m.description}">
                <br><small class="text-muted ms-4">${m.description}</small>
            </c:if>
            <br><small class="text-muted ms-4">
                Uploaded: <ct:dateFormat date="${m.uploadedAt}" pattern="dd/MM/yyyy" />
                by ${m.uploaderName}
            </small>
        </div>
        <div>
            <c:if test="${not empty m.filePath}">
                <a href="${pageContext.request.contextPath}/uploads/materials/${m.filePath}"
                   class="btn btn-sm btn-gradient" target="_blank">
                    <i class="bi bi-download me-1"></i> Download
                </a>
            </c:if>
            <c:if test="${not empty m.externalUrl}">
                <a href="${m.externalUrl}" class="btn btn-sm btn-outline-info" target="_blank">
                    <i class="bi bi-link-45deg me-1"></i> Open Link
                </a>
            </c:if>
        </div>
    </div>
</c:forEach>

<c:if test="${currentWeek != -1}">
    </div></div>
</c:if>

<jsp:include page="/common/footer.jsp" />

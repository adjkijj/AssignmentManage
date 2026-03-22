<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="ct" uri="http://assignmentmanage/customtags"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Manage Materials" />
</jsp:include>

<c:if test="${not empty sessionScope.success}">
    <div class="alert alert-success">${sessionScope.success}</div>
    <c:remove var="success" scope="session" />
</c:if>
<c:if test="${not empty sessionScope.error}">
    <div class="alert alert-danger">${sessionScope.error}</div>
    <c:remove var="error" scope="session" />
</c:if>

<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <h5 class="mb-0">Materials: ${course.courseName}</h5>
        <small class="text-muted">${course.courseCode}</small>
    </div>
    <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-outline-primary btn-sm">
        <i class="bi bi-arrow-left me-1"></i> Back
    </a>
</div>

<!-- Upload Form -->
<div class="data-card mb-4">
    <div class="card-header">
        <h5><i class="bi bi-cloud-upload me-2"></i>Upload Material</h5>
    </div>
    <div class="card-body">
        <form method="post" action="${pageContext.request.contextPath}/course-materials"
              enctype="multipart/form-data">
            <input type="hidden" name="action" value="upload">
            <input type="hidden" name="courseId" value="${course.courseId}">
            <div class="row g-3">
                <div class="col-md-4">
                    <label for="title" class="form-label">Title *</label>
                    <input type="text" class="form-control" id="title" name="title" required
                           placeholder="e.g. Week 1 Slides">
                </div>
                <div class="col-md-4">
                    <label for="topic" class="form-label">Topic</label>
                    <input type="text" class="form-control" id="topic" name="topic"
                           placeholder="e.g. Servlet Basics">
                </div>
                <div class="col-md-2">
                    <label for="weekNumber" class="form-label">Week #</label>
                    <input type="number" class="form-control" id="weekNumber" name="weekNumber"
                           min="1" max="20" placeholder="1">
                </div>
                <div class="col-md-2">
                    <label for="materialType" class="form-label">Type</label>
                    <select class="form-select" id="materialType" name="materialType">
                        <option value="SLIDE">Slide</option>
                        <option value="PDF">PDF</option>
                        <option value="VIDEO">Video</option>
                        <option value="OTHER">Other</option>
                    </select>
                </div>
                <div class="col-md-6">
                    <label for="description" class="form-label">Description</label>
                    <input type="text" class="form-control" id="description" name="description"
                           placeholder="Brief description">
                </div>
                <div class="col-md-4">
                    <label for="file" class="form-label">File (PDF, Word, ZIP, Images)</label>
                    <input type="file" class="form-control" id="file" name="file">
                </div>
                <div class="col-md-2 d-flex align-items-end">
                    <button type="submit" class="btn btn-gradient w-100">
                        <i class="bi bi-upload me-1"></i> Upload
                    </button>
                </div>
                <div class="col-md-6">
                    <label for="externalUrl" class="form-label">Or External URL</label>
                    <input type="url" class="form-control" id="externalUrl" name="externalUrl"
                           placeholder="https://...">
                </div>
            </div>
        </form>
    </div>
</div>

<!-- Materials List -->
<div class="data-card">
    <div class="card-header">
        <h5><i class="bi bi-folder me-2"></i>All Materials (${materials.size()})</h5>
    </div>
    <div class="card-body p-0">
        <c:if test="${empty materials}">
            <div class="empty-state">
                <i class="bi bi-folder-x"></i>
                <h5>No Materials Yet</h5>
                <p>Upload your first course material above.</p>
            </div>
        </c:if>
        <c:if test="${not empty materials}">
            <div class="table-responsive">
                <table class="table">
                    <thead>
                        <tr>
                            <th>Week</th>
                            <th>Title</th>
                            <th>Type</th>
                            <th>Topic</th>
                            <th>Visibility</th>
                            <th>Uploaded</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="m" items="${materials}">
                            <tr>
                                <td>
                                    <c:if test="${m.weekNumber > 0}">
                                        <span class="badge bg-primary">W${m.weekNumber}</span>
                                    </c:if>
                                </td>
                                <td><strong>${m.title}</strong>
                                    <c:if test="${not empty m.description}">
                                        <br><small class="text-muted">${m.description}</small>
                                    </c:if>
                                </td>
                                <td><span class="badge bg-secondary">${m.materialType}</span></td>
                                <td>${m.topic}</td>
                                <td>
                                    <span class="badge ${m.isVisible ? 'bg-success' : 'bg-warning'}">
                                        ${m.isVisible ? 'Visible' : 'Hidden'}
                                    </span>
                                </td>
                                <td><ct:dateFormat date="${m.uploadedAt}" pattern="dd/MM/yyyy" /></td>
                                <td>
                                    <c:if test="${not empty m.filePath}">
                                        <a href="${pageContext.request.contextPath}/uploads/materials/${m.filePath}"
                                           class="btn btn-sm btn-outline-primary" title="Download" target="_blank">
                                            <i class="bi bi-download"></i>
                                        </a>
                                    </c:if>
                                    <c:if test="${not empty m.externalUrl}">
                                        <a href="${m.externalUrl}" class="btn btn-sm btn-outline-info"
                                           title="External Link" target="_blank">
                                            <i class="bi bi-link-45deg"></i>
                                        </a>
                                    </c:if>
                                    <form method="post" action="${pageContext.request.contextPath}/course-materials"
                                          style="display: inline;">
                                        <input type="hidden" name="action" value="toggle">
                                        <input type="hidden" name="id" value="${m.materialId}">
                                        <input type="hidden" name="courseId" value="${m.courseId}">
                                        <button type="submit" class="btn btn-sm btn-outline-warning" title="Toggle Visibility">
                                            <i class="bi bi-eye${m.isVisible ? '-slash' : ''}"></i>
                                        </button>
                                    </form>
                                    <form method="post" action="${pageContext.request.contextPath}/course-materials"
                                          style="display: inline;" onsubmit="return confirm('Delete this material?');">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="id" value="${m.materialId}">
                                        <input type="hidden" name="courseId" value="${m.courseId}">
                                        <button type="submit" class="btn btn-sm btn-outline-danger" title="Delete">
                                            <i class="bi bi-trash"></i>
                                        </button>
                                    </form>
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

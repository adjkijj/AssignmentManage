<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="ct" uri="http://assignmentmanage/customtags"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Assignments" />
</jsp:include>

<c:choose>
    <c:when test="${not empty assignment}">
        <!-- Assignment Detail View (unchanged) -->
        <div class="data-card">
            <div class="card-header">
                <h5><i class="bi bi-journal-text me-2"></i>${assignment.title}</h5>
                <div>
                    <a href="${pageContext.request.contextPath}/assignments?action=edit&id=${assignment.assignmentId}"
                       class="btn btn-sm btn-outline-primary me-1">
                        <i class="bi bi-pencil"></i> Edit
                    </a>
                    <a href="${pageContext.request.contextPath}/rubric?action=edit&assignmentId=${assignment.assignmentId}"
                       class="btn btn-sm btn-outline-info me-1">
                        <i class="bi bi-ui-checks"></i> Rubric
                    </a>
                    <a href="${pageContext.request.contextPath}/submissions?action=view&assignmentId=${assignment.assignmentId}"
                       class="btn btn-sm btn-gradient me-1">
                        <i class="bi bi-file-earmark-check"></i> Grade Submissions
                    </a>
                    <a href="${pageContext.request.contextPath}/submissions?action=stats&assignmentId=${assignment.assignmentId}"
                       class="btn btn-sm btn-info text-white">
                        <i class="bi bi-bar-chart"></i> Stats
                    </a>
                </div>
            </div>
            <div class="card-body">
                <p><strong>Course:</strong> ${assignment.courseName}</p>
                <p><strong>Deadline:</strong> <ct:dateFormat date="${assignment.deadline}" pattern="dd/MM/yyyy HH:mm" /></p>
                <c:if test="${not empty assignment.softDeadline}">
                    <p><strong>Soft Deadline:</strong>
                        <span class="text-warning"><ct:dateFormat date="${assignment.softDeadline}" pattern="dd/MM/yyyy HH:mm" /></span>
                        <small class="text-muted">(late after this)</small>
                    </p>
                </c:if>
                <c:if test="${not empty assignment.hardDeadline}">
                    <p><strong>Hard Deadline:</strong>
                        <span class="text-danger"><ct:dateFormat date="${assignment.hardDeadline}" pattern="dd/MM/yyyy HH:mm" /></span>
                        <small class="text-muted">(blocked after this)</small>
                    </p>
                </c:if>
                <p><strong>Description:</strong></p>
                <p>${assignment.description}</p>
                <c:if test="${not empty assignment.attachmentPath}">
                    <p><i class="bi bi-paperclip"></i> <strong>Attachment:</strong> ${assignment.attachmentPath}</p>
                </c:if>

                <hr>
                <h6>Submissions (${submissions.size()})</h6>
                <c:if test="${empty submissions}">
                    <p class="text-muted">No submissions yet.</p>
                </c:if>
                <c:if test="${not empty submissions}">
                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Student</th><th>Version</th><th>Submitted</th>
                                    <th>Status</th><th>File</th><th>Grade</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="s" items="${submissions}">
                                    <tr>
                                        <td>${s.studentName}</td>
                                        <td><span class="badge bg-secondary">v${s.version}</span></td>
                                        <td><ct:dateFormat date="${s.submittedAt}" /></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${s.status == 'graded'}"><span class="badge bg-success">Graded</span></c:when>
                                                <c:when test="${s.status == 'late'}"><span class="badge bg-warning text-dark">Late</span></c:when>
                                                <c:otherwise><span class="badge bg-primary">Submitted</span></c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>${s.filePath}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${s.grade != null}">
                                                    <span class="badge bg-success">${s.grade}/10</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-warning text-dark">Pending</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:if>

                <a href="${pageContext.request.contextPath}/assignments" class="btn btn-outline-primary mt-3">
                    <i class="bi bi-arrow-left me-1"></i> Back to List
                </a>
            </div>
        </div>

        <!-- Comments Section -->
        <div class="data-card">
            <div class="card-header">
                <h5><i class="bi bi-chat-dots me-2"></i>Comments</h5>
            </div>
            <div class="card-body">
                <c:forEach var="comment" items="${comments}">
                    <div class="comment-item" style="margin-left: ${comment.parentCommentId != null ? '30px' : '0'}; border-left: ${comment.parentCommentId != null ? '3px solid var(--primary-color)' : 'none'}; padding-left: ${comment.parentCommentId != null ? '15px' : '0'};">
                        <div class="d-flex justify-content-between">
                            <span class="comment-user">${comment.userName}</span>
                            <span class="comment-date"><ct:dateFormat date="${comment.createdAt}" /></span>
                        </div>
                        <p class="comment-text">${comment.content}</p>
                        <button class="btn btn-sm btn-outline-secondary mb-2" type="button"
                                onclick="document.getElementById('replyForm${comment.commentId}').classList.toggle('d-none')">
                            <i class="bi bi-reply me-1"></i>Reply
                        </button>
                        <div id="replyForm${comment.commentId}" class="d-none mb-3">
                            <form method="post" action="${pageContext.request.contextPath}/comments">
                                <input type="hidden" name="action" value="add">
                                <input type="hidden" name="assignmentId" value="${assignment.assignmentId}">
                                <input type="hidden" name="parentId" value="${comment.commentId}">
                                <div class="mb-2">
                                    <textarea class="form-control form-control-sm" name="content" rows="2"
                                              placeholder="Reply to ${comment.userName}..." required></textarea>
                                </div>
                                <button type="submit" class="btn btn-primary btn-sm">
                                    <i class="bi bi-send me-1"></i>Reply
                                </button>
                            </form>
                        </div>
                    </div>
                </c:forEach>
                <c:if test="${empty comments}">
                    <p class="text-muted text-center">No comments yet.</p>
                </c:if>
                <form method="post" action="${pageContext.request.contextPath}/comments" class="mt-3">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="assignmentId" value="${assignment.assignmentId}">
                    <div class="mb-3">
                        <textarea class="form-control" name="content" rows="3"
                                  placeholder="Add a comment..." required></textarea>
                    </div>
                    <button type="submit" class="btn btn-primary btn-sm">
                        <i class="bi bi-send me-1"></i> Post Comment
                    </button>
                </form>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <!-- Feature 2: Filter Form -->
        <div class="data-card mb-3">
            <div class="card-body">
                <form method="get" action="${pageContext.request.contextPath}/assignments" class="row g-2 align-items-end">
                    <div class="col-md-3">
                        <label class="form-label small text-muted">Course</label>
                        <select name="courseId" class="form-select form-select-sm">
                            <option value="">Tất cả course</option>
                            <c:forEach var="course" items="${instructorCourses}">
                                <option value="${course.courseId}" ${filterCourseId == course.courseId ? 'selected' : ''}>
                                    ${course.courseCode} — ${course.courseName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label small text-muted">Trạng thái</label>
                        <select name="gradingStatus" class="form-select form-select-sm">
                            <option value="">Tất cả</option>
                            <option value="pending" ${filterGradingStatus == 'pending' ? 'selected' : ''}>Có bài chờ chấm</option>
                            <option value="all_graded" ${filterGradingStatus == 'all_graded' ? 'selected' : ''}>Đã chấm hết</option>
                            <option value="near_deadline" ${filterGradingStatus == 'near_deadline' ? 'selected' : ''}>Sắp deadline</option>
                            <option value="past_deadline" ${filterGradingStatus == 'past_deadline' ? 'selected' : ''}>Đã qua deadline</option>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label small text-muted">Tìm kiếm</label>
                        <input type="text" name="keyword" class="form-control form-control-sm"
                               placeholder="Tên bài tập..." value="${filterKeyword}">
                    </div>
                    <div class="col-md-3 d-flex gap-1">
                        <button type="submit" class="btn btn-sm btn-gradient">
                            <i class="bi bi-search me-1"></i>Lọc
                        </button>
                        <a href="${pageContext.request.contextPath}/assignments" class="btn btn-sm btn-outline-secondary">
                            <i class="bi bi-x-circle me-1"></i>Xóa filter
                        </a>
                    </div>
                </form>
                <c:if test="${not empty filterCourseId || not empty filterGradingStatus || not empty filterKeyword}">
                    <div class="mt-2">
                        <small class="text-muted">
                            <i class="bi bi-funnel me-1"></i>Đang lọc:
                            <c:if test="${not empty filterCourseId}"> Course ID: ${filterCourseId}</c:if>
                            <c:if test="${not empty filterGradingStatus}"> | Trạng thái: ${filterGradingStatus}</c:if>
                            <c:if test="${not empty filterKeyword}"> | Từ khóa: "${filterKeyword}"</c:if>
                        </small>
                    </div>
                </c:if>
            </div>
        </div>

        <!-- Assignments List -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="mb-0">All Assignments <c:if test="${not empty assignments}"><span class="badge bg-secondary">${assignments.size()}</span></c:if></h5>
            <a href="${pageContext.request.contextPath}/assignments?action=create" class="btn btn-gradient">
                <i class="bi bi-plus-circle me-1"></i> Create Assignment
            </a>
        </div>

        <c:if test="${empty assignments}">
            <div class="data-card">
                <div class="card-body">
                    <div class="empty-state">
                        <i class="bi bi-journal-plus"></i>
                        <h5>No Assignments Yet</h5>
                        <p>Create your first assignment to get started.</p>
                        <a href="${pageContext.request.contextPath}/assignments?action=create" class="btn btn-gradient mt-2">
                            <i class="bi bi-plus-circle me-1"></i> Create Assignment
                        </a>
                    </div>
                </div>
            </div>
        </c:if>

        <c:forEach var="a" items="${assignments}">
            <div class="assignment-card">
                <div class="d-flex justify-content-between align-items-start">
                    <div style="flex: 1;">
                        <div class="d-flex align-items-center gap-2 mb-1">
                            <span class="course-badge">${a.courseName}</span>
                            <c:if test="${a.pendingCount > 0}">
                                <span class="badge bg-danger">
                                    <i class="bi bi-hourglass-split me-1"></i>${a.pendingCount} chờ chấm
                                </span>
                            </c:if>
                        </div>
                        <h5>
                            <a href="${pageContext.request.contextPath}/assignments?action=detail&id=${a.assignmentId}"
                               style="color: inherit;">${a.title}</a>
                        </h5>
                        <div class="meta">
                            <span class="me-3"><i class="bi bi-calendar-event"></i>
                                Deadline: <ct:dateFormat date="${a.deadline}" /></span>
                            <span><i class="bi bi-clock-history"></i>
                                Created: <ct:dateFormat date="${a.createdAt}" /></span>
                        </div>

                        <!-- Feature 3: Submission Rate Progress Bar -->
                        <c:set var="stats" value="${submissionStatsMap[a.assignmentId]}" />
                        <c:if test="${stats != null && stats.totalEnrolled > 0}">
                            <div class="mt-2">
                                <div class="progress" style="height: 8px; border-radius: 4px;">
                                    <div class="progress-bar bg-success" style="width: ${stats.onTimePercent}%"
                                         title="${stats.submittedOnTime} đúng hạn"></div>
                                    <div class="progress-bar bg-warning" style="width: ${stats.latePercent}%"
                                         title="${stats.submittedLate} muộn"></div>
                                    <div class="progress-bar bg-danger" style="width: ${stats.notSubmittedPercent}%"
                                         title="${stats.notSubmitted} chưa nộp"></div>
                                </div>
                                <small class="text-muted">
                                    ${stats.totalSubmitted}/${stats.totalEnrolled} nộp
                                    <c:if test="${stats.submittedLate > 0}"> (${stats.submittedLate} muộn)</c:if>
                                    <c:if test="${stats.notSubmitted > 0}">, ${stats.notSubmitted} chưa nộp</c:if>
                                </small>
                            </div>
                        </c:if>
                    </div>
                    <div class="d-flex gap-1">
                        <a href="${pageContext.request.contextPath}/submissions?action=view&assignmentId=${a.assignmentId}"
                           class="btn btn-sm btn-outline-primary" title="Grade Submissions">
                            <i class="bi bi-file-earmark-check"></i>
                        </a>
                        <a href="${pageContext.request.contextPath}/assignments?action=edit&id=${a.assignmentId}"
                           class="btn btn-sm btn-outline-primary" title="Edit">
                            <i class="bi bi-pencil"></i>
                        </a>
                        <a href="${pageContext.request.contextPath}/assignments?action=duplicateForm&id=${a.assignmentId}"
                           class="btn btn-sm btn-outline-secondary" title="Duplicate">
                            <i class="bi bi-copy"></i>
                        </a>
                        <a href="${pageContext.request.contextPath}/assignments?action=delete&id=${a.assignmentId}"
                           class="btn btn-sm btn-outline-danger" title="Delete"
                           onclick="return confirm('Are you sure you want to delete this assignment?');">
                            <i class="bi bi-trash"></i>
                        </a>
                    </div>
                </div>
            </div>
        </c:forEach>
    </c:otherwise>
</c:choose>

<jsp:include page="/common/footer.jsp" />

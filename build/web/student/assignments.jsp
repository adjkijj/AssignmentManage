<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="ct" uri="http://assignmentmanage/customtags"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="My Assignments" />
</jsp:include>

<c:choose>
    <c:when test="${not empty assignment}">
        <!-- Assignment Detail View (unchanged) -->
        <div class="data-card">
            <div class="card-header">
                <h5><i class="bi bi-journal-text me-2"></i>${assignment.title}</h5>
                <span class="badge bg-primary">${assignment.courseName}</span>
            </div>
            <div class="card-body">
                <p><strong>Deadline:</strong>
                    <span class="${assignment.deadline.time < System.currentTimeMillis() ? 'text-danger' : 'text-success'}">
                        <ct:dateFormat date="${assignment.deadline}" pattern="dd/MM/yyyy HH:mm" />
                    </span>
                </p>
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

                <!-- Submission Status -->
                <c:choose>
                    <c:when test="${not empty submission}">
                        <div class="alert ${submission.isLate ? 'alert-warning' : 'alert-success'}">
                            <i class="bi bi-check-circle me-2"></i>
                            <strong>Submitted (v${submission.version})</strong> on <ct:dateFormat date="${submission.submittedAt}" />
                            <c:if test="${submission.isLate}">
                                <span class="badge bg-warning text-dark ms-1">Late</span>
                            </c:if>
                            <c:if test="${submission.grade != null}">
                                <br><strong>Grade:</strong> ${submission.grade}/10 - ${submission.feedback}
                            </c:if>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-danger">
                            <i class="bi bi-exclamation-triangle me-2"></i> Not yet submitted.
                        </div>
                    </c:otherwise>
                </c:choose>

                <a href="${pageContext.request.contextPath}/submissions?action=submit&assignmentId=${assignment.assignmentId}"
                   class="btn btn-gradient">
                    <i class="bi bi-cloud-arrow-up me-1"></i>
                    ${not empty submission ? 'Submit New Version' : 'Submit Assignment'}
                </a>
                <c:if test="${not empty submission}">
                    <a href="${pageContext.request.contextPath}/submissions?action=history&assignmentId=${assignment.assignmentId}"
                       class="btn btn-outline-primary ms-1">
                        <i class="bi bi-clock-history me-1"></i> History
                    </a>
                </c:if>
                <a href="${pageContext.request.contextPath}/assignments" class="btn btn-outline-primary ms-2">
                    <i class="bi bi-arrow-left me-1"></i> Back to List
                </a>
            </div>
        </div>

        <!-- Comments Section (Threaded) -->
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

        <!-- ============== Feature 6: Filter Form ============== -->
        <div class="data-card mb-3">
            <div class="card-body py-3">
                <form method="get" action="${pageContext.request.contextPath}/assignments" class="row g-2 align-items-end">
                    <input type="hidden" name="action" value="list">
                    <div class="col-md-3">
                        <label class="form-label small fw-semibold mb-1">Môn học</label>
                        <select class="form-select form-select-sm" name="courseId">
                            <option value="">Tất cả môn</option>
                            <c:forEach var="course" items="${enrolledCourses}">
                                <option value="${course.courseId}" ${filterCourseId == course.courseId ? 'selected' : ''}>
                                    ${course.courseCode} — ${course.courseName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label small fw-semibold mb-1">Trạng thái</label>
                        <select class="form-select form-select-sm" name="status">
                            <option value="">Tất cả</option>
                            <option value="unsubmitted" ${filterStatus == 'unsubmitted' ? 'selected' : ''}>Chưa nộp</option>
                            <option value="submitted" ${filterStatus == 'submitted' ? 'selected' : ''}>Đã nộp</option>
                            <option value="graded" ${filterStatus == 'graded' ? 'selected' : ''}>Đã có điểm</option>
                            <option value="near_deadline" ${filterStatus == 'near_deadline' ? 'selected' : ''}>Sắp hết hạn</option>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label small fw-semibold mb-1">Tìm kiếm</label>
                        <input type="text" class="form-control form-control-sm" name="keyword"
                               value="${filterKeyword}" placeholder="Tên bài tập...">
                    </div>
                    <div class="col-md-3 d-flex gap-1">
                        <button type="submit" class="btn btn-primary btn-sm flex-fill">
                            <i class="bi bi-search me-1"></i>Lọc
                        </button>
                        <a href="${pageContext.request.contextPath}/assignments" class="btn btn-outline-secondary btn-sm">
                            <i class="bi bi-x-lg"></i>
                        </a>
                    </div>
                </form>
            </div>
        </div>

        <!-- Result count -->
        <c:if test="${not empty filterCourseId || not empty filterStatus || not empty filterKeyword}">
            <div class="alert alert-info py-2 mb-3">
                <i class="bi bi-funnel me-1"></i>Hiển thị <strong>${fn:length(assignments)}</strong> bài tập
            </div>
        </c:if>

        <!-- ============== Assignments List with Badges (Feature 1) ============== -->
        <c:if test="${empty assignments}">
            <div class="data-card">
                <div class="card-body">
                    <div class="empty-state">
                        <i class="bi bi-journal-x"></i>
                        <h5>Không tìm thấy bài tập</h5>
                        <p>Không có bài tập nào phù hợp với bộ lọc. Hãy thử thay đổi tiêu chí lọc.</p>
                    </div>
                </div>
            </div>
        </c:if>

        <c:forEach var="a" items="${assignments}">
            <div class="assignment-card">
                <div class="d-flex justify-content-between align-items-start">
                    <div>
                        <span class="course-badge">${a.courseName}</span>

                        <%-- Feature 1: Status Badge --%>
                        <c:choose>
                            <c:when test="${a.submissionStatus == 'graded'}">
                                <span class="badge bg-success ms-1">
                                    <i class="bi bi-check-circle-fill me-1"></i>Đã có điểm (${a.gradeValue}/10)
                                </span>
                            </c:when>
                            <c:when test="${a.submissionStatus == 'late'}">
                                <span class="badge bg-warning text-dark ms-1">
                                    <i class="bi bi-clock-fill me-1"></i>Nộp muộn
                                </span>
                            </c:when>
                            <c:when test="${a.submissionStatus == 'submitted'}">
                                <span class="badge bg-primary ms-1">
                                    <i class="bi bi-cloud-check-fill me-1"></i>Đã nộp - chờ chấm
                                </span>
                            </c:when>
                            <c:when test="${a.deadline.time < System.currentTimeMillis() && a.submissionStatus == null}">
                                <span class="badge bg-secondary ms-1">
                                    <i class="bi bi-x-circle-fill me-1"></i>Quá hạn
                                </span>
                            </c:when>
                            <c:when test="${a.deadline.time - System.currentTimeMillis() < 172800000 && a.deadline.time > System.currentTimeMillis() && a.submissionStatus == null}">
                                <span class="badge bg-warning text-dark ms-1">
                                    <i class="bi bi-exclamation-triangle-fill me-1"></i>Sắp hết hạn
                                </span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge bg-danger ms-1">
                                    <i class="bi bi-dash-circle me-1"></i>Chưa nộp
                                </span>
                            </c:otherwise>
                        </c:choose>

                        <h5>
                            <a href="${pageContext.request.contextPath}/assignments?action=detail&id=${a.assignmentId}"
                               style="color: inherit;">
                                ${a.title}
                            </a>
                        </h5>
                        <div class="meta">
                            <span><i class="bi bi-calendar-event"></i>
                                Deadline: <ct:dateFormat date="${a.deadline}" /></span>
                        </div>
                    </div>
                    <div>
                        <a href="${pageContext.request.contextPath}/assignments?action=detail&id=${a.assignmentId}"
                           class="btn btn-outline-primary btn-sm">
                            <i class="bi bi-eye me-1"></i> Xem
                        </a>
                        <c:if test="${a.submissionStatus != 'graded'}">
                            <a href="${pageContext.request.contextPath}/submissions?action=submit&assignmentId=${a.assignmentId}"
                               class="btn btn-gradient btn-sm ms-1">
                                <i class="bi bi-cloud-arrow-up me-1"></i> Nộp bài
                            </a>
                        </c:if>
                    </div>
                </div>
            </div>
        </c:forEach>
    </c:otherwise>
</c:choose>

<jsp:include page="/common/footer.jsp" />

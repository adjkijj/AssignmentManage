<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="ct" uri="http://assignmentmanage/customtags"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Manage Groups" />
</jsp:include>

<c:if test="${not empty sessionScope.success}">
    <div class="alert alert-success">${sessionScope.success}</div>
    <c:remove var="success" scope="session" />
</c:if>
<c:if test="${not empty sessionScope.error}">
    <div class="alert alert-danger">${sessionScope.error}</div>
    <c:remove var="error" scope="session" />
</c:if>

<c:choose>
    <c:when test="${not empty assignment}">
        <!-- Groups for a specific assignment -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h5 class="mb-0">Groups for: ${assignment.title}</h5>
                <small class="text-muted">${assignment.courseName}</small>
            </div>
            <a href="${pageContext.request.contextPath}/groups" class="btn btn-outline-primary btn-sm">
                <i class="bi bi-arrow-left me-1"></i> Back to Assignments
            </a>
        </div>

        <!-- Create New Group -->
        <div class="data-card mb-4">
            <div class="card-header">
                <h5><i class="bi bi-plus-circle me-2"></i>Create New Group</h5>
            </div>
            <div class="card-body">
                <form method="post" action="${pageContext.request.contextPath}/groups">
                    <input type="hidden" name="action" value="create">
                    <input type="hidden" name="assignmentId" value="${assignment.assignmentId}">
                    <div class="row g-3">
                        <div class="col-md-8">
                            <input type="text" class="form-control" name="groupName"
                                   placeholder="Enter group name (e.g., Group A)" required>
                        </div>
                        <div class="col-md-4">
                            <button type="submit" class="btn btn-gradient w-100">
                                <i class="bi bi-plus-circle me-1"></i> Create Group
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <!-- Existing Groups -->
        <c:if test="${empty groups}">
            <div class="data-card">
                <div class="card-body">
                    <div class="empty-state">
                        <i class="bi bi-people"></i>
                        <h5>No Groups Yet</h5>
                        <p>Create your first group above to start organizing students.</p>
                    </div>
                </div>
            </div>
        </c:if>

        <c:forEach var="group" items="${groups}">
            <div class="data-card mb-3">
                <div class="card-header">
                    <h5><i class="bi bi-people me-2"></i>${group.groupName}</h5>
                    <div class="d-flex align-items-center gap-2">
                        <span class="badge bg-secondary">${group.members.size()} member(s)</span>
                        <a href="${pageContext.request.contextPath}/groups?action=gradeMembers&groupId=${group.groupId}"
                           class="btn btn-sm btn-outline-warning" title="Chấm điểm từng thành viên">
                            <i class="bi bi-pencil-square me-1"></i>Grade Members
                        </a>
                    </div>
                </div>
                <div class="card-body">
                    <!-- Current Members -->
                    <c:if test="${not empty group.members}">
                        <div class="table-responsive mb-3">
                            <table class="table">
                                <thead>
                                    <tr>
                                        <th>Student</th>
                                        <th>Username</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="member" items="${group.members}">
                                        <tr>
                                            <td><strong>${member.fullName}</strong></td>
                                            <td class="text-muted">${member.username}</td>
                                            <td>
                                                <form method="post" action="${pageContext.request.contextPath}/groups"
                                                      style="display: inline;"
                                                      onsubmit="return confirm('Remove this student from the group?');">
                                                    <input type="hidden" name="action" value="removeMember">
                                                    <input type="hidden" name="assignmentId" value="${assignment.assignmentId}">
                                                    <input type="hidden" name="groupId" value="${group.groupId}">
                                                    <input type="hidden" name="studentId" value="${member.userId}">
                                                    <button type="submit" class="btn btn-sm btn-outline-danger">
                                                        <i class="bi bi-person-dash me-1"></i> Remove
                                                    </button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:if>
                    <c:if test="${empty group.members}">
                        <p class="text-muted mb-3">No members yet. Add students below.</p>
                    </c:if>

                    <!-- Add Members Form (Multi-Select) - only show students not yet in any group -->
                    <c:set var="hasAvailableStudents" value="false" />
                    <c:forEach var="e" items="${enrolledStudents}">
                        <c:if test="${not assignedStudentIds.contains(e.studentId)}">
                            <c:set var="hasAvailableStudents" value="true" />
                        </c:if>
                    </c:forEach>

                    <c:choose>
                        <c:when test="${hasAvailableStudents}">
                            <form method="post" action="${pageContext.request.contextPath}/groups" class="mt-2">
                                <input type="hidden" name="action" value="addMembers">
                                <input type="hidden" name="assignmentId" value="${assignment.assignmentId}">
                                <input type="hidden" name="groupId" value="${group.groupId}">
                                <label class="form-label fw-bold"><i class="bi bi-person-plus me-1"></i>Add Students (select multiple):</label>
                                <div style="max-height: 200px; overflow-y: auto; border: 1px solid var(--border-color); border-radius: 8px; padding: 10px; margin-bottom: 10px;">
                                    <c:forEach var="e" items="${enrolledStudents}">
                                        <c:if test="${not assignedStudentIds.contains(e.studentId)}">
                                            <div class="form-check">
                                                <input class="form-check-input" type="checkbox"
                                                       name="studentId" value="${e.studentId}" id="stu_${group.groupId}_${e.studentId}">
                                                <label class="form-check-label" for="stu_${group.groupId}_${e.studentId}">
                                                    ${e.studentName}
                                                </label>
                                            </div>
                                        </c:if>
                                    </c:forEach>
                                </div>
                                <div class="d-flex gap-2">
                                    <button type="button" class="btn btn-sm btn-outline-secondary"
                                            onclick="this.closest('form').querySelectorAll('input[type=checkbox]').forEach(c=>c.checked=true)">
                                        Select All
                                    </button>
                                    <button type="button" class="btn btn-sm btn-outline-secondary"
                                            onclick="this.closest('form').querySelectorAll('input[type=checkbox]').forEach(c=>c.checked=false)">
                                        Deselect All
                                    </button>
                                    <button type="submit" class="btn btn-sm btn-success">
                                        <i class="bi bi-person-plus me-1"></i> Add Selected Members
                                    </button>
                                </div>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <p class="text-muted mt-2"><i class="bi bi-check-circle me-1"></i>All enrolled students have been assigned to a group.</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </c:forEach>
    </c:when>
    <c:otherwise>
        <!-- Assignment Selection -->
        <div class="data-card">
            <div class="card-header">
                <h5><i class="bi bi-people me-2"></i>Select Assignment for Group Management</h5>
            </div>
            <div class="card-body">
                <c:if test="${empty assignments}">
                    <div class="empty-state">
                        <i class="bi bi-journal-x"></i>
                        <h5>No Assignments</h5>
                        <p>Create an assignment first to manage groups.</p>
                    </div>
                </c:if>

                <c:forEach var="a" items="${assignments}">
                    <div class="d-flex justify-content-between align-items-center p-3 mb-2"
                         style="background: var(--body-bg); border-radius: 8px;">
                        <div>
                            <strong>${a.title}</strong>
                            <br><small class="text-muted">${a.courseName} &bull;
                                Deadline: <ct:dateFormat date="${a.deadline}" pattern="dd/MM/yyyy HH:mm" /></small>
                        </div>
                        <a href="${pageContext.request.contextPath}/groups?assignmentId=${a.assignmentId}"
                           class="btn btn-sm btn-gradient">
                            <i class="bi bi-people me-1"></i> Manage Groups
                        </a>
                    </div>
                </c:forEach>
            </div>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/common/footer.jsp" />

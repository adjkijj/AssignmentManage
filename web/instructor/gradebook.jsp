<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@taglib prefix="ct" uri="http://assignmentmanage/customtags" %>
            <jsp:include page="/common/header.jsp">
                <jsp:param name="title" value="Gradebook" />
            </jsp:include>

            <!-- Course Picker -->
            <div class="data-card mb-3">
                <div class="card-header">
                    <h5><i class="bi bi-table me-2"></i>Gradebook</h5>
                    <c:if test="${not empty selectedCourseId}">
                        <a href="${pageContext.request.contextPath}/export?type=grades&courseId=${selectedCourseId}&format=csv"
                            class="btn btn-sm btn-outline-success">
                            <i class="bi bi-download me-1"></i>Xuất CSV
                        </a>
                    </c:if>
                </div>
                <div class="card-body">
                    <form method="get" action="${pageContext.request.contextPath}/gradebook"
                        class="row g-2 align-items-end">
                        <div class="col-md-5">
                            <label class="form-label small">Chọn course</label>
                            <select name="courseId" class="form-select form-select-sm" onchange="this.form.submit()">
                                <option value="">-- Chọn course --</option>
                                <c:forEach var="c" items="${instructorCourses}">
                                    <option value="${c.courseId}" ${selectedCourseId==c.courseId ? 'selected' : '' }>
                                        ${c.courseCode} — ${c.courseName}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <button type="submit" class="btn btn-sm btn-gradient">
                                <i class="bi bi-arrow-right"></i>
                            </button>
                        </div>
                    </form>
                </div>
            </div>

            <c:if test="${not empty gbStudents && not empty gbAssignments}">
                <div class="data-card">
                    <div class="card-body p-0">
                        <div class="table-responsive" style="max-height: 70vh;">
                            <table class="table table-bordered table-sm mb-0" style="font-size: 0.85rem;">
                                <thead class="table-dark" style="position: sticky; top: 0; z-index: 2;">
                                    <tr>
                                        <th
                                            style="position: sticky; left: 0; z-index: 3; background: orange; min-width: 160px;">
                                            Student</th>
                                        <c:forEach var="a" items="${gbAssignments}">
                                            <th class="text-center" style="min-width: 100px; white-space: nowrap;">
                                                ${a.title}<br>
                                                <small class="fw-normal text-light">
                                                    <ct:dateFormat date="${a.deadline}" pattern="dd/MM" />
                                                </small>
                                            </th>
                                        </c:forEach>
                                        <th class="text-center" style="min-width: 70px;">TB</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="s" items="${gbStudents}">
                                        <tr>
                                            <td
                                                style="position: sticky; left: 0; z-index: 1; background: var(--card-bg);">
                                                <strong>${s.fullName}</strong><br>
                                                <small class="text-muted">${s.username}</small>
                                            </td>
                                            <c:forEach var="a" items="${gbAssignments}">
                                                <c:set var="cell" value="${gbMatrix[s.id][a.id]}" />
                                                <c:choose>
                                                    <c:when test="${cell != null && cell.grade != null}">
                                                        <c:choose>
                                                            <c:when test="${cell.grade >= 8}">
                                                                <td class="text-center"
                                                                    style="background: #d1fae5; color: #065f46;">
                                                                    <strong>${cell.grade}</strong>
                                                                </td>
                                                            </c:when>
                                                            <c:when test="${cell.grade >= 5}">
                                                                <td class="text-center"
                                                                    style="background: #fef9c3; color: #854d0e;">
                                                                    <strong>${cell.grade}</strong>
                                                                </td>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <td class="text-center"
                                                                    style="background: #fecaca; color: #991b1b;">
                                                                    <strong>${cell.grade}</strong>
                                                                </td>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:when>
                                                    <c:when test="${cell != null && cell.isLate}">
                                                        <td class="text-center" style="background: #fed7aa;">
                                                            <small>Muộn</small>
                                                        </td>
                                                    </c:when>
                                                    <c:when test="${cell != null}">
                                                        <td class="text-center" style="background: #dbeafe;">
                                                            <small>Chờ chấm</small>
                                                        </td>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <td class="text-center" style="background: #fef2f2;">
                                                            <small class="text-muted">—</small>
                                                        </td>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:forEach>
                                            <td class="text-center fw-bold">
                                                <c:choose>
                                                    <c:when test="${studentAvgs[s.id] >= 0}">${studentAvgs[s.id]}
                                                    </c:when>
                                                    <c:otherwise>—</c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    <!-- Average row -->
                                    <tr class="table-secondary fw-bold">
                                        <td style="position: sticky; left: 0; z-index: 1; background: #e5e7eb;">TB Lớp
                                        </td>
                                        <c:forEach var="a" items="${gbAssignments}">
                                            <td class="text-center">
                                                <c:choose>
                                                    <c:when test="${assignmentAvgs[a.id] >= 0}">${assignmentAvgs[a.id]}
                                                    </c:when>
                                                    <c:otherwise>—</c:otherwise>
                                                </c:choose>
                                            </td>
                                        </c:forEach>
                                        <td></td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <!-- Legend -->
                <div class="mt-2">
                    <small class="text-muted">
                        <span
                            style="display: inline-block; width: 12px; height: 12px; background: #d1fae5; border: 1px solid #ccc; border-radius: 2px; color: white;"></span>
                        <span style="color: white;">≥8</span> &nbsp;
                        <span
                            style="display: inline-block; width: 12px; height: 12px; background: #fef9c3; border: 1px solid #ccc; border-radius: 2px;color: white;"></span>
                        <span style="color: white;">≥5</span> &nbsp;
                        <span
                            style="display: inline-block; width: 12px; height: 12px; background: #fecaca; border: 1px solid #ccc; border-radius: 2px;color: white"></span>
                        <5 &nbsp; <span
                            style="display: inline-block; width: 12px; height: 12px; background: #dbeafe; border: 1px solid #ccc; border-radius: 2px;color: white">
                            </span> <span style="color: white;">Chờ chấm</span> &nbsp;
                            <span
                                style="display: inline-block; width: 12px; height: 12px; background: #fed7aa; border: 1px solid #ccc; border-radius: 2px;color: white"></span>
                            <span style="color: white;">Muộn</span> &nbsp;
                            <span
                                style="display: inline-block; width: 12px; height: 12px; background: #fef2f2; border: 1px solid #ccc; border-radius: 2px;color: white"></span>
                            <span style="color: white;">Chưa nộp</span> &nbsp;
                    </small>
                </div>
            </c:if>

            <c:if test="${not empty selectedCourseId && empty gbStudents}">
                <div class="data-card">
                    <div class="card-body text-center text-muted">
                        <i class="bi bi-people" style="font-size: 3rem;"></i>
                        <h5>Chưa có student enroll</h5>
                    </div>
                </div>
            </c:if>

            <jsp:include page="/common/footer.jsp" />
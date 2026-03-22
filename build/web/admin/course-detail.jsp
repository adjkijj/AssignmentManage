<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="ct" uri="http://assignmentmanage/customtags"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Course Detail" />
</jsp:include>

<div class="mb-4">
    <a href="${pageContext.request.contextPath}/admin/courses" class="btn btn-outline-secondary btn-sm">
        <i class="bi bi-arrow-left"></i> Quay lại Danh sách Khóa học
    </a>
</div>

<div class="card mb-4 shadow-sm border-0">
    <div class="card-body">
        <h3 class="card-title text-primary"><i class="bi bi-book me-2"></i> ${detail.course.courseCode} - ${detail.course.courseName}</h3>
        <p class="card-text text-muted mb-1">
            <strong>Giảng viên:</strong> ${detail.course.instructorName} | 
            <strong>Kỳ học:</strong> ${detail.course.semesterName} | 
            <strong>Môn học:</strong> ${detail.course.subjectName}
        </p>
        <p class="card-text">${detail.course.description}</p>
    </div>
</div>

<ul class="nav nav-tabs" id="myTab" role="tablist">
  <li class="nav-item" role="presentation">
    <button class="nav-link active fw-bold" id="students-tab" data-bs-toggle="tab" data-bs-target="#students" type="button" role="tab" aria-controls="students" aria-selected="true">
        <i class="bi bi-people me-1"></i> Danh sách sinh viên (${detail.enrolledStudents.size()})
    </button>
  </li>
  <li class="nav-item" role="presentation">
    <button class="nav-link fw-bold" id="assignments-tab" data-bs-toggle="tab" data-bs-target="#assignments" type="button" role="tab" aria-controls="assignments" aria-selected="false">
        <i class="bi bi-file-earmark-text me-1"></i> Danh sách bài tập (${detail.assignmentStats.size()})
    </button>
  </li>
</ul>

<div class="tab-content border border-top-0 bg-white p-4 rounded-bottom shadow-sm mb-5" id="myTabContent">
  <!-- Students Tab -->
  <div class="tab-pane fade show active" id="students" role="tabpanel" aria-labelledby="students-tab">
      <c:if test="${empty detail.enrolledStudents}">
          <div class="text-center text-muted my-4">Chưa có sinh viên nào đăng ký khóa học này.</div>
      </c:if>
      <c:if test="${not empty detail.enrolledStudents}">
          <div class="table-responsive">
              <table class="table table-hover align-middle">
                  <thead class="table-light">
                      <tr>
                          <th>Username</th>
                          <th>Họ và tên</th>
                          <th>Email</th>
                          <th>Ngày đăng ký</th>
                      </tr>
                  </thead>
                  <tbody>
                      <c:forEach var="stu" items="${detail.enrolledStudents}">
                          <tr>
                              <td>${stu.username}</td>
                              <td><strong>${stu.fullName}</strong></td>
                              <td>${stu.email}</td>
                              <td><ct:dateFormat date="${stu.createdAt}" pattern="dd/MM/yyyy" /></td>
                          </tr>
                      </c:forEach>
                  </tbody>
              </table>
          </div>
      </c:if>
  </div>
  
  <!-- Assignments Tab -->
  <div class="tab-pane fade" id="assignments" role="tabpanel" aria-labelledby="assignments-tab">
      <c:if test="${empty detail.assignmentStats}">
          <div class="text-center text-muted my-4">Chưa có bài tập nào cho khóa học này.</div>
      </c:if>
      <c:if test="${not empty detail.assignmentStats}">
          <div class="table-responsive">
              <table class="table table-hover align-middle">
                  <thead class="table-light">
                      <tr>
                          <th>Bài tập</th>
                          <th>Hạn chót</th>
                          <th>Số bài nộp</th>
                      </tr>
                  </thead>
                  <tbody>
                      <c:forEach var="stat" items="${detail.assignmentStats}">
                          <tr>
                              <td>
                                  <strong>${stat.assignment.title}</strong>
                              </td>
                              <td><ct:dateFormat date="${stat.assignment.deadline}" pattern="dd/MM/yyyy HH:mm" /></td>
                              <td><span class="badge bg-info text-dark rounded-pill">${stat.submissionCount} bài</span></td>
                          </tr>
                      </c:forEach>
                  </tbody>
              </table>
          </div>
      </c:if>
  </div>
</div>

<jsp:include page="/common/footer.jsp" />

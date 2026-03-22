<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="ct" uri="http://assignmentmanage/customtags"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Thông báo" />
</jsp:include>

<div class="row">
    <div class="col-lg-8 mx-auto">
        <div class="card shadow-sm border-0">
            <div class="card-header bg-white">
                <h4 class="mb-0 text-primary"><i class="bi bi-bell-fill me-2"></i>Thông báo của tôi</h4>
            </div>
            <div class="card-body p-0 bg-light">
                <c:forEach var="a" items="${announcements}">
                    <div class="card m-3 shadow-none border">
                        <div class="card-body">
                            <div class="d-flex w-100 justify-content-between align-items-center mb-2">
                                <h5 class="card-title text-primary mb-0">${a.title}</h5>
                                <small class="text-muted"><i class="bi bi-clock me-1"></i><ct:dateFormat date="${a.createdAt}"/></small>
                            </div>
                            <h6 class="card-subtitle mb-3 text-muted">
                                <c:choose>
                                    <c:when test="${a.courseId == 0}">
                                        <span class="badge bg-danger text-white me-2"><i class="bi bi-globe me-1"></i>Hệ thống</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-info text-dark me-2">${a.courseName}</span>
                                    </c:otherwise>
                                </c:choose>
                                Từ người gửi: <strong>${a.instructorName}</strong>
                            </h6>
                            <p class="card-text text-dark" style="white-space: pre-wrap;">${a.content}</p>
                        </div>
                    </div>
                </c:forEach>
                <c:if test="${empty announcements}">
                    <div class="p-5 text-center text-muted">
                        <i class="bi bi-inbox fs-1 d-block mb-3"></i>
                        Không có thông báo nào.
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />

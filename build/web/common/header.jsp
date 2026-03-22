<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="ct" uri="http://assignmentmanage/customtags"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${param.title} - Assignment Manager</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css?v=2.0" rel="stylesheet">
</head>
<body>
<div class="wrapper">
    <!-- Sidebar -->
    <nav class="sidebar">
        <div class="sidebar-brand">
            <h4><i class="bi bi-mortarboard-fill"></i> AssignmentMS</h4>
            <small>Management System</small>
        </div>
        <ul class="sidebar-nav">
            <li class="nav-label">Navigation</li>
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/dashboard">
                    <i class="bi bi-grid-1x2-fill"></i> Dashboard
                </a>
            </li>
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/notifications">
                    <i class="bi bi-bell"></i> Notifications
                </a>
            </li>

            <c:if test="${sessionScope.currentUser.role == 'student'}">
                <li class="nav-label">Student</li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/student/courses">
                        <i class="bi bi-book"></i> My Classes
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/assignments">
                        <i class="bi bi-journal-text"></i> My Assignments
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/announcements">
                        <i class="bi bi-megaphone"></i> Announcements
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/submissions?action=grades">
                        <i class="bi bi-award"></i> My Grades
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/student/enroll">
                        <i class="bi bi-mortarboard"></i> Browse Classes
                    </a>
                </li>
            </c:if>

            <c:if test="${sessionScope.currentUser.role == 'instructor'}">
                <li class="nav-label">Instructor</li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/instructor/courses">
                        <i class="bi bi-book"></i> My Classes
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/assignments">
                        <i class="bi bi-journal-text"></i> Assignments
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/announcements">
                        <i class="bi bi-megaphone"></i> Announcements
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/assignments?action=create">
                        <i class="bi bi-plus-circle"></i> Create Assignment
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/groups">
                        <i class="bi bi-people"></i> Manage Groups
                    </a>
                </li>
            </c:if>

            <c:if test="${sessionScope.currentUser.role == 'admin'}">
                <li class="nav-label">Administration</li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/admin/users">
                        <i class="bi bi-people"></i> Manage Users
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/admin/courses">
                        <i class="bi bi-book"></i> Manage Courses
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/admin/enrollments">
                        <i class="bi bi-person-check"></i> Enrollments
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/admin/import-users">
                        <i class="bi bi-file-earmark-arrow-up"></i> Import Users
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/admin/semesters">
                        <i class="bi bi-calendar3"></i> Semesters
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/admin/subjects">
                        <i class="bi bi-journal-bookmark"></i> Subjects
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/announcements">
                        <i class="bi bi-megaphone"></i> System Announcements
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/admin/audit-log">
                        <i class="bi bi-clock-history"></i> Audit Logs
                    </a>
                </li>
            </c:if>

            <li class="nav-label">Account</li>
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/change-password">
                    <i class="bi bi-shield-lock"></i> Change Password
                </a>
            </li>
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/logout">
                    <i class="bi bi-box-arrow-left"></i> Logout
                </a>
            </li>
        </ul>
    </nav>

    <!-- Main Content -->
    <div class="main-content">
        <!-- Top Navbar -->
        <div class="top-navbar">
            <h1 class="page-title">${param.title}</h1>
            <div class="user-info">
                <a href="${pageContext.request.contextPath}/notifications" class="me-3 text-decoration-none position-relative">
                    <i class="bi bi-bell fs-5"></i>
                </a>
                <ct:roleBadge role="${sessionScope.currentUser.role}" />
                <span class="fw-semibold">${sessionScope.currentUser.fullName}</span>
                <div class="user-avatar">
                    ${sessionScope.currentUser.fullName.substring(0,1)}
                </div>
            </div>
        </div>

        <!-- Page Content -->
        <div class="page-content fade-in">


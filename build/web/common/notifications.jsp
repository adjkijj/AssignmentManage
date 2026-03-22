<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="ct" uri="http://assignmentmanage/customtags"%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Notifications" />
</jsp:include>

<div class="data-card">
    <div class="card-header">
        <h5>
            <i class="bi bi-bell me-2"></i>All Notifications
            <c:if test="${unreadCount > 0}">
                <span class="badge bg-danger ms-1">${unreadCount} unread</span>
            </c:if>
        </h5>
        <c:if test="${unreadCount > 0}">
            <a href="${pageContext.request.contextPath}/notifications?action=markAllRead"
               class="btn btn-sm btn-outline-primary">
                <i class="bi bi-check-all me-1"></i> Mark All as Read
            </a>
        </c:if>
    </div>
    <div class="card-body">
        <c:if test="${empty notifications}">
            <div class="empty-state">
                <i class="bi bi-bell-slash"></i>
                <h5>No Notifications</h5>
                <p>You don't have any notifications yet.</p>
            </div>
        </c:if>

        <c:forEach var="n" items="${notifications}">
            <div class="d-flex align-items-start justify-content-between p-3 mb-2 ${!n.isRead ? 'border-start border-primary border-3' : ''}"
                 style="background: var(--body-bg); border-radius: 8px;">
                <div class="d-flex align-items-start">
                    <i class="bi bi-bell${n.isRead ? '' : '-fill text-primary'} me-3 mt-1 fs-5"></i>
                    <div>
                        <strong>${n.title}</strong>
                        <c:if test="${!n.isRead}">
                            <span class="badge bg-primary ms-1">New</span>
                        </c:if>
                        <br><span class="text-muted">${n.message}</span>
                        <br><small class="text-muted">
                            <i class="bi bi-clock me-1"></i><ct:dateFormat date="${n.createdAt}" />
                        </small>
                    </div>
                </div>
                <div class="d-flex align-items-center">
                    <c:if test="${!n.isRead}">
                        <a href="${pageContext.request.contextPath}/notifications?action=markRead&id=${n.notificationId}"
                           class="btn btn-sm btn-outline-secondary me-2" title="Mark as read">
                            <i class="bi bi-check"></i>
                        </a>
                    </c:if>
                    <form method="get" action="${pageContext.request.contextPath}/notifications" style="display:inline;" onsubmit="return confirm('Delete this notification permanently?');">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="id" value="${n.notificationId}">
                        <button type="submit" class="btn btn-sm btn-outline-danger" title="Delete">
                            <i class="bi bi-trash"></i>
                        </button>
                    </form>
                </div>
            </div>
        </c:forEach>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />

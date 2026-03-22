<%@tag description="File Preview Tag" pageEncoding="UTF-8"%>
<%@attribute name="filePath" required="true" type="java.lang.String" %>
<%@attribute name="width" required="false" type="java.lang.String" %>
<%@attribute name="height" required="false" type="java.lang.String" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="w" value="${empty width ? '100%' : width}" />
<c:set var="h" value="${empty height ? '500px' : height}" />
<c:set var="url" value="${pageContext.request.contextPath}/uploads/submissions/${filePath}" />
<c:set var="ext" value="${fn:toLowerCase(fn:substringAfter(filePath, '.'))}" />

<c:choose>
    <c:when test="${ext == 'pdf'}">
        <iframe src="${url}" width="${w}" height="${h}" style="border:1px solid #ddd; border-radius:4px;"></iframe>
    </c:when>
    <c:when test="${ext == 'png' || ext == 'jpg' || ext == 'jpeg' || ext == 'gif'}">
        <div style="text-align:center; padding:10px; background:#f8f9fa; border:1px solid #ddd; border-radius:4px;">
            <img src="${url}" alt="Preview" style="max-width:${w}; max-height:${h}; object-fit:contain;" />
        </div>
    </c:when>
    <c:when test="${ext == 'txt'}">
        <iframe src="${url}" width="${w}" height="${h}" style="border:1px solid #ddd; border-radius:4px; background:#fff;"></iframe>
    </c:when>
    <c:otherwise>
        <div class="alert alert-secondary d-flex align-items-center justify-content-between">
            <div>
                <i class="bi bi-file-earmark me-2 fs-4"></i>
                <strong>Không thể xem trước định dạng này (.${ext})</strong>
            </div>
            <a href="${url}" class="btn btn-sm btn-primary" target="_blank" download>
                <i class="bi bi-download"></i> Tải về
            </a>
        </div>
    </c:otherwise>
</c:choose>

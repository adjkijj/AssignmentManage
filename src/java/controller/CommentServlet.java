package controller;

import dao.CommentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Comment;
import model.User;
import java.io.IOException;

/**
 * CommentServlet - Handles adding comments to assignments.
 * Extended: supports threaded replies via parentCommentId.
 */
public class CommentServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String action = request.getParameter("action");

        if ("add".equals(action)) {
            String content = request.getParameter("content");
            int assignmentId = Integer.parseInt(request.getParameter("assignmentId"));
            String parentIdStr = request.getParameter("parentId");

            if (content != null && !content.trim().isEmpty()) {
                Comment comment = new Comment();
                comment.setAssignmentId(assignmentId);
                comment.setUserId(currentUser.getUserId());
                comment.setContent(content.trim());

                // Set parent comment ID for threading (optional)
                if (parentIdStr != null && !parentIdStr.isEmpty()) {
                    comment.setParentCommentId(Integer.parseInt(parentIdStr));
                }

                CommentDAO commentDAO = new CommentDAO();
                commentDAO.createComment(comment);
            }

            response.sendRedirect(request.getContextPath() + "/assignments?action=detail&id=" + assignmentId);
        } else if ("delete".equals(action)) {
            int commentId = Integer.parseInt(request.getParameter("commentId"));
            int assignmentId = Integer.parseInt(request.getParameter("assignmentId"));

            CommentDAO commentDAO = new CommentDAO();
            commentDAO.deleteComment(commentId, currentUser.getUserId());

            response.sendRedirect(request.getContextPath() + "/assignments?action=detail&id=" + assignmentId);
        }
    }
}


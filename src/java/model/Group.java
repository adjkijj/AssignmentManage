package model;

import java.util.Date;
import java.util.List;

/**
 * Group model for group assignments.
 * A group is tied to a specific assignment and contains multiple student members.
 */
public class Group {
    private int groupId;
    private int assignmentId;
    private String groupName;
    private Date createdAt;
    private List<User> members; // populated via DAO join

    public Group() {}

    public int getGroupId() { return groupId; }
    public void setGroupId(int groupId) { this.groupId = groupId; }

    public int getAssignmentId() { return assignmentId; }
    public void setAssignmentId(int assignmentId) { this.assignmentId = assignmentId; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public List<User> getMembers() { return members; }
    public void setMembers(List<User> members) { this.members = members; }
}

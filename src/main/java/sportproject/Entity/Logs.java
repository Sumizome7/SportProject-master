package sportproject.Entity;

import java.sql.Timestamp;

public class Logs {

    private int logId;
    private int userId;
    private String actionType;
    private int targetId;
    private Timestamp createdAt;

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Logs() {
    }

    public Logs(int logId, int userId, String actionType, int targetId, Timestamp createdAt) {
        this.logId = logId;
        this.userId = userId;
        this.actionType = actionType;
        this.targetId = targetId;
        this.createdAt = createdAt;
    }
}

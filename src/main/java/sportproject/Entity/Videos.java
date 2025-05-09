package sportproject.Entity;

import java.sql.Timestamp;

public class Videos {

    private int videoId;
    private String videoName;
    private String videoCover;
    private Timestamp uploadTime;
    private String videoUrl;
    private int userId;
    private String category;

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoCover() {
        return videoCover;
    }

    public void setVideoCover(String videoCover) {
        this.videoCover = videoCover;
    }

    public Timestamp getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Timestamp uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Videos() {
    }

    public Videos(int videoId, String videoName, String videoCover, Timestamp uploadTime, String videoUrl, int userId, String category) {
        this.videoId = videoId;
        this.videoName = videoName;
        this.videoCover = videoCover;
        this.uploadTime = uploadTime;
        this.videoUrl = videoUrl;
        this.userId = userId;
        this.category = category;
    }
}

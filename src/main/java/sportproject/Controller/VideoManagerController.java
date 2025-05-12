package sportproject.Controller;

import org.springframework.http.HttpStatus;
import sportproject.Entity.Logs;
import sportproject.Entity.Videos;
import sportproject.Service.LogService;
import sportproject.Service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.UUID;
import  sportproject.Util.AliOssUtil;

@RestController
@RequestMapping("/api/videos")
public class VideoManagerController {

    @Autowired
    private VideoService videoService;
    @Autowired
    private AliOssUtil aliOssUtil;
    @Autowired
    private LogService logService;


    //本地上传到阿里云
    @PostMapping("/upload")
    public ResponseEntity<?> uploadVideoToOss(
            @RequestParam("videoName") String videoName,
            @RequestParam("videoFile") MultipartFile videoFile,
            @RequestParam("coverFile") MultipartFile coverFile,
            @RequestParam("category") String category,
            HttpSession session
    ) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        try {
            // 上传封面到 OSS
            String coverFileName = UUID.randomUUID() + "_" + coverFile.getOriginalFilename();
            String ossCoverPath = "videos/cover/" + coverFileName;
            String coverUrl = aliOssUtil.upload(coverFile.getBytes(), ossCoverPath);

            // 上传视频到 OSS
            String videoFileName = UUID.randomUUID() + "_" + videoFile.getOriginalFilename();
            String ossVideoPath = "videos/clips/" + videoFileName;
            String videoUrl = aliOssUtil.upload(videoFile.getBytes(), ossVideoPath);

            // 保存数据库记录
            Videos video = new Videos();
            video.setVideoName(videoName);
            video.setVideoCover(coverUrl);
            video.setVideoUrl(videoUrl);
            video.setCategory(category);
            video.setUserId(userId);
            video.setUploadTime(new Timestamp(System.currentTimeMillis()));

            videoService.uploadVideos(video);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("上传失败：" + e.getMessage());
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateVideo(
            @RequestParam("videoId") int videoId,
            @RequestParam("videoName") String videoName,
            @RequestParam(value = "videoFile", required = false) MultipartFile videoFile,
            @RequestParam(value = "coverFile", required = false) MultipartFile coverFile,
            @RequestParam("category") String category,
            HttpSession session
    ) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        try {
            // 先查原记录
            Videos video = videoService.videoInfo(videoId);
            if (video == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("找不到视频");
            }

            video.setVideoName(videoName);
            video.setCategory(category);

            // 删除旧封面并上传新封面
            if (coverFile != null && !coverFile.isEmpty()) {
                if (video.getVideoCover() != null) {
                    aliOssUtil.delete(video.getVideoCover());
                }
                String coverFileName = UUID.randomUUID() + "_" + coverFile.getOriginalFilename();
                String ossCoverPath = "videos/cover/" + coverFileName;
                String coverUrl = aliOssUtil.upload(coverFile.getBytes(), ossCoverPath);
                video.setVideoCover(coverUrl);
            }

            // 删除旧视频并上传新视频
            if (videoFile != null && !videoFile.isEmpty()) {
                if (video.getVideoUrl() != null) {
                    aliOssUtil.delete(video.getVideoUrl());
                }
                String videoFileName = UUID.randomUUID() + "_" + videoFile.getOriginalFilename();
                String ossVideoPath = "videos/clips/" + videoFileName;
                String videoUrl = aliOssUtil.upload(videoFile.getBytes(), ossVideoPath);
                video.setVideoUrl(videoUrl);
            }

            // 更新视频信息
            videoService.updateVideos(video);

            // 添加操作日志
            Logs log = new Logs();
            log.setUserId(userId);
            log.setActionType("edit");
            log.setTargetId(videoId);
            log.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            logService.addLogs(log);

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("编辑失败：" + e.getMessage());
        }
    }

}

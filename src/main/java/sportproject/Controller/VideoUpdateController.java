package sportproject.Controller;

import org.springframework.http.HttpStatus;
import sportproject.Entity.Videos;
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
public class VideoUpdateController {

    @Autowired
    private VideoService videoService;
    @Autowired
    private AliOssUtil aliOssUtil;


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
}

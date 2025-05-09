package sportproject.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sportproject.Entity.Users;
import sportproject.Entity.Videos;
import sportproject.Service.VideoService;
import sportproject.Util.AliOssUtil;


import java.io.File;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("user/api/videos")
public class UserVideoController {
    @Autowired
    private VideoService videoService;

    @Autowired
    private AliOssUtil aliOssUtil;

    @GetMapping
    public List<Videos> getVideosForUser(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        System.out.println("【调试】当前登录用户ID为: " + userId);
        if (userId == null) {
            // 或者 return 401
            return Collections.emptyList();
        }
        return videoService.videoForUsers(userId);
    }

    //本地上传到本地
//    @PostMapping("/upload")
//    public ResponseEntity<?> uploadVideo(
//            @RequestParam("videoName") String videoName,
//            @RequestParam("videoFile") MultipartFile videoFile,
//            @RequestParam("coverFile") MultipartFile coverFile,
//            @RequestParam("category") String category,
//            HttpSession session
//    ) {
//        Integer userId = (Integer) session.getAttribute("userId");
//        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//
//        try {
//            // 保存封面
//            String coverFileName = UUID.randomUUID() + "_" + coverFile.getOriginalFilename();
//            File coverDest = new File("D:/SportProject-profile/upload/videos/cover/", coverFileName);
//            coverFile.transferTo(coverDest);
//
//            // 保存视频
//            String videoFileName = UUID.randomUUID() + "_" + videoFile.getOriginalFilename();
//            File videoDest = new File("D:/SportProject-profile/upload/videos/clips/", videoFileName);
//            videoFile.transferTo(videoDest);
//
//            // 保存到数据库
//            Videos video = new Videos();
//            video.setVideoName(videoName);
//            video.setVideoCover(coverFileName);
//            video.setVideoUrl(videoFileName);
//            video.setCategory(category);
//            video.setUserId(userId);
//            video.setUploadTime(new Timestamp(System.currentTimeMillis()));
//
//            videoService.uploadVideos(video);
//            return ResponseEntity.ok().build();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("上传失败");
//        }
//    }

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


    @PutMapping
    public ResponseEntity<?> updateVideo(@RequestBody Videos video) {
        videoService.updateVideos(video);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVideo(@PathVariable int id) {
        videoService.videoDelete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public List<Videos> searchVideos(@RequestParam String keyword) {
        return videoService.videoSearchName(keyword);
    }
}


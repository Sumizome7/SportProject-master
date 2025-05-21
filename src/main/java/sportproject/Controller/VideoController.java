package sportproject.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import sportproject.Entity.VideoShared;
import sportproject.Entity.Videos;
import sportproject.Service.VideoService;
import org.springframework.ui.Model;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/videos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    private static final Logger logger = LoggerFactory.getLogger(VideoController.class);

    @ResponseBody
    @GetMapping("/category/{category}")
    public ResponseEntity<?> getVideosByCategory(@PathVariable String category, HttpSession session) {
        logger.info("请求视频类别: {}", category);

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户未登录");
        }

        try {
            List<Videos> videos;
            if ("all".equalsIgnoreCase(category)) {
                videos = videoService.videoForUsers(userId);
            } else {
                videos = videoService.videoList(userId, category);
            }

            return ResponseEntity.ok(convertVideoData(videos));
        } catch (Exception e) {
            logger.error("获取视频失败", e);
            return ResponseEntity.internalServerError().body("Error retrieving videos");
        }
    }


    private List<Map<String, Object>> convertVideoData(List<Videos> videos) {
        return videos.stream().map(v -> {
            Map<String, Object> videoData = new HashMap<>();
            videoData.put("id", v.getVideoId());
            videoData.put("title", v.getVideoName());
            videoData.put("cover", v.getVideoCover());
            videoData.put("url", v.getVideoUrl());
            videoData.put("category", v.getCategory());
            videoData.put("uploadTime", v.getUploadTime());
            return videoData;
        }).collect(Collectors.toList());
    }

    @GetMapping("/play/{id}")
    public String playVideo(@PathVariable("id") int videoId, Model model) {
        Videos video = videoService.videoInfo(videoId);
        model.addAttribute("video", video);
        return "videoPlayer";
    }

    @ResponseBody
    @GetMapping("/shared")
    public List<Videos> getAllSharedVideos() {
        return videoService.videoShared();
    }

    @GetMapping("/search")
    public List<Videos> searchVideos(@RequestParam String keyword) {
        return videoService.videoSearchName(keyword);
    }

    @ResponseBody
    @GetMapping("/sharedMine")
    public ResponseEntity<?> getUserSharedVideos(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户未登录");
        }

        try {
            List<VideoShared> sharedList = videoService.videoUserShared(userId);
            System.out.println("共享信息返回: " + new ObjectMapper().writeValueAsString(sharedList));
            return ResponseEntity.ok(sharedList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("获取共享信息失败");
        }
    }

    @PostMapping("/shared/update")
    @ResponseBody
    public ResponseEntity<?> updateSharedStatus(@RequestBody Map<String, Object> payload, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户未登录");
        }

        try {
            int videoId = (int) payload.get("videoId");
            boolean newStatus = (boolean) payload.get("isShared");

            Timestamp now = new Timestamp(System.currentTimeMillis());
            int rows = videoService.videoUpdateShared(newStatus, now, userId, videoId);

            if (rows > 0) {
                return ResponseEntity.ok("更新成功");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("更新失败：视频ID无效");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器异常：" + e.getMessage());
        }
    }

}

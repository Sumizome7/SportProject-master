package sportproject.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sportproject.Entity.Videos;
import sportproject.Service.VideoService;
import sportproject.Util.AliOssUtil;


import java.util.Collections;
import java.util.List;



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


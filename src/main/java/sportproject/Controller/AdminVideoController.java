package sportproject.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sportproject.Entity.Videos;
import sportproject.Service.VideoService;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/api/videos")
public class AdminVideoController {

    @Autowired
    private VideoService videoService;

    @GetMapping
    public List<Videos> getAllVideos() {
        return videoService.videoList();
    }

    @PostMapping
    public ResponseEntity<?> addVideo(@RequestBody Videos video) {
        System.out.println("收到的视频对象: " + video);
        video.setUploadTime(new Timestamp(System.currentTimeMillis()));
        videoService.uploadVideos(video);
        return ResponseEntity.ok().build();
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

    @PostMapping("/upload/video")
    public ResponseEntity<Map<String, String>> uploadVideoFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "文件为空"));
        }
        try {
            // 保存到本地磁盘
            String uploadDir = "D:/uploads/videos/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String originalFilename = file.getOriginalFilename();
            String filename = System.currentTimeMillis() + "_" + originalFilename;
            File dest = new File(uploadDir + filename);
            file.transferTo(dest);

            // 生成一个对外可访问的URL
            String fileUrl = "/uploads/videos/" + filename;

            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "上传失败"));
        }
    }

}

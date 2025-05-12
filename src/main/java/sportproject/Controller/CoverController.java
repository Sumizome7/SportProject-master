package sportproject.Controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.util.UUID;

@RestController
@RequestMapping("/api/cover")
public class CoverController {

    @PostMapping("/generate")
    public void generateCover(
            @RequestParam("videoFile") MultipartFile videoFile,
            HttpServletResponse response
    ) throws Exception {
        // 1. 将上传的视频保存到临时目录
        String tempVideoPath = System.getProperty("java.io.tmpdir") + "/" + UUID.randomUUID() + "_" + videoFile.getOriginalFilename();
        File tempVideoFile = new File(tempVideoPath);
        videoFile.transferTo(tempVideoFile);

        // 2. 设置封面保存路径
        String coverFileName = UUID.randomUUID() + "_cover.jpg";

        // 默认保存路径为桌面（Windows 用户）
        String desktopPath = System.getProperty("user.home") + "/Desktop/" + coverFileName;

        // 3. 执行 ffmpeg 命令提取封面
        String command = String.format("ffmpeg -i \"%s\" -ss 00:00:01 -vframes 1 \"%s\"", tempVideoPath, desktopPath);
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();

        // 4. 删除临时视频文件
        tempVideoFile.delete();

        // 5. 以文件下载形式返回给用户（也可以省略这步，直接告诉用户“已生成至桌面”）
        File coverFile = new File(desktopPath);
        if (coverFile.exists()) {
            response.setContentType("image/jpeg");
            response.setHeader("Content-Disposition", "attachment; filename=" + coverFileName);
            Files.copy(coverFile.toPath(), response.getOutputStream());
            response.flushBuffer();
        }
    }
}

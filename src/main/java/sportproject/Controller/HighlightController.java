package sportproject.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

@RestController
@RequestMapping("/api/highlight")
public class HighlightController {

    @PostMapping("/generate")
    public void generateHighlight(
            @RequestParam("videoFile") MultipartFile videoFile,
            @RequestParam("rankInput") String rankInput,
            HttpServletResponse response
    ) {
        try {
            // 1. 设置目录路径
            Path baseDir = Paths.get("D:/Users/admin/Desktop/pycharm/ultralytics-main/test1");
            Path inputVideoDir = baseDir.resolve("input/video");
            Path inputTxtDir = baseDir.resolve("input/txt");
            Path outputVideo = baseDir.resolve("output/video/highlight_edited_video.mp4");

            Files.createDirectories(inputVideoDir);
            Files.createDirectories(inputTxtDir);

            // 2. 保存视频
            String videoName = videoFile.getOriginalFilename();
            Path videoPath = inputVideoDir.resolve(videoName);
            videoFile.transferTo(videoPath.toFile());

            // 3. 保存名次
            Path inputTxt = inputTxtDir.resolve("input.txt");
            System.out.println("📄 尝试写入 input.txt 路径：" + inputTxt.toAbsolutePath());
            System.out.println("📥 接收到 rankInput = " + rankInput);
            try {
                Files.write(inputTxt, rankInput.getBytes(StandardCharsets.UTF_8));
                System.out.println("✅ input.txt 写入成功！");
            } catch (IOException ioException) {
                System.err.println("❌ 写入 input.txt 失败：" + ioException.getMessage());
                ioException.printStackTrace();
            }


            // 4. 执行脚本
            if (!runPythonPipeline(baseDir)) {
                returnError(response, "执行 Python pipeline 失败！");
                return;
            }

            // 5. 检查结果视频
            if (!Files.exists(outputVideo)) {
                returnError(response, "未生成输出视频！");
                return;
            }

            // 6. 写入响应并下载
            response.setContentType("video/mp4");
            response.setHeader("Content-Disposition", "attachment; filename=\"highlight_edited_video.mp4\"");
            Files.copy(outputVideo, response.getOutputStream());
            response.flushBuffer();

            // 7. 自动清理
            Files.deleteIfExists(inputTxt);
            Files.deleteIfExists(videoPath);
            Files.deleteIfExists(outputVideo);

        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.setStatus(500);
                response.getWriter().write("服务器异常: " + e.getMessage());
            } catch (IOException ignored) {}
        }
    }

    // 单个 Python 脚本执行器
    private boolean runPythonPipeline(Path workingDir) {
        try {
            // run_pipeline.bat 必须放在 workingDir 中
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "run_pipeline.bat");
            pb.directory(workingDir.toFile());
            pb.redirectErrorStream(true);

            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[pipeline] " + line);
                }
            }

            return process.waitFor() == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 错误输出封装
    private void returnError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(500);
        response.getWriter().write("❌ " + message);
    }


    @PostMapping("/test")
    public void test(HttpServletResponse response) {
        try {
            System.out.println("✅ 收到 POST 请求！");
            response.setContentType("text/plain");
            response.getWriter().write("后台正常工作！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



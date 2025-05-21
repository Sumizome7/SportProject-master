package sportproject.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sportproject.Entity.Users;
import sportproject.Service.UserService;
import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/user/profile")
public class UserProfileController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String showProfilePage(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        model.addAttribute("username", userService.userName(userId));
        model.addAttribute("userId", userId);
        model.addAttribute("gender", userService.userGender(userId));
        model.addAttribute("email", userService.userEmail(userId));
        model.addAttribute("phone", userService.userPhone(userId));


        String fileName = userService.userAvatarUrl(userId);
        String avatarUrl = (fileName != null && !fileName.isEmpty()) ? "/avatars/" + fileName : "/images/default-avatar.jpg";
        model.addAttribute("avatarUrl", avatarUrl);

        return "user-profile";
    }

    @PostMapping("/updateProfile")
    public ResponseEntity<String> updateProfile(@RequestBody Users user) {
        // 用户名不能为空且长度大于等于4
        if (user.getUserName() == null || user.getUserName().length() < 4) {
            return ResponseEntity.badRequest().body("用户名不得为空且不少于4个字符");
        }

        // 邮箱校验
        if (user.getEmail() == null || !user.getEmail().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            return ResponseEntity.badRequest().body("邮箱格式不正确");
        }

        // 手机号校验（以中国手机号为例）
        if (user.getPhone() == null || !user.getPhone().matches("^1[3-9]\\d{9}$")) {
            return ResponseEntity.badRequest().body("手机号格式不正确");
        }

        boolean success = userService.userUpdate(user);
        return success ? ResponseEntity.ok("修改成功") : ResponseEntity.status(500).body("修改失败");
    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未登录或登录已过期");
        }

        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");

        if (currentPassword == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "密码不能为空"));
        }

        // 获取数据库中当前密码
        String dbPassword = userService.userPwd(userId);
        if (!currentPassword.equals(dbPassword)) {
            return ResponseEntity.badRequest().body(Map.of("message", "当前密码不正确"));
        }

        // 更新密码
        int rows = userService.userPwdUpdate(userId, newPassword);
        return rows > 0
                ? ResponseEntity.ok(Map.of("message", "密码修改成功"))
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "密码修改失败"));
    }

    @PostMapping("/uploadAvatar")
    @ResponseBody
    public Map<String, Object> uploadAvatar(@RequestParam("avatar") MultipartFile avatar, HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        if (avatar.isEmpty()) {
            result.put("error", "文件为空");
            return result;
        }

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            result.put("error", "未登录");
            return result;
        }

        try {
            // 生成文件名
            String filename = UUID.randomUUID() + "-" + avatar.getOriginalFilename();
            String uploadPath = "uploads/avatars/";

            File dir = new File(uploadPath);
            if (!dir.exists()) dir.mkdirs();

            File dest = new File(uploadPath + filename);
            avatar.transferTo(dest);

            // 保存文件名到数据库（只存如 "abc.jpg"）
            userService.userAvatarUpdate(userId, filename);

            // 返回前端完整路径（如 "/avatars/abc.jpg"）
            result.put("avatarUrl", "/avatars/" + filename);
            return result;

        } catch (IOException e) {
            e.printStackTrace();
            result.put("error", "上传失败");
            return result;
        }
    }

}
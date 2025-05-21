package sportproject.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sportproject.Entity.Users;
import sportproject.Service.UserService;

import java.util.Map;

@Controller
@RequestMapping("/admin/profile")
public class AdminProfileController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String showAdminProfilePage(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        // 判断用户角色
        if (!userService.userRole(userId)) {
            return "redirect:/user/profile";
        }

        model.addAttribute("username", userService.userName(userId));
        model.addAttribute("userId", userId);
        model.addAttribute("gender", userService.userGender(userId));
        model.addAttribute("email", userService.userEmail(userId));
        model.addAttribute("phone", userService.userPhone(userId));
        model.addAttribute("avatarUrl", userService.userAvatarUrl(userId));

        return "admin-profile";
    }

    @PostMapping("/updateProfile")
    public ResponseEntity<String> updateProfile(@RequestBody Users user) {
        // 同样做表单校验
        if (user.getUserName() == null || user.getUserName().length() < 4) {
            return ResponseEntity.badRequest().body("用户名不得为空且不少于4个字符");
        }

        if (user.getEmail() == null || !user.getEmail().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            return ResponseEntity.badRequest().body("邮箱格式不正确");
        }

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
}
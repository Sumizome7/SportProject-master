package sportproject.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sportproject.Entity.Users;
import sportproject.Mapper.UserMapper;
import sportproject.Service.UserService;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RegisterController {

    @Autowired
    private UserService userService;
    private UserMapper userMapper;


    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody Map<String, Object> data) {
        System.out.println("收到原始注册数据：" + data);

        Users user = new Users();
        user.setUserName((String) data.get("userName"));
        user.setPassword((String) data.get("password"));
        user.setEmail((String) data.get("email"));
        user.setPhone((String) data.get("phone"));

        // ✨ 显式处理性别转换
        String genderStr = (String) data.get("gender");
        Boolean gender = "男".equals(genderStr) ? true : false;
        user.setGender(gender);

        // 设置默认值
        user.setAdmin(false);
        user.setCreateAt(new Timestamp(System.currentTimeMillis()));
        user.setAvatarUrl("default-avatar.jpg");

        if (userService.isUsernameTaken(user.getUserName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "用户名已存在，请更换"));
        }

        try {
            // 插入成功会自动回填 userId（需 @Options）
            int result = userService.addbyRegister(user);

            if (result > 0) {
                // ✅ 注册成功，返回详细信息供前端写入 txt
                Map<String, Object> response = new HashMap<>();
                response.put("message", "注册成功！");
                // 自动回填的 ID
                response.put("userId", user.getUserId());
                response.put("userName", user.getUserName());
                response.put("password", user.getPassword());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        Map.of("message", "注册失败，请重试")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("message", "发生错误：" + e.getMessage())
            );
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, Object> request) {
        Integer userId = (Integer) request.get("userId");
        if (userId == null) {
            return ResponseEntity.badRequest().body("用户ID不能为空");
        }

        int result = userService.userPwdUpdate(userId, "123");
        if (result > 0) {
            return ResponseEntity.ok("密码已重置为123");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("未找到该用户");
        }
    }
}

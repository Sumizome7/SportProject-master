package sportproject.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import sportproject.Entity.Users;
import sportproject.Service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/api/users")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<Users> getAllUsers() {
        return userService.allUsers();
    }

    @GetMapping("/{id}")
    public Users getUserById(@PathVariable int id) {
        return userService.userInfo(id);
    }

    @PostMapping
    public ResponseEntity<String> addUser(@RequestBody Users user) {
        System.out.println("接收到用户：" + user);
        userService.addUsers(user);
        return ResponseEntity.ok("用户添加成功");
    }

    @PutMapping
    public ResponseEntity<String> updateUser(@RequestBody Users user) {
        if (userService.userUpdate(user)) {
            return ResponseEntity.ok("用户更新成功");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("更新失败");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id) {
        if (userService.deleteUsers(id) > 0) {
            return ResponseEntity.ok("删除成功");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("用户不存在");
        }
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<String> updateUserPassword(@PathVariable int id, @RequestBody Map<String, String> payload) {
        String newPassword = payload.get("password");
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("密码不能为空");
        }

        int result = userService.userPwdUpdate(id, newPassword);
        if (result > 0) {
            return ResponseEntity.ok("密码更新成功");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("更新失败");
        }
    }

}

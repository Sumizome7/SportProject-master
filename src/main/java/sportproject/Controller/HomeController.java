package sportproject.Controller;

import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import sportproject.Service.UserService;


@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @GetMapping("/user") // 普通用户主页
    public String showUserDashboard(HttpSession session) {
        // 从会话获取登录时存储的userId
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            // 未登录则重定向到登录页
            return "redirect:/login";
        }

        // 调用 Service 方法获取用户名
        String username = userService.userName(userId);
        session.setAttribute("username", username);

        // 返回普通用户主页模板
        return "redirect:/user/home";
    }

    @GetMapping("/admin") // 管理员主页
    public String showAdminDashboard(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        String username = userService.userName(userId);
        model.addAttribute("username", username);

        // 返回管理员主页模板
        return "admin";
    }

    // 添加注销端点
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // 使当前会话失效
        session.invalidate();
        // 重定向到登录页
        return "redirect:/login";
    }
}
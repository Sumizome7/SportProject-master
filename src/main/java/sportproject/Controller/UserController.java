package sportproject.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    @GetMapping("/home")
    public String loadHomeFragment(HttpSession session, Model model) {
        String username = (String)session.getAttribute("username");
        model.addAttribute("username", username);
        model.addAttribute("content", "fragments/user-home");
        return "user";
    }

    @GetMapping("/upload")
    public String showUploadPage(Model model) {
        model.addAttribute("content", "fragments/video-upload");
        return "user";
    }

    @GetMapping("/manage")
    public String loadManageFragment(Model model) {
        model.addAttribute("content", "fragments/video-manage");
        return "user";
    }

    @GetMapping("/view")
    public String loadViewFragment(Model model) {
        model.addAttribute("content", "fragments/video-view");
        return "user";
    }
}
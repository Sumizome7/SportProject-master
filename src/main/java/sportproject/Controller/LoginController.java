package sportproject.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import sportproject.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserService userService;

    //display login page
    @GetMapping
    public ModelAndView showLoginPage() {
        return new ModelAndView("login");
    }

    //user login
    @PostMapping
    public ModelAndView login(@RequestParam int userId, @RequestParam String password, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        session.setAttribute("userId", userId);
        if (userService.userExist(userId)) {
            if (userService.userLogin(userId, password)) {
                //userId succeed
                boolean isAdmin = userService.userRole(userId);
                if (isAdmin) {
                    modelAndView.setViewName("redirect:/admin");
                } else {
                    modelAndView.setViewName("redirect:/user");
//                    System.out.println("Login successful, userId = " + userId + ", redirecting...");
                }
            } else {
                //login failed
                modelAndView.setViewName("login");
                modelAndView.addObject("error", "Invalid userID or password.");
            }
        } else {
            //userID does not existed
            modelAndView.setViewName("login");
            modelAndView.addObject("error", "userID does not existed.");
        }
        return modelAndView;
    }
}

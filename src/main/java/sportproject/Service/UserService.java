package sportproject.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import sportproject.Entity.Users;

import java.util.List;

public interface UserService extends IService<Users> {
    //get user's info
    Users userInfo(int userId);

    //check user is existed
    boolean userExist(int userId);

    //get user's pwd
    String userPwd(int userId);

    //check user's role
    boolean userRole(int userId);

    //get user's name
    String userName(int userId);

    //check userId is match userPwd
    boolean userLogin(int userId, String password);

    String userPhone(int userId);

    String userEmail(int userId);

    boolean userGender(int userId);

    String userAvatarUrl(int userId);

    boolean userUpdate(Users user);

    int userAvatarUpdate(int userId, String avatarFileName);

    int addUsers(Users user);

    List<Users> allUsers();

    int deleteUsers(int userId);

    int userPwdUpdate(int userId, String password);
}
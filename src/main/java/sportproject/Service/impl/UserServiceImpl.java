package sportproject.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import sportproject.Entity.Users;
import sportproject.Mapper.UserMapper;
import sportproject.Service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, Users> implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public Users userInfo(int userId) {
        return userMapper.findByUserId(userId);
    }

    @Override
    public String userName(int userId) {
        return userMapper.getUserName(userId);
    }

    @Override
    public String userPwd(int userId) {
        return userMapper.getUserPwd(userId);
    }

    @Override
    public boolean userLogin(int userId, String password) {
        return userMapper.getUserPwd(userId).equals(password);
    }

    @Override
    public boolean userRole(int userId) {
        return userMapper.getUserRole(userId);
    }

    @Override
    public boolean userExist(int userId) {
        return userMapper.isUserId(userId);
    }

    @Override
    public String userPhone(int userId) {
        return userMapper.getUserPhone(userId);
    }

    @Override
    public String userEmail(int userId) {
        return userMapper.getUserEmail(userId);
    }

    @Override
    public boolean userGender(int userId) {
        return userMapper.getUserGender(userId);
    }

    @Override
    public String userAvatarUrl(int userId) {
        return userMapper.getUserAvatarUrl(userId);
    }

    @Override
    public boolean userUpdate(Users user) {
        return userMapper.updateUser(user) > 0;
    }

    @Override
    public int userAvatarUpdate(int userId, String avatarFileName) {
        return userMapper.updateUserAva(userId, avatarFileName);
    }

    @Override
    public int addUsers(Users user) {
        return userMapper.addUser(user);
    }

    @Override
    public List<Users> allUsers() {
        return userMapper.getUserList();
    }

    @Override
    public int deleteUsers(int userId) {
        return userMapper.deleteUser(userId);
    }

    @Override
    public int userPwdUpdate(int userId, String password) {
        return userMapper.updateUserPwd(userId, password);
    }

}

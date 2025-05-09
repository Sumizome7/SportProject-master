package sportproject.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import sportproject.Entity.Users;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<Users> {
    //search a user by id
    @Select("SELECT * FROM users WHERE user_id = #{userid}")
    Users findByUserId(@Param("userid") int userid);

    //search if the userId is existed
    @Select("SELECT EXISTS(SELECT 1 FROM users WHERE user_id = #{userid})")
    boolean isUserId(@Param("userid") int userid);

    //search user's password
    @Select("SELECT password FROM users WHERE user_id = #{userid}")
    String getUserPwd(@Param("userid") int userid);

    //search user's role
    @Select("SELECT is_admin FROM users WHERE user_id = #{userid}")
    boolean getUserRole(@Param("userid") int userid);

    //search userName by id
    @Select("SELECT username FROM users WHERE user_id = #{userid}")
    String getUserName(@Param("userid") int userid);

    //查找用户电话号码
    @Select("SELECT phone FROM users WHERE user_id = #{userid}")
    String getUserPhone(@Param("userid") int userid);

    //查找用户邮箱
    @Select("SELECT email FROM users WHERE user_id = #{userid}")
    String getUserEmail(@Param("userid") int userid);

    //查找用户性别
    @Select("SELECT gender FROM users WHERE user_id = #{userid}")
    boolean getUserGender(@Param("userid") int userid);

    //查找用户头像URL
    @Select("SELECT avatarUrl FROM users WHERE user_id = #{userid}")
    String getUserAvatarUrl(@Param("userid") int userid);

    //add a new user
    @Insert("INSERT INTO users (user_id, username, password, is_admin, created_at, phone, email, gender, avatarUrl) VALUE(#{userId}, #{userName}, #{password}, #{isAdmin}, #{createAt}, #{phone}, #{email}, #{gender}, #{avatarUrl})")
    int addUser(Users user);

    //update a user
    @Update("UPDATE users SET username = #{userName}, phone = #{phone}, email = #{email} WHERE user_id = #{userId}")
    int updateUser(Users user);

    @Update("UPDATE users SET password = #{password} WHERE user_id = #{userId}")
    int updateUserPwd(@Param("userId") int userId, @Param("password") String password);

    @Update("UPDATE users SET avatarUrl = #{avatarUrl} WHERE user_id = #{userId}")
    int updateUserAva(@Param("userId") int userId, @Param("avatarUrl") String avatarUrl);

    //delete a user
    @Delete("DELETE FROM users WHERE user_id = #{userId}")
    int deleteUser(@Param("userId") int userId);

    @Select("SELECT * FROM users")
    List<Users> getUserList();

}

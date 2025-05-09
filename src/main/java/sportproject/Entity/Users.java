package sportproject.Entity;

import java.sql.Timestamp;
import jakarta.validation.constraints.*;

public class Users {

    private int userId;

    private String userName;

    private String password;

    private boolean isAdmin;

    private Timestamp createAt;

    private String phone;

    private String email;

    private boolean gender;

    private String avatarUrl;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public Timestamp getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Timestamp createAt) {
        this.createAt = createAt;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Users() {
    }

    public Users(int userId, String userName, String password, boolean isAdmin, Timestamp createAt, String phone, String email, boolean gender, String avatarUrl) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.isAdmin = isAdmin;
        this.createAt = createAt;
        this.phone = phone;
        this.email = email;
        this.gender = gender;
        this.avatarUrl = avatarUrl;
    }
}

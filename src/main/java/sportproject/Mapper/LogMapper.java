package sportproject.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import sportproject.Entity.Logs;
import sportproject.Entity.Users;

import java.util.List;

@Mapper
public interface LogMapper extends BaseMapper<Logs> {
    @Insert("INSERT INTO audit_logs (user_id, action_type, target_id, created_at) VALUES (#{userId}, #{actionType}, #{targetId}, #{createdAt})")
    int insert(Logs log);

    @Select("SELECT * FROM audit_logs")
    List<Logs> getlogList();
}

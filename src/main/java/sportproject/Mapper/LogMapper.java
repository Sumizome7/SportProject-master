package sportproject.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import sportproject.Entity.Logs;

@Mapper
public interface LogMapper extends BaseMapper<Logs> {
    @Insert("INSERT INTO audit_logs (user_id, action_type, target_id, created_at) VALUES (#{userId}, #{actionType}, #{targetId}, #{createdAt})")
    int insert(Logs log);
}

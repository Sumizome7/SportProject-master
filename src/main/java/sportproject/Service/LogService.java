package sportproject.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import sportproject.Entity.Logs;

public interface LogService extends IService<Logs> {
    int addLogs(Logs log);
}

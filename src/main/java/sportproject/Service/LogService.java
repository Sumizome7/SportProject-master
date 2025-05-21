package sportproject.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import sportproject.Entity.Logs;

import java.util.List;

public interface LogService extends IService<Logs> {
    int addLogs(Logs log);

    List<Logs> logList();
}

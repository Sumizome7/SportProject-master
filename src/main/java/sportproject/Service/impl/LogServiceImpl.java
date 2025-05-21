package sportproject.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sportproject.Entity.Logs;
import sportproject.Mapper.LogMapper;
import sportproject.Service.LogService;

import java.util.List;

@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, Logs> implements LogService {

    @Autowired
    LogMapper logMapper;

    @Override
    public int addLogs(Logs log) {
        return logMapper.insert(log);
    }

    @Override
    public List<Logs> logList() {
        return logMapper.getlogList();
    }
}

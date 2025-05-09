package sportproject.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import sportproject.Entity.Videos;

import java.util.List;

/**
 * @author poker
 * 视频服务类接口
 */

public interface VideoService extends IService<Videos> {

    //返回视频列表
    List<Videos> videoList();

    //查询视频信息
    Videos videoInfo(int videoId);

    //查询视频名
    String videoName(int videoId);

    //查询视频URL
    String videoUrl(int videoId);

    //查询封面url
    String coverUrl(int videoId);

    //查询视频类型
    String videoCategory(int videoId);

    //根据视频类型返回视频列表
    List<Videos> videoByCategory(String keyword);

    List<Videos> videoList(int userId, String keyword);

    //删除视频
    int videoDelete(int videoId);

    //视频ID模糊查询
    List<Videos> videoSearchId(String keyword);

    //视频名模糊查询
    List<Videos> videoSearchName(String keyword);

    int uploadVideos(Videos video);

    int updateVideos(Videos video);

    List<Videos> videoForUsers(int userId);
}

package sportproject.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import sportproject.Entity.Videos;
import sportproject.Mapper.VideoMapper;
import sportproject.Service.VideoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Videos> implements VideoService {

    @Autowired
    VideoMapper videoMapper;

    @Override
    public List<Videos> videoList() {
        return videoMapper.getVideoList();
    }

    @Override
    public Videos videoInfo(int videoId) {
        return videoMapper.findByVideoId(videoId);
    }

    @Override
    public String videoName(int videoId) {
        return videoMapper.getVideoName(videoId);
    }

    @Override
    public String videoUrl(int videoId) {
        return videoMapper.getVideoUrl(videoId);
    }

    @Override
    public String coverUrl(int videoId) {
        return videoMapper.getCoverUrl(videoId);
    }

    @Override
    public String videoCategory(int videoId) {
        return videoMapper.getCategory(videoId);
    }

    @Override
    public List<Videos> videoByCategory(String keyword) {
        return videoMapper.getVideosByCategory(keyword);
    }

    @Override
    public List<Videos> videoList(int userId, String keyword) {
        return videoMapper.getVideo(userId, keyword);
    }

    @Override
    public int videoDelete(int videoId) {
        return videoMapper.deleteVideoById(videoId);
    }

    @Override
    public List<Videos> videoSearchId(String keyword) {
        return videoMapper.getVideosByIdKeyword(keyword);
    }

    @Override
    public List<Videos> videoSearchName(String keyword) {
        return videoMapper.getVideosByKeyword(keyword);
    }

    @Override
    public int uploadVideos(Videos video) {
        return videoMapper.uploadVideo(video);
    }

    @Override
    public int updateVideos(Videos video) {
        return videoMapper.updateVideo(video);
    }

    @Override
    public List<Videos> videoForUsers(int userId) {
        return videoMapper.getVideosByUserId(userId);
    }
}

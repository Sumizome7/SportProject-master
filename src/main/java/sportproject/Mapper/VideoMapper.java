package sportproject.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import sportproject.Entity.Users;
import sportproject.Entity.VideoShared;
import sportproject.Entity.Videos;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface VideoMapper extends BaseMapper<Videos> {
    @Select("SELECT * FROM videos")
    List<Videos> getVideoList();

    //search a video by id
    @Select("SELECT * FROM videos WHERE video_id = #{videoid}")
    Videos findByVideoId(@Param("videoid") int videoid);

    //search video name by id
    @Select("SELECT video_name FROM videos WHERE video_id = #{videoid}")
    String getVideoName(@Param("videoid") int videoid);

    //search video url by id
    @Select("SELECT video_url FROM videos WHERE video_id = #{videoid}")
    String getVideoUrl(@Param("videoid") int videoid);

    //search cover url by id
    @Select("SELECT video_cover FROM videos WHERE video_id = #{videoid}")
    String getCoverUrl(@Param("videoid") int videoid);

    @Select("SELECT category FROM videos WHERE video_id = #{videoid}")
    String getCategory(@Param("videoid") int videoid);

    @Select("SELECT * FROM videos WHERE category = #{keyword}")
    List<Videos>getVideosByCategory(@Param("keyword") String keyword);

    @Select("SELECT * FROM videos WHERE user_id = #{userId}")
    List<Videos>getVideosByUserId(@Param("userId") int userId);

    @Select("SELECT * FROM videos WHERE user_id = #{userId} AND category = #{keyword}")
    List<Videos>getVideo(@Param("userId") int userId, @Param("keyword") String keyword);

    //视频名称模糊查询
    @Select("SELECT * FROM videos WHERE video_name LIKE CONCAT('%', #{keyword}, '%')")
    List<Videos> getVideosByKeyword(@Param("keyword") String keyword);

    //视频ID模糊查询
    @Select("SELECT * FROM videos WHERE CAST(video_id AS CHAR) LIKE CONCAT('%', #{keyword}, '%')")
    List<Videos> getVideosByIdKeyword(@Param("keyword") String keyword);

    //delete video by id
    @Delete("DELETE FROM videos WHERE video_id = #{videoid}")
    int deleteVideoById(@Param("videoid") int videoid);

    //insert
    @Insert("INSERT INTO videos (video_id, video_name, video_cover, upload_time, video_url, user_id, category) " +
            "VALUES (#{videoId}, #{videoName}, #{videoCover}, #{uploadTime}, #{videoUrl}, #{userId}, #{category})")
    @Options(useGeneratedKeys = true, keyProperty = "videoId")
    int uploadVideo(Videos video);

    //update
    @Update("UPDATE videos SET video_name = #{videoName}, video_cover = #{videoCover}, video_url = #{videoUrl}, category = #{category} WHERE video_id = #{videoId}")
    int updateVideo(Videos video);

    @Insert("INSERT INTO video_share (video_id, is_shared, shared_at, shared_by) VALUES (#{videoId}, #{isShared}, #{sharedAt}, #{sharedBy})")
    int uploadVideoShare(@Param("videoId") int videoId, @Param("isShared") boolean isShared, @Param("sharedAt") Timestamp sharedAt, @Param("sharedBy") int sharedBy);

    @Delete("DELETE FROM video_share WHERE video_id = #{videoId}")
    int deleteVideoShare(@Param("videoId") int videoId);

    @Update("UPDATE video_share SET is_shared = #{isShared}, shared_at = #{sharedAt}, shared_by = #{sharedBy} WHERE video_id = #{videoId}")
    int updateVideoShare(@Param("isShared") boolean isShared, @Param("sharedAt") Timestamp sharedAt, @Param("sharedBy") int sharedBy, @Param("videoId") int videoId);

    @Select("SELECT v.* FROM videos v JOIN video_share vs ON v.video_id = vs.video_id WHERE vs.is_shared = 1 ")
    List<Videos> getAllSharedVideos();

    @Select("SELECT vs.* FROM video_share vs JOIN videos v ON vs.video_id = v.video_id WHERE v.user_id = #{userId}")
    List<VideoShared> getUserShared(@Param("userId") int userId);

    @Select("SELECT * FROM video_share")
    List<VideoShared> getAllShared();
}

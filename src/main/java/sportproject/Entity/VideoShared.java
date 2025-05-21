package sportproject.Entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class VideoShared {
    private int videoId;
    private boolean isShared;
    private Timestamp sharedAt;
    private int sharedBy;
}

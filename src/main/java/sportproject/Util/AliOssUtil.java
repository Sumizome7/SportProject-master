package sportproject.Util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;

@Slf4j
public class AliOssUtil {

    private final String endpoint;
    private final String accessKeyId;
    private final String accessKeySecret;
    private final String bucketName;

    public AliOssUtil(String endpoint, String accessKeyId, String accessKeySecret, String bucketName) {
        this.endpoint = endpoint;
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.bucketName = bucketName;
    }

    public String upload(byte[] bytes, String objectName) {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            // ✅ 判断文件类型
            String contentType;
            if (objectName.endsWith(".mp4")) {
                contentType = "video/mp4";
            } else if (objectName.endsWith(".jpg") || objectName.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (objectName.endsWith(".png")) {
                contentType = "image/png";
            } else {
                contentType = "application/octet-stream"; // 其他类型
            }

            // ✅ 设置元数据
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);

            // ✅ 上传时带上 metadata
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes), metadata);

        } catch (OSSException oe) {
            System.out.println("OSS错误：" + oe.getErrorMessage());
        } catch (ClientException ce) {
            System.out.println("客户端错误：" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        return "http://" + bucketName + "." + endpoint + "/" + objectName;
    }


    public void delete(String fullUrl) {
        String objectName = fullUrl.replace("http://" + bucketName + "." + endpoint + "/", "");

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            ossClient.deleteObject(bucketName, objectName);
            log.info("删除成功：{}", objectName);
        } catch (Exception e) {
            log.error("删除失败：{}", e.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
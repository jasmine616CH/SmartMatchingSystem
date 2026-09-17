package config.minio;

import io.minio.MinioClient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * minio初始化
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    /**
     * minio存放地址
     */
    private String endpoint;

    /**
     * minio访问密钥id
     */
    private String accessKey;

    /**
     * minio访问私密密钥
     */
    private String secretKey;

    @Bean
    public MinioClient minioClient(){
        return minioClient().builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

}

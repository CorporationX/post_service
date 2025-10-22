package faang.school.postservice.service.image;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class MinioClient { // создает настроенный клиент для подключения к MinIO серверу

    @Value("${services.s3.endpoint}") //берет значение из application.properties и и присваивает его полю endpoint
    private String endpoint;

    @Value("${services.s3.accessKey}") //  // Берет access key из конфигурации ("user")
    private String accessKey;

    @Value("${services.s3.secretKey}") // Берет secret key из конфигурации ("password")
    private String secretKey;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey); //  // Создает объект учетных данных AWS из accessKey и secretKey

        return S3Client.builder() // Создает строитель (builder) для S3Client
                .endpointOverride(URI.create(endpoint)) // Переопределяет стандартный endpoint AWS на наш MinIO Вместо s3.amazonaws.com использует http://192.168.1.65
                .credentialsProvider(StaticCredentialsProvider.create(credentials)) //Указывает провайдер учетных данных, StaticCredentialsProvider - фиксированные логин/пароль
                .region(Region.US_EAST_1) //   Устанавливает регион (для MinIO можно любой), AWS требует регион, MinIO игнорирует его
                .build(); // /Завершает строительство и возвращает готовый S3Client
    }
}

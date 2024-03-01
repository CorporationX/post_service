package faang.school.postservice.springContext;

import faang.school.postservice.service.s3.S3Service;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class SpringContextTest {
    @MockBean
    private S3Service clientAmazonS3;

    @Test
    void contextLoads(){
    }
}

package faang.school.postservice.service.s3;

import com.jayway.jsonpath.JsonPath;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserClientResponseDto;
import faang.school.postservice.model.comment.Comment;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.repository.comment.CommentRepository;
import faang.school.postservice.repository.post.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.FileCopyUtils;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketAlreadyExistsException;
import software.amazon.awssdk.services.s3.model.BucketAlreadyOwnedByYouException;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class S3ServiceIntegrationTest {

    private static final String POSTGRES_IMAGE = "postgres:13.3";
    private static final String MINIO_IMAGE = "minio/minio:latest";
    private static final String MINIO_USER = "user";
    private static final String MINIO_PASSWORD = "password";
    private static final int MINIO_PORT = 9000;
    private static final String MINIO_COMMAND = "server /data";
    private static final String BUCKET = "corpbucket";
    private static final String TEST_IMAGE_PATH = "test-images/kik.jpg";
    private static final String TEST_IMAGE_NAME = "kik.jpg";
    private static final String TEST_IMAGE_TYPE = "image/jpeg";
    private static final long USER_ID = 1L;

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(POSTGRES_IMAGE)
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Container
    static final GenericContainer<?> minio = new GenericContainer<>(MINIO_IMAGE)
            .withExposedPorts(MINIO_PORT)
            .withEnv("MINIO_ROOT_USER", MINIO_USER)
            .withEnv("MINIO_ROOT_PASSWORD", MINIO_PASSWORD)
            .withCommand(MINIO_COMMAND);

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("cloud.aws.s3.endpoint", () ->
                "http://" + minio.getHost() + ":" + minio.getMappedPort(MINIO_PORT));
    }

    @Autowired
    private S3Client s3Client;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @MockBean
    private UserServiceClient userServiceClient;

    private Comment comment;

    @BeforeEach
    void initBucket() {
        try {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(BUCKET).build());
        } catch (BucketAlreadyExistsException | BucketAlreadyOwnedByYouException ignored) {
        }
    }

    @BeforeEach
    void createCommentIfNotExists() {
        Post post = postRepository.save(Post.builder()
                .authorId(USER_ID)
                .content("Test post")
                .build());

        comment = commentRepository.save(Comment.builder()
                .post(post)
                .authorId(USER_ID)
                .content("Test comment")
                .build());
    }

    @Test
    void shouldUploadAndDownloadImage() throws Exception {
        ClassPathResource resource = new ClassPathResource(TEST_IMAGE_PATH);
        byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());

        MockMultipartFile file = new MockMultipartFile(
                "file",
                TEST_IMAGE_NAME,
                TEST_IMAGE_TYPE,
                bytes
        );

        Long commentId = comment.getId();

        UserClientResponseDto user = new UserClientResponseDto(USER_ID, "name", "email");
        when(userServiceClient.getUserById(USER_ID)).thenReturn(user);

        String uploadResponse = mockMvc.perform(multipart(String.format("/api/v1/comments/%d/images", commentId))
                        .file(file)
                        .header("X-USER-ID", USER_ID))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.fileKey").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Integer imageId = JsonPath.read(uploadResponse, "$.imageId");

        mockMvc.perform(MockMvcRequestBuilders.get(
                                String.format("/api/v1/comments/%d/images/%s/view", commentId, imageId))
                        .header("X-USER-ID", USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG));
    }
}

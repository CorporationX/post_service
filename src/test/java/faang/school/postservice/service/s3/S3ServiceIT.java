package faang.school.postservice.service.s3;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserClientResponseDto;
import faang.school.postservice.entity.comment.Comment;
import faang.school.postservice.entity.post.Post;
import faang.school.postservice.repository.comment.CommentRepository;
import faang.school.postservice.repository.post.PostRepository;
import faang.school.postservice.service.config.TestContainersConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.FileCopyUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketAlreadyExistsException;
import software.amazon.awssdk.services.s3.model.BucketAlreadyOwnedByYouException;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

import java.io.IOException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class S3ServiceIT extends TestContainersConfig {

    private static final String BUCKET = "corpbucket";
    private static final String TEST_IMAGE_PATH = "test-images/kik.jpg";
    private static final String TEST_IMAGE_NAME = "kik.jpg";
    private static final String TEST_IMAGE_TYPE = "image/jpeg";
    private static final long USER_ID = 1L;

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
    private MockMultipartFile file;

    @BeforeEach
    void initBucket() {
        try {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(BUCKET).build());
        } catch (BucketAlreadyExistsException | BucketAlreadyOwnedByYouException ignored) {
        }
    }

    @BeforeEach
    void createCommentIfNotExists() throws IOException {
        Post post = new Post();
        post.setTitle("Test Title");
        post.setAuthorId(USER_ID);
        post.setContent("Test post");

        post = postRepository.save(post);

        comment = new Comment();
        comment.setAuthorId(USER_ID);
        comment.setContent("Test comment");
        comment.setPost(post);

        comment = commentRepository.save(comment);

        ClassPathResource resource = new ClassPathResource(TEST_IMAGE_PATH);
        byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());

        file = new MockMultipartFile(
                "file",
                TEST_IMAGE_NAME,
                TEST_IMAGE_TYPE,
                bytes
        );
    }

    @Test
    void shouldUploadAndDownloadImage() throws Exception {
        Long commentId = comment.getId();

        UserClientResponseDto user = new UserClientResponseDto(USER_ID, "name", "email");
        when(userServiceClient.getUserById(USER_ID)).thenReturn(user);

        mockMvc.perform(multipart(String.format("/api/v1/comments/%d/images", commentId))
                        .file(file)
                        .header("X-USER-ID", USER_ID))
                .andExpect(status().isAccepted())
                .andExpectAll(
                        jsonPath("$.fileKey").exists(),
                        jsonPath("$.previewKey").exists(),
                        jsonPath("$.contentType").exists(),
                        jsonPath("$.size").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        mockMvc.perform(MockMvcRequestBuilders.get(
                String.format("/api/v1/comments/%d/images/view", commentId))
                        .header("X-USER-ID", USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG));
    }
}

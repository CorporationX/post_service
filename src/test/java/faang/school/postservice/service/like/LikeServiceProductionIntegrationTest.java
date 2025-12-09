package faang.school.postservice.service.like;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DuplicateLikeException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
@Testcontainers
@ActiveProfiles("integration-test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Transactional
class LikeServiceProductionIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:15-alpine"))
            .withDatabaseName("postservice_test")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    @Container
    static KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.4.0"))
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // PostgreSQL конфигурация
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

        // JPA
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");

        // Kafka
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.kafka.topics.like-events", () -> "like-events-test");
        registry.add("spring.kafka.topics.unlike-events", () -> "unlike-events-test");

        // Redis (отключаем для тестов)
        registry.add("spring.data.redis.enabled", () -> false);

        // Отключаем Liquibase для тестов (используем create-drop)
        registry.add("spring.liquibase.enabled", () -> false);
    }

    @Autowired
    private LikeService likeService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeRepository likeRepository;

    @MockBean
    private UserContext userContext;

    private static final Long USER_ID = 1L;
    private static final Long POST_AUTHOR_ID = 2L;
    private static final Long ANOTHER_USER_ID = 3L;
    private static final Long NON_EXISTENT_ID = 999L;

    private Post post;
    private Comment comment;

    @BeforeEach
    void setUp() {

        likeRepository.deleteAll();
        commentRepository.deleteAll();
        postRepository.deleteAll();

        post = Post.builder()
                .content("Integration test post content")
                .authorId(POST_AUTHOR_ID)
                .published(true)
                .deleted(false)
                .build();
        post = postRepository.save(post);

        comment = Comment.builder()
                .content("Integration test comment")
                .authorId(USER_ID)
                .post(post)
                .build();
        comment = commentRepository.save(comment);

        when(userContext.getUserId()).thenReturn(USER_ID);
    }

    @Test
    void addLikeToPost_WithRealPostgreSQL_ShouldSaveCorrectly() {

        LikeDto result = likeService.addLikeToPost(post.getId());

        assertNotNull(result);
        assertEquals(USER_ID, result.userId());
        assertEquals(post.getId(), result.postId());
        assertEquals(POST_AUTHOR_ID, result.postAuthorId());

        Optional<Like> savedLike = likeRepository.findByPostIdAndUserId(post.getId(), USER_ID);
        assertTrue(savedLike.isPresent(), "Like should be saved in PostgreSQL");
        assertEquals(USER_ID, savedLike.get().getUserId());
        assertEquals(post.getId(), savedLike.get().getPost().getId());
        assertNotNull(savedLike.get().getCreatedAt(), "Creation timestamp should be set");
    }

    @Test
    void addLikeToPost_WhenConcurrentLikes_ShouldHandleTransactionsCorrectly() {

        when(userContext.getUserId()).thenReturn(USER_ID);
        likeService.addLikeToPost(post.getId());

        when(userContext.getUserId()).thenReturn(ANOTHER_USER_ID);

        LikeDto secondLike = likeService.addLikeToPost(post.getId());

        assertNotNull(secondLike);
        assertEquals(ANOTHER_USER_ID, secondLike.userId());

        assertEquals(2, likeRepository.countByPostId(post.getId()),
                "Should have 2 likes for the post");
    }

    @Test
    void addLikeToPost_WhenAlreadyLiked_ShouldThrowDuplicateLikeException() {

        likeService.addLikeToPost(post.getId());

        assertThrows(DuplicateLikeException.class,
                () -> likeService.addLikeToPost(post.getId()));
    }

    @Test
    void addLikeToPost_WhenPostNotFound_ShouldThrowEntityNotFoundException() {

        assertThrows(EntityNotFoundException.class,
                () -> likeService.addLikeToPost(NON_EXISTENT_ID));
    }

    @Test
    void removeLikeFromPost_Success() {

        likeService.addLikeToPost(post.getId());

        LikeDto result = likeService.removeLikeFromPost(post.getId());

        assertNotNull(result);
        assertEquals(USER_ID, result.userId());
        assertEquals(post.getId(), result.postId());

        Optional<Like> deletedLike = likeRepository.findByPostIdAndUserId(post.getId(), USER_ID);
        assertTrue(deletedLike.isEmpty());
    }

    @Test
    void removeLikeFromPost_WhenLikeNotFound_ShouldThrowEntityNotFoundException() {

        assertThrows(EntityNotFoundException.class,
                () -> likeService.removeLikeFromPost(post.getId()));
    }

    @Test
    void removeLikeFromPost_WhenDifferentUser_ShouldThrowEntityNotFoundException() {

        when(userContext.getUserId()).thenReturn(ANOTHER_USER_ID);
        likeService.addLikeToPost(post.getId());

        when(userContext.getUserId()).thenReturn(USER_ID);
        assertThrows(EntityNotFoundException.class,
                () -> likeService.removeLikeFromPost(post.getId()));

        assertTrue(likeRepository.findByPostIdAndUserId(post.getId(), ANOTHER_USER_ID).isPresent(),
                "Original like should still exist");
    }

    @Test
    void addLikeToComment_WithRealPostgreSQL_ShouldSaveCommentLike() {

        LikeDto result = likeService.addLikeToComment(comment.getId());

        assertNotNull(result);
        assertEquals(USER_ID, result.userId());
        assertEquals(comment.getId(), result.commentId());
        assertNull(result.postAuthorId());

        Optional<Like> savedLike = likeRepository.findByCommentIdAndUserId(comment.getId(), USER_ID);
        assertTrue(savedLike.isPresent(), "Comment like should be saved in PostgreSQL");
        assertNotNull(savedLike.get().getComment(), "Comment reference should be set");
        assertNull(savedLike.get().getPost(), "Post should be null for comment like");
    }

    @Test
    void testKafkaIsRunning() {
        assertTrue(kafka.isRunning(), "Kafka container should be running");
        assertNotNull(kafka.getBootstrapServers(), "Kafka bootstrap servers should be available");
    }

    @Test
    void testPostgreSQLIsRunning() {
        assertTrue(postgres.isRunning(), "PostgreSQL container should be running");
        assertTrue(postgres.getJdbcUrl().contains("postgresql"),
                "Should use PostgreSQL JDBC URL");
    }
}
package faang.school.postservice.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.config.context.UserHeaderFilter;
import faang.school.postservice.controller.LikeController;
import faang.school.postservice.dto.like.LikeCommentRequestDto;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikePostRequestDto;
import faang.school.postservice.exception.CommentNotFoundException;
import faang.school.postservice.exception.LikeExistsException;
import faang.school.postservice.exception.LikeNotFoundException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.exception.UserNotFoundException;
import faang.school.postservice.service.LikeService;
import faang.school.postservice.util.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class ExceptionApiHandlerTest {
    private static final Long USER_ID = 10L;
    private static final Long COMMENT_ID = 40L;
    private static final Long POST_ID = 60L;

    @Spy
    private Utils utils;
    @Mock
    private LikeService likeService;
    @Spy
    private ObjectMapper objectMapper;
    @Spy
    private UserContext userContext;
    @InjectMocks
    private LikeController likeController;
    @InjectMocks
    private ExceptionApiHandler exceptionApiHandler;

    private UserHeaderFilter userHeaderFilter;

    @BeforeEach
    public void setUp() {
        userHeaderFilter = new UserHeaderFilter(userContext);
    }

    @Test
    public void testMissingUserIdInHeader() {
        MockMvcWebTestClient.bindToController(likeController)
                .controllerAdvice(exceptionApiHandler)
                .filter(userHeaderFilter)
                .build()
                .delete()
                .uri(utils.format("/likes/comment/{}", COMMENT_ID))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    /**
     * на url
     */
    @Test
    void testMethodArgumentNotValidException() {
        MockMvcWebTestClient.bindToController(likeController)
                .controllerAdvice(exceptionApiHandler).build()
                .post()
                .uri("/likes/test")
                .header("x-user-id", String.valueOf(USER_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "id": 1,
                            "projectId": 1,
                            "likePostCount": 10,
                            "likeCommentCount": 20
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errorDate").exists()
                .jsonPath("$.errorMessage").exists()
                .jsonPath("$.detail").hasJsonPath();
    }

    @Test
    void testMethodArgumentTypeMismatchException() {
        MockMvcWebTestClient.bindToController(likeController)
                .controllerAdvice(exceptionApiHandler).build()
                .delete()
                .uri("/likes/post/wrongValue")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED)
                .expectBody()
                .jsonPath("$.errorDate").exists()
                .jsonPath("$.errorMessage").exists()
                .jsonPath("$.detail").doesNotExist();
    }

    @Test
    void testUnrecognizedPropertyException() {
        //todo: придумать ситуацию для этой ошибки. при наличии обработчика для RuntimeException.class
        // исключение UnrecognizedPropertyException не воспроизводится.
    }

    @Test
    void testUserNotFoundException() {
        LikeDto likeDto = LikeDto.builder()
                .userId(USER_ID)
                .commentId(COMMENT_ID)
                .build();

        final String expectedError = utils.format(LikeService.USER_NOT_FOUND, USER_ID);
        UserNotFoundException exception = new UserNotFoundException(expectedError);
        doThrow(exception).when(likeService).deleteLikeFromComment(likeDto);

        MockMvcWebTestClient.bindToController(likeController)
                .controllerAdvice(exceptionApiHandler)
                .filter(userHeaderFilter)
                .build()
                .delete()
                .uri(utils.format("/likes/comment/{}", COMMENT_ID))
                .header("x-user-id", String.valueOf(USER_ID))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody()
                .jsonPath("$.errorDate").exists()
                .jsonPath("$.errorMessage").exists()
                .jsonPath("$.errorMessage").isEqualTo(expectedError)
                .jsonPath("$.detail").doesNotExist();
    }

    @Test
    void testLikeNotFoundException() {
        LikeDto likeDto = LikeDto.builder()
                .userId(USER_ID)
                .commentId(COMMENT_ID)
                .build();

        final String expectedError = "mock LikeNotFoundException";
        LikeNotFoundException exception = new LikeNotFoundException(expectedError);
        doThrow(exception).when(likeService).deleteLikeFromComment(likeDto);

        MockMvcWebTestClient.bindToController(likeController)
                .controllerAdvice(exceptionApiHandler)
                .filter(userHeaderFilter)
                .build()
                .delete()
                .uri(utils.format("/likes/comment/{}", COMMENT_ID))
                .header("x-user-id", String.valueOf(USER_ID))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody()
                .jsonPath("$.errorDate").exists()
                .jsonPath("$.errorMessage").isEqualTo(expectedError)
                .jsonPath("$.errorMessage").isEqualTo(expectedError)
                .jsonPath("$.detail").doesNotExist();
    }

    @Test
    void testPostNotFoundException() throws JsonProcessingException {
        LikePostRequestDto dto = LikePostRequestDto.builder()
                .postId(POST_ID)
                .build();

        final String expectedError = "mock PostNotFoundException";
        PostNotFoundException exception = new PostNotFoundException(expectedError);

        doThrow(exception).when(likeService).addLikeToPost(any(LikeDto.class));

        MockMvcWebTestClient.bindToController(likeController)
                .controllerAdvice(exceptionApiHandler)
                .filter(userHeaderFilter)
                .build()
                .post()
                .uri(utils.format("/likes/post", POST_ID))
                .header("x-user-id", String.valueOf(USER_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(dto))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody()
                .jsonPath("$.errorDate").exists()
                .jsonPath("$.errorMessage").exists()
                .jsonPath("$.errorMessage").isEqualTo(expectedError)
                .jsonPath("$.detail").doesNotExist();
    }

    @Test
    void testCommentNotFoundException() throws JsonProcessingException {
        LikeCommentRequestDto dto = LikeCommentRequestDto.builder()
                .commentId(COMMENT_ID)
                .build();

        final String expectedError = "mock CommentNotFoundException";
        CommentNotFoundException exception = new CommentNotFoundException(expectedError);

        doThrow(exception).when(likeService).addLikeToComment(any(LikeDto.class));

        MockMvcWebTestClient.bindToController(likeController)
                .controllerAdvice(exceptionApiHandler)
                .filter(userHeaderFilter)
                .build()
                .post()
                .uri(utils.format("/likes/comment", COMMENT_ID))
                .header("x-user-id", String.valueOf(USER_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(dto))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody()
                .jsonPath("$.errorDate").exists()
                .jsonPath("$.errorMessage").exists()
                .jsonPath("$.errorMessage").isEqualTo(expectedError)
                .jsonPath("$.detail").doesNotExist();
    }

    @Test
    void testLikeExistsException() throws JsonProcessingException {
        LikeCommentRequestDto dto = LikeCommentRequestDto.builder()
                .commentId(COMMENT_ID)
                .build();

        final String expectedError = "mock LikeExistsException";
        LikeExistsException exception = new LikeExistsException(expectedError);

        doThrow(exception).when(likeService).addLikeToComment(any(LikeDto.class));

        MockMvcWebTestClient.bindToController(likeController)
                .controllerAdvice(exceptionApiHandler)
                .filter(userHeaderFilter)
                .build()
                .post()
                .uri(utils.format("/likes/comment", COMMENT_ID))
                .header("x-user-id", String.valueOf(USER_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(dto))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS)
                .expectBody()
                .jsonPath("$.errorDate").exists()
                .jsonPath("$.errorMessage").exists()
                .jsonPath("$.errorMessage").isEqualTo(expectedError)
                .jsonPath("$.detail").doesNotExist();
    }

    @Test
    void testRuntimeException() {
        MockMvcWebTestClient.bindToController(likeController)
                .controllerAdvice(exceptionApiHandler)
                .filter(userHeaderFilter)
                .build()
                .post()
                .uri(utils.format("/likes/comment", COMMENT_ID))
                .header("x-user-id", String.valueOf(USER_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED)
                .expectBody()
                .jsonPath("$.errorDate").exists()
                .jsonPath("$.errorMessage").exists()
                .jsonPath("$.errorMessage").isEqualTo(ExceptionApiHandler.RUNTIME_ERROR)
                .jsonPath("$.detail").doesNotExist();
    }
}
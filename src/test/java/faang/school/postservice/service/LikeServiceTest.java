package faang.school.postservice.service;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.integration.UserService;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.messaging.kafka.events.LikeEvent;
import faang.school.postservice.messaging.kafka.publishing.like.LikeProducer;
import faang.school.postservice.messaging.kafka.publishing.like.UnlikeProducer;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.messaging.redis.publisher.LikeEventPublisher;
import faang.school.postservice.repository.LikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {
    private static final long LIKE_ID = 23L;
    private static final long COMMENT_ID = 23L;
    private static final long POST_ID = 57L;
    private static final long USER_ID = 1L;

    @InjectMocks
    private LikeService likeService;

    @Mock
    private UserService userService;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private LikeMapper likeMapper;

    @Mock
    private PostService postService;

    @Mock
    private CommentService commentService;

    @Mock
    private UnlikeProducer unlikeProducer;

    @Mock
    private LikeProducer likeProducer;

    @Mock
    private LikeEventPublisher likeEventPublisher;

    private UserDto userDto;
    private LikeDto likeDto;


    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(USER_ID);

        likeDto = new LikeDto();
        likeDto.setId(LIKE_ID);

        when(userService.getUser()).thenReturn(userDto);
    }


    @Nested
    @DisplayName("like and unlike for Post")
    class LikeUnlikePostTest {
        private Post post;
        private Like like;

        @BeforeEach
        void setUp() {
            likeDto.setPostId(POST_ID);

            post = new Post();
            post.setId(POST_ID);

            like = new Like();
            like.setUserId(USER_ID);
            like.setPost(post);
        }

        @Nested
        class LikePostTest {

            @Test
            public void whenLikeIsNotExistingThenLikePost() {
                when(postService.getPostIfExist(POST_ID)).thenReturn(post);
                when(likeRepository.findByPostIdAndUserId(POST_ID, userDto.getId())).thenReturn(Optional.empty());
                when(likeRepository.save(like)).thenReturn(like);
                when(likeMapper.toDto(like)).thenReturn(likeDto);
                doNothing().when(likeProducer).publish((LikeEvent) any(Object.class));

                LikeDto result = likeService.likePost(likeDto);
                assertNotNull(result);
                assertEquals(likeDto, result);
            }

            @Test
            public void whenLikeAlreadyExistsThenThrowExc() {
                when(postService.getPostIfExist(POST_ID)).thenReturn(post);
                when(likeRepository.findByPostIdAndUserId(POST_ID, userDto.getId())).thenReturn(Optional.of(like));

                assertThrows(DataValidationException.class, () -> likeService.likePost(likeDto));
            }
        }

        @Nested
        class UnlikePostTest {

            @Test
            public void whenLikeIsExistThenUnlikePost() {
                when(likeRepository.findByPostIdAndUserId(POST_ID, userDto.getId())).thenReturn(Optional.of(like));
                doNothing().when(unlikeProducer).publish((LikeEvent) any(Object.class));
                likeService.unlikePost(POST_ID);
                verify(likeRepository).delete(like);
            }

            @Test
            public void whenLikeIsNotExistThenThrowExc() {
                when(likeRepository.findByPostIdAndUserId(POST_ID, userDto.getId())).thenReturn(Optional.empty());
                assertThrows(EntityNotFoundException.class, () -> likeService.unlikePost(POST_ID));
            }
        }
    }

    @Nested
    @DisplayName("like and unlike for Comment")
    class LikeUnlikeCommentTest {
        private Comment comment;
        private Like like;

        @BeforeEach
        void setUp() {
            likeDto.setCommentId(COMMENT_ID);

            comment = new Comment();
            comment.setId(COMMENT_ID);

            like = new Like();
            like.setUserId(USER_ID);
            like.setComment(comment);
        }

        @Test
        public void whenLikeIsNotExistingThenLikeComment() {
            when(userService.getUser()).thenReturn(userDto);
            when(commentService.findExistingComment(COMMENT_ID)).thenReturn(comment);
            when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, userDto.getId())).thenReturn(Optional.empty());
            when(likeRepository.save(like)).thenReturn(like);
            when(likeMapper.toDto(like)).thenReturn(likeDto);

            LikeDto result = likeService.likeComment(likeDto);
            assertNotNull(result);
            assertEquals(likeDto, result);
        }

        @Test
        public void whenLikeAlreadyExistsThenThrowExc() {
            when(userService.getUser()).thenReturn(userDto);
            when(commentService.findExistingComment(COMMENT_ID)).thenReturn(comment);
            when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, userDto.getId())).thenReturn(Optional.of(like));

            assertThrows(DataValidationException.class, () -> likeService.likeComment(likeDto));
        }

        @Nested
        class UnlikeCommentTest {

            @Test
            public void whenLikeIsExistThenUnlikeComment() {
                when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, userDto.getId())).thenReturn(Optional.of(like));
                likeService.unlikeComment(COMMENT_ID);
                verify(likeRepository).delete(like);
            }

            @Test
            public void whenLikeIsNotExistThenThrowExc() {
                when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, userDto.getId())).thenReturn(Optional.empty());
                assertThrows(EntityNotFoundException.class, () -> likeService.unlikeComment(COMMENT_ID));
            }
        }
    }
}
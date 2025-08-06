package faang.school.postservice.util;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDraftDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.integration.user.service.UserClient;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
        @InjectMocks
        PostService postService;
        @Mock
        private UserContext userContext;
        @Mock
        private PostRepository postRepository;
        @Mock
        private UserClient userClient;
        @Spy
        private PostMapperImpl mapper;
        @Captor
        private ArgumentCaptor<Post> postCaptor;

        private static final long POST_ID = 1;
        private static final long USER_ID = 1;
        private static final long AUTHOR_ID = 1;
        private static final long OTHER_USER_ID = 4;
        private static final String CONTENT = "Test";
        private static final String NEW_CONTENT = "New test content";

        private PostDto postDto;
        private PostDraftDto postDraftDto;

        @BeforeEach
        void setUp() {
            postDto = preparePostDto(null, CONTENT);
        }

        @Test
        @DisplayName("Успешное создание черновика поста")
        void positive_testCreatePost() {
            PostDto expected = preparePostDto(POST_ID, CONTENT);
            preparePositiveCreateBehavior();

            PostDraftDto actual = postService.createPostDraft(postDraftDto); //Как проверить что пост создался не меняя метод в Сервисе?

            verify(postRepository, times(1)).save(postCaptor.capture());
            assertNotNull(actual);
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("Успешное обновление поста")
        void positive_testUpdatePostById() {
            PostDto postDto = preparePostDto(null, NEW_CONTENT);
            PostDto expected = preparePostDto(POST_ID, NEW_CONTENT);
            preparePositiveUpdateBehavior();

            PostDto actual = PostService.update(POST_ID, postDto);

            verify(postRepository, times(1)).save(postCaptor.capture());
            assertNotNull(actual);
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("Успешное удаление поста")
        void positive_testDeletePostById() {
            preparePositiveDeleteBehavior();

            postService.delete(POST_ID);
            verify(postRepository, times(1)).deleteById(POST_ID);
        }

//        @Test
//        @DisplayName("Успешное получение комментариев по id поста")
//        void positive_testFindAllPostByPostId() {
//            List<PostDto> expected  = List.of(preparePostDto(POST_ID, CONTENT));
//            when(postRepository.findAllByPostIdOrderByCreatedAtDesc(POST_ID))
//                    .thenReturn(List.of(prepareExistsPost().get()));
//
//            List<PostDto> actual = postService.findAllByPostId(POST_ID);
//
//            verify(postRepository, times(1)).findAllByPostIdOrderByCreatedAtDesc(POST_ID);
//            assertNotNull(actual);
//            assertEquals(expected, actual);
//        }

//        @Test
//        @DisplayName("Ошибка создания поста - юзер не автор")
//        void negative_whenUserNotOwner_createThrowsException() {
//            when(userContext.getUserId()).thenReturn(OTHER_USER_ID);
//
//            verify(postRepository, never()).save(any(Post.class));
//            assertThrows(NotResourceOwnerException.class,
//                    () -> postService.create(postDto));
//        }

        @Test
        @DisplayName("Ошибка создания поста - пользователь не найден")
        void negative_whenUserNotFound_createThrowsException() {
            String expectedMessage = "User " + USER_ID + " not found";
            when(userContext.getUserId()).thenReturn(USER_ID);
            when(postRepository.findById(POST_ID)).thenReturn(preparePost());

            verify(postRepository, never()).save(any(Post.class));
            String actualMessage = assertThrows(EntityNotFoundException.class,
                    () -> postService.createPostDraft(postDraftDto)).getMessage();
            assertEquals(expectedMessage, actualMessage);
        }

//        @Test
//        @DisplayName("Ошибка создания комментария - пост не найден")
//        void negative_whenPostNotFound_createThrowsException() {
//            String expectedMessage = "Post " + POST_ID + " not found";
//            when(userContext.getUserId()).thenReturn(USER_ID);
//            when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());
//
//            verify(postRepository, never()).save(any(Post.class));
//            String actualMessage = assertThrows(EntityNotFoundException.class,
//                    () -> postService.create(postDto)).getMessage();
//            assertEquals(expectedMessage, actualMessage);
//        }

        @Test
        @DisplayName("Ошибка обновления поста - юзер не автор")
        void negative_whenUserNotOwner_updateThrowsException() {
            when(userContext.getUserId()).thenReturn(OTHER_USER_ID);

            verify(postRepository, never()).save(any(Post.class));
            assertThrows(NotResourceOwnerException.class,
                    () -> postService.update(POST_ID, postDto));
        }

        @Test
        @DisplayName("Ошибка обновления поста - юзер не найден")
        void negative_whenUserNotFound_updateThrowsException() {
            String expectedMessage = "User " + USER_ID + " not found";
            when(userContext.getUserId()).thenReturn(USER_ID);
            when(postRepository.findById(POST_ID)).thenReturn(prepareExistsPost());

            verify(postRepository, never()).save(any(Post.class));
            String actualMessage = assertThrows(EntityNotFoundException.class,
                    () -> postService.update(POST_ID, postDto)).getMessage();
            assertEquals(expectedMessage, actualMessage);
        }

        @Test
        @DisplayName("Ошибка обновления поста - пост не найден")
        void negative_whenPostNotFound_updateThrowsException() {
            String expectedMessage = "Post " + POST_ID + " not found";
            when(userContext.getPostId()).thenReturn(POST_ID);
            when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());

            verify(postRepository, never()).save(any(Post.class));
            String actualMessage = assertThrows(EntityNotFoundException.class,
                    () -> postService.update(POST_ID, postDto)).getMessage();
            assertEquals(expectedMessage, actualMessage);
        }

        @Test
        @DisplayName("Ошибка удаления поста - юзер не автор")
        void negative_whenUserNotOwner_deleteThrowsException() {
            when(userContext.getUserId()).thenReturn(OTHER_USER_ID);
            when(postRepository.findById(POST_ID)).thenReturn(prepareExistsPost());

            verify(postRepository, never()).deleteById(anyLong());
            assertThrows(NotResourceOwnerException.class,
                    () -> postService.delete(POST_ID));
        }

        @Test
        @DisplayName("Ошибка удаления поста - юзер не найден")
        void negative_whenUserNotFound_deleteThrowsException() {
            String expectedMessage = "User " + USER_ID + " not found";
            when(userContext.getUserId()).thenReturn(USER_ID);
            when(postRepository.findById(POST_ID)).thenReturn(prepareExistsPost());

            verify(postRepository, never()).deleteById(anyLong());
            String actualMessage = assertThrows(EntityNotFoundException.class,
                    () -> postService.delete(POST_ID)).getMessage();
            assertEquals(expectedMessage, actualMessage);
        }

        @Test
        @DisplayName("Ошибка удаления поста - пост не найден")
        void negative_whenPostNotFound_deleteThrowsException() {
            String expectedMessage = "Post " + POST_ID + " not found";
            when(userContext.getUserId()).thenReturn(USER_ID);
            when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());

            verify(postRepository, never()).deleteById(anyLong());
            String actualMessage = assertThrows(EntityNotFoundException.class,
                    () -> postService.delete(POST_ID)).getMessage();
            assertEquals(expectedMessage, actualMessage);
        }

        // ----------------------

        private PostDto preparePostDto(Long postId, String content) {
            return new PostDto(postId, content, POST_ID, 0, POST_ID, null, null);
        }

        private PostDto prepareUser() {
            return new PostDto(USER_ID, null, null);
        }

        private Optional<Post> preparePost() {
            return Optional.of(Post.builder()
                    .id(POST_ID)
                    .build());
        }

        private Optional<Post> prepareExistsPost() {
            return Optional.of(Post.builder()
                    .id(POST_ID)
                    .content(CONTENT)
                    .authorId(USER_ID)
                    .post(preparePost().get())
                    .build());
        }

        private void preparePositiveCreateBehavior() {
            prepareCommonPositiveBehavior();
            when(postRepository.findById(POST_ID)).thenReturn(preparePost());
            when(postRepository.save(postCaptor.capture()))
                    .thenAnswer(invocation -> {
                        Post post = postCaptor.getValue();
                        post.setId(POST_ID);
                        return post;
                    });
        }

        private void preparePositiveUpdateBehavior() {
            prepareCommonPositiveBehavior();
            when(postRepository.findById(POST_ID)).thenReturn(prepareExistsPost());
            when(postRepository.save(postCaptor.capture()))
                    .thenAnswer(invocation -> {
                        Post post = postCaptor.getValue();
                        post.setId(POST_ID);
                        return post;
                    });
        }

        private void preparePositiveDeleteBehavior() {
            prepareCommonPositiveBehavior();
            when(postRepository.findById(POST_ID)).thenReturn(prepareExistsPost());
        }

        private void prepareCommonPositiveBehavior() {
            when(userContext.getUserId()).thenReturn(USER_ID);
            when(userClient.getUser(USER_ID)).thenReturn(prepareUser());
        }
    }

}
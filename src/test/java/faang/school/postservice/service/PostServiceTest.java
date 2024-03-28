package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.AlreadyDeletedException;
import faang.school.postservice.exception.AlreadyPostedException;
import faang.school.postservice.exception.NoPublishedPostException;
import faang.school.postservice.exception.SamePostAuthorException;
import faang.school.postservice.exception.UpdatePostException;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.moderation.ModerationDictionary;
import faang.school.postservice.validator.PostValidator;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
//
//    @Spy
//    private PostMapperImpl postMapper;
//    @Mock
//    private PostRepository postRepository;
//    @Mock
//    private UserServiceClient userService;
//    @Mock
//    private ProjectServiceClient projectService;
//    @Mock
//    private ModerationDictionary moderationDictionary;
//    @Mock
//    private Executor threadPoolForPostModeration;
//    @Mock
//    private PublisherService publisherService;
//    private PostValidator postValidator;
//    private PostService postService;
//
//    private PostDto incorrectPostDto;
//    private PostDto correctPostDto;
//    private Post alreadyPublishedPost;
//    private Post correctPost;
//    private Post post1;
//    private Post post2;
//    private Post post3;
//    private final Long CORRECT_ID = 1L;
//    private final long INCORRECT_ID = 0;
//
//    @BeforeEach
//    void initData() {
//        postValidator = new PostValidator(userService, projectService, postRepository);
//        postService = new PostService(postRepository, postValidator, postMapper, moderationDictionary, threadPoolForPostModeration, publisherService);
//        incorrectPostDto = PostDto.builder()
//                .id(INCORRECT_ID)
//                .content("content")
//                .projectId(CORRECT_ID)
//                .authorId(CORRECT_ID)
//                .build();
//        correctPostDto = PostDto.builder()
//                .id(CORRECT_ID)
//                .content("content")
//                .authorId(CORRECT_ID)
//                .build();
//        alreadyPublishedPost = Post.builder()
//                .id(2L)
//                .published(true)
//                .build();
//        correctPost = Post.builder()
//                .content("content")
//                .id(CORRECT_ID)
//                .authorId(CORRECT_ID)
//                .build();
//    }
//
//    @Test
//    void testCreateDaftPostWithSameAuthor() {
//        assertThrows(SamePostAuthorException.class, () -> postService.crateDraftPost(incorrectPostDto));
//    }
//
//    @Test
//    void testCreateDaftPostWithNonExistentUser() {
//        incorrectPostDto.setProjectId(null);
//        when(userService.getUser(CORRECT_ID)).thenThrow(FeignException.class);
//
//        assertThrows(EntityNotFoundException.class, () -> postService.crateDraftPost(incorrectPostDto));
//    }
//
//    @Test
//    void testCreateDaftPostWithNonExistentProject() {
//        incorrectPostDto.setAuthorId(null);
//        when(projectService.getProject(CORRECT_ID)).thenThrow(FeignException.class);
//
//        assertThrows(EntityNotFoundException.class, () -> postService.crateDraftPost(incorrectPostDto));
//    }
//
//    @Test
//    void testCreateDaftPost() {
//        Post post = postMapper.toEntity(correctPostDto);
//        when(postRepository.save(post)).thenReturn(post);
//
//        PostDto actualPostDto = postService.crateDraftPost(correctPostDto);
//        assertEquals(correctPostDto, actualPostDto);
//    }
//
//    @Test
//    void testPublishPostWithoutPostInDB() {
//        when(postRepository.findById(CORRECT_ID)).thenThrow(EntityNotFoundException.class);
//        assertThrows(EntityNotFoundException.class, () -> postService.publishPost(CORRECT_ID));
//    }
//
//    @Test
//    void testPublishPostWithAlreadyPublishedPost() {
//        when(postRepository.findById(CORRECT_ID)).thenReturn(Optional.ofNullable(alreadyPublishedPost));
//        assertThrows(AlreadyPostedException.class, () -> postService.publishPost(CORRECT_ID));
//    }
//
//    @Test
//    void testPublishedPostWithDeletedPost() {
//        correctPost.setDeleted(true);
//        returnCorrectPostForPostRepository();
//        assertThrows(AlreadyDeletedException.class, () -> postService.publishPost(CORRECT_ID));
//    }
//
//    @Test
//    void testPublishPost() {
//        correctPostDto.setPublishedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
//        correctPostDto.setPublished(true);
//        when(postRepository.findById(CORRECT_ID)).thenReturn(Optional.ofNullable(correctPost));
//
//        PostDto actualPostDto = postService.publishPost(CORRECT_ID);
//        actualPostDto.setPublishedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
//        assertEquals(correctPostDto, actualPostDto);
//    }
//
//    @Test
//    void testUpdatePostWithoutPostInDB() {
//        when(postRepository.findById(INCORRECT_ID)).thenThrow(EntityNotFoundException.class);
//        assertThrows(EntityNotFoundException.class, () -> postService.updatePost(incorrectPostDto));
//    }
//
//    @Test
//    void testUpdatePostWithIncorrectAuthor() {
//        correctPostDto.setAuthorId(2L);
//        returnCorrectPostForPostRepository();
//
//        assertThrows(UpdatePostException.class, () -> postService.updatePost(correctPostDto));
//    }
//
//    @Test
//    void testUpdatePost() {
//        correctPostDto.setContent("other content");
//        correctPostDto.setScheduledAt(LocalDateTime.now().plusMonths(1));
//        correctPost.setScheduledAt(LocalDateTime.now());
//        returnCorrectPostForPostRepository();
//
//        PostDto actualPostDto = postService.updatePost(correctPostDto);
//        assertEquals(correctPostDto, actualPostDto);
//    }
//
//    @Test
//    void testSoftDeleteWithoutPostInDB() {
//        when(postRepository.findById(CORRECT_ID)).thenThrow(EntityNotFoundException.class);
//        assertThrows(EntityNotFoundException.class, () -> postService.softDelete(CORRECT_ID));
//    }
//
//    @Test
//    void testSoftDeleteWithAlreadyDeletedPost() {
//        correctPost.setDeleted(true);
//        returnCorrectPostForPostRepository();
//
//        assertThrows(AlreadyDeletedException.class, () -> postService.softDelete(CORRECT_ID));
//    }
//
//    @Test
//    void testSoftDelete() {
//        correctPostDto.setDeleted(true);
//        returnCorrectPostForPostRepository();
//
//        PostDto actualPostDto = postService.softDelete(CORRECT_ID);
//        assertEquals(correctPostDto, actualPostDto);
//    }
//
//    @Test
//    void testGetPostWithoutPostInDB() {
//        when(postRepository.findById(CORRECT_ID)).thenThrow(EntityNotFoundException.class);
//        assertThrows(EntityNotFoundException.class, () -> postService.getPost(CORRECT_ID));
//    }
//
//    @Test
//    void testGetPostAlreadyDeleted() {
//        correctPost.setDeleted(true);
//        returnCorrectPostForPostRepository();
//
//        assertThrows(AlreadyDeletedException.class, () -> postService.getPost(CORRECT_ID));
//    }
//
//    @Test
//    void testGetPostWhichNoPublished() {
//        returnCorrectPostForPostRepository();
//        assertThrows(NoPublishedPostException.class, () -> postService.getPost(CORRECT_ID));
//    }
//
//    @Test
//    void testGetPost() {
//        correctPost.setPublished(true);
//        correctPostDto.setPublished(true);
//        returnCorrectPostForPostRepository();
//
//        PostDto actualPostDto = postService.getPost(CORRECT_ID);
//        assertEquals(correctPostDto, actualPostDto);
//    }
//
//    @Test
//    void testDoPostModeration() {
//        when(postRepository.findNotVerified()).thenReturn(getNotVerifiedPosts());
//        ReflectionTestUtils.setField(postService, "sublistSize", 100);
//        postService.doPostModeration();
//
//        verify(threadPoolForPostModeration).execute(any());
//    }
//
//    @Test
//    void testDoPostModerationWithAsync() {
//        when(postRepository.findNotVerified()).thenReturn(getNotVerifiedPosts());
//        ReflectionTestUtils.setField(postService, "sublistSize", 2);
//        postService.doPostModeration();
//
//        verify(threadPoolForPostModeration, times(2)).execute(any());
//    }
//
//    private void returnCorrectPostForPostRepository() {
//        when(postRepository.findById(CORRECT_ID)).thenReturn(Optional.ofNullable(correctPost));
//    }
//
//    private List<Post> getNotVerifiedPosts() {
//        post1 = Post.builder()
//                .content("some content")
//                .build();
//        post2 = Post.builder()
//                .content("post number two")
//                .build();
//        post3 = Post.builder()
//                .content("This is the best bootcamp!")
//                .build();
//        return List.of(post1, post2, post3);
//    }
}
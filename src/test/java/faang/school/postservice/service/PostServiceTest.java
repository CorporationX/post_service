package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.validator.PostServiceValidator;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostServiceValidator validator;

    @Mock
    private PostRepository postRepository;

    @Spy
    private PostMapper postMapper;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        Mockito.lenient().doNothing().when(validator).validateToAdd(Mockito.any());
    }

//    @Test
//    void addPost_InputsAreCorrect_ShouldGetCorrectPost() {
//        PostDto postDto = buildPostDto();
//
//        postService.addPost(postDto);
//
//        Assert.assertEquals(buildPost(), );
//    }

    @Test
    void addPost_ShouldSave() {
        PostDto postDto = buildPostDto();

        postService.addPost(postDto);

        Mockito.verify(postMapper, Mockito.times(1)).toDto(Mockito.any());
    }

    @Test
    void publishPost_FieldsShouldBeSet() {
        Post post = buildPost();
        Mockito.when(validator.validateToPublish(1L)).thenReturn(post);

        postService.publishPost(1L);

        Assertions.assertTrue(post.isPublished());
        Assertions.assertTrue(post.getPublishedAt().isAfter(LocalDateTime.now().minusSeconds(2))); // тут не уверен, что стоит так делать
    }

    @Test
    void publishPost_ShouldPublish() {
        Post post = buildPost();
        Mockito.when(validator.validateToPublish(1L)).thenReturn(post);

        postService.publishPost(1L);

        Mockito.verify(postRepository, Mockito.times(1)).save(post);
    }

    @Test
    void updatePost_FieldsShouldBeSet() {
        Post post = buildPost();
        Mockito.when(validator.validateToUpdate(1L, "content")).thenReturn(post);

        postService.updatePost(1L, "content");

        Assertions.assertEquals("content", post.getContent());
        Assertions.assertTrue(post.getUpdatedAt().isAfter(LocalDateTime.now().minusSeconds(2))); // тут не уверен, что стоит так делать
    }

    @Test
    void updatePost_ShouldPublish() {
        Post post = buildPost();
        Mockito.when(validator.validateToUpdate(1L, "content")).thenReturn(post);

        postService.updatePost(1L, "content");

        Mockito.verify(postRepository, Mockito.times(1)).save(post);
    }

    private PostDto buildPostDto() {
        return PostDto.builder()
                .content("content")
                .authorId(1L)
                .adId(1L)
                .build();
    }

    private Post buildPost() {
        return Post.builder()
                .id(0)
                .content("content")
                .authorId(1L)
                .ad(Ad.builder().id(1L).build())
                .likes(new ArrayList<>())
                .comments(new ArrayList<>())
                .albums(new ArrayList<>())
                .published(false)
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .build();
    }
}

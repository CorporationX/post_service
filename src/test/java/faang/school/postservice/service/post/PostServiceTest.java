package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.post.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.utils.PostServiceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostServiceUtils postServiceUtils;
    @Spy
    private PostMapperImpl postMapper;
    @InjectMocks
    private PostService postService;

    private Post testPost;
    private CreatePostDto createPostDto;
    private PostDto postDto;
    private LocalDateTime now = LocalDateTime.now();

        @BeforeEach
        void setUp() {
            testPost = Post.builder()
                .id(1L)
                .content("Test content")
                .authorId(1L)
                .createdAt(now)
                .updatedAt(now)
                .build();

            createPostDto = CreatePostDto.builder()
                .content("Test content")
                .authorId(1L)
                .build();

            postDto = PostDto.builder()
                .id(1L)
                .content("Test content")
                .authorId(1L)
                .published(false)
                .build();
        }

        @Test
        void testCreateTrue() {
            doNothing().when(postServiceUtils).isAuthorOrProjectAdded(createPostDto);
            Post post = postMapper.toEntity(createPostDto);
            when(postRepository.save(post)).thenReturn(testPost);
            assertThat(postService.create(createPostDto)).isEqualTo(postDto);
        }

        @Test
        void testPublishPostFalseFromStart() {
            testPost.setPublished(false);
            when(postServiceUtils.checkPostExists(anyLong())).thenReturn(testPost);
            when(postRepository.save(testPost)).thenReturn(testPost);

            PostDto postDto = postService.publishPost(1L);

            assertThat(postDto.getPublished()).isTrue();
            assertThat(postDto.getPublishedAt()).isNotNull();
        }


    @Test
    void testPublishPostTrueFromStart() {
            testPost.setPublished(true);
        when(postServiceUtils.checkPostExists(1L)).thenReturn(testPost);
        assertThrows(IllegalArgumentException.class,
                () -> postService.publishPost(1L));
        }

        @Test
        void testUpdate() {
            String content = "Updated content";
            when(postServiceUtils.checkPostExists(anyLong())).thenReturn(testPost);

            postService.update(1L, content);
            assertEquals(content, testPost.getContent());
        }
    }



 
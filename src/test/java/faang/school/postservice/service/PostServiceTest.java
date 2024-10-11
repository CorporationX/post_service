package faang.school.postservice.service;

import org.junit.jupiter.api.extension.ExtendWith;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private PostRepository postRepository;
    @Spy
    private PostMapper postMapper;
    @Mock
    private PostValidator postValidator;
    @InjectMocks
    private PostService postService;
    private Post post;
    private PostDto postDto;
    private final long ANY_ID = 1L;
    private final String CONTENT = "Content";
    private List<PostDto> postDtoList = new ArrayList<>();
    private List<Post> postList = new ArrayList<>();
    private Post postInList1;
    private Post postInList2;
    private Post postInList3;
    private PostDto postDtoInList1;
    private PostDto postDtoInList2;
    private PostDto postDtoInList3;

    @BeforeEach
    public void init() {
        post = Post.builder()
                .id(ANY_ID)
                .content(CONTENT)
                .authorId(ANY_ID)
                .published(false)
                .deleted(false)
                .build();
        postDto = PostDto.builder()
                .id(ANY_ID)
                .content(CONTENT)
                .authorId(ANY_ID)
                .published(false)
                .deleted(false)
                .build();
        postInList1 = Post.builder()
                .id(1)
                .content(CONTENT)
                .authorId(ANY_ID)
                .published(false)
                .deleted(false)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();
        postInList2 = Post.builder()
                .id(2)
                .content(CONTENT)
                .authorId(ANY_ID)
                .published(false)
                .deleted(false)
                .createdAt(LocalDateTime.now().minusDays(2))
                .build();
        postInList3 = Post.builder()
                .id(3)
                .content(CONTENT)
                .authorId(ANY_ID)
                .published(false)
                .deleted(false)
                .createdAt(LocalDateTime.now().minusDays(3))
                .build();
        postDtoInList1 = PostDto.builder()
                .id(1)
                .content(CONTENT)
                .authorId(ANY_ID)
                .published(false)
                .deleted(false)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();
        postDtoInList2 = PostDto.builder()
                .id(2)
                .content(CONTENT)
                .authorId(ANY_ID)
                .published(false)
                .deleted(false)
                .createdAt(LocalDateTime.now().minusDays(2))
                .build();
        postDtoInList3 = PostDto.builder()
                .id(3)
                .content(CONTENT)
                .authorId(ANY_ID)
                .published(false)
                .deleted(false)
                .createdAt(LocalDateTime.now().minusDays(3))
                .build();


        postList = List.of(postInList1, postInList2, postInList3);
        postDtoList = List.of(postDtoInList3, postDtoInList2, postDtoInList1);

    }

    @Nested
    class PositiveTests {
        @Test
        @DisplayName("Verify saving post")
        void whenCreatedThenSuccess() {
            when(postMapper.toEntity(any())).thenReturn(post);
            when(postMapper.toDto(any())).thenReturn(postDto);

            postService.createPostDraft(postDto);

            Mockito.verify(postRepository).save(any());
        }

        @Test
        @DisplayName("Verify saving post")
        void whenPostPublishedThenSuccess() {
            when(postRepository.findById(any())).thenReturn(Optional.of(new Post()));

            postService.publishPost(ANY_ID);

            Mockito.verify(postRepository).save(any());
        }

        @Test
        @DisplayName("Verify updating post")
        void whenUpdatedThenSuccess() {
            when(postRepository.findById(any())).thenReturn(Optional.of(new Post()));
            when(postMapper.toDto(any())).thenReturn(postDto);

            postService.updatePost(postDto);

            Mockito.verify(postRepository).save(any());
        }

        @Test
        @DisplayName("Verify soft deleting post")
        void whenDeletedThenSuccess() {
            when(postRepository.findById(any())).thenReturn(Optional.of(new Post()));

            postService.softDeletePost(ANY_ID);

            Mockito.verify(postRepository).save(any());
        }

        @Test
        @DisplayName("Verify sorting posts by userId")
        void whenPostsGetByUserIdAndSortedThenSuccess() {
            when(postRepository.findByAuthorId(ANY_ID)).thenReturn(postList);
            when(postMapper.toDto(postInList1)).thenReturn(postDtoInList1);
            when(postMapper.toDto(postInList2)).thenReturn(postDtoInList2);
            when(postMapper.toDto(postInList3)).thenReturn(postDtoInList3);

            List<PostDto> postDtoListResult = postService.getPostsNotDeletedNotPublishedByUserId(ANY_ID);

            assertEquals(postList.size(), postDtoListResult.size());
            assertEquals(postDtoList.get(0), postDtoListResult.get(0));
            assertEquals(postDtoList.get(1), postDtoListResult.get(1));
            assertEquals(postDtoList.get(2), postDtoListResult.get(2));

        }

        @Test
        @DisplayName("Verify sorting posts by projectId")
        void whenPostsGetByProjectIdAndSortedThenSuccess() {
            when(postRepository.findByProjectId(ANY_ID)).thenReturn(postList);
            when(postMapper.toDto(postInList1)).thenReturn(postDtoInList1);
            when(postMapper.toDto(postInList2)).thenReturn(postDtoInList2);
            when(postMapper.toDto(postInList3)).thenReturn(postDtoInList3);

            List<PostDto> postDtoListResult = postService.getPostsNotDeletedNotPublishedByProjectId(ANY_ID);

            assertEquals(postList.size(), postDtoListResult.size());
            assertEquals(postDtoList.get(0), postDtoListResult.get(0));
            assertEquals(postDtoList.get(1), postDtoListResult.get(1));
            assertEquals(postDtoList.get(2), postDtoListResult.get(2));

        }

        @Test
        @DisplayName("Verify sorting published posts by authorId")
        void whenPostsGetByAuthorIdAndSortedNotDeletedThenSuccess() {
            when(postRepository.findByAuthorId(ANY_ID)).thenReturn(postList);

            List<PostDto> postDtoListResult = postService.getPostsPublishedNotDeletedByUserId(ANY_ID);

            assertTrue(postDtoListResult.isEmpty());
        }

        @Test
        @DisplayName("Verify sorting published posts by projectId")
        void whenPostsGetByProjectIdAndSortedNotDeletedThenSuccess() {
            when(postRepository.findByProjectId(ANY_ID)).thenReturn(postList);

            List<PostDto> postDtoListResult = postService.getPostsPublishedNotDeletedByProjectId(ANY_ID);

            assertTrue(postDtoListResult.isEmpty());
        }

    }
}
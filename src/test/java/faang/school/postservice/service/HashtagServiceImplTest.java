package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.HashtagRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HashtagServiceImpl Tests")
class HashtagServiceImplTest {

    @Mock
    private HashtagRepository hashtagRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private HashtagServiceImpl hashtagService;

    // ==================== extractHashtagNames() ====================

    @Test
    @DisplayName("Should extract multiple hashtags from content")
    void extractHashtagNames_WithMultipleHashtags_ShouldReturnAllHashtags() {
        // Arrange
        String content = "I love #java and #spring programming!";

        // Act
        List<String> result = hashtagService.extractHashtagNames(content);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly("#java", "#spring");
    }

    @Test
    @DisplayName("Should extract single hashtag from content")
    void extractHashtagNames_WithSingleHashtag_ShouldReturnOneHashtag() {
        // Arrange
        String content = "Learning #java today!";

        // Act
        List<String> result = hashtagService.extractHashtagNames(content);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly("#java");
    }

    @Test
    @DisplayName("Should return empty list when no hashtags in content")
    void extractHashtagNames_WithNoHashtags_ShouldReturnEmptyList() {
        // Arrange
        String content = "This is a post without hashtags";

        // Act
        List<String> result = hashtagService.extractHashtagNames(content);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return empty list when content is empty")
    void extractHashtagNames_WithEmptyContent_ShouldReturnEmptyList() {
        // Arrange
        String content = "";

        // Act
        List<String> result = hashtagService.extractHashtagNames(content);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should extract hashtags with numbers and underscores")
    void extractHashtagNames_WithSpecialCharacters_ShouldExtractCorrectly() {
        // Arrange
        String content = "Testing #java17 and #spring_boot";

        // Act
        List<String> result = hashtagService.extractHashtagNames(content);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly("#java17", "#spring_boot");
    }

    // ==================== saveHashtags() ====================

    @Test
    @DisplayName("Should save all hashtags when content has multiple hashtags")
    void saveHashtags_WithMultipleHashtags_ShouldSaveAll() {
        // Arrange
        String content = "I love #java and #spring!";
        Long postId = 1L;

        // Act
        hashtagService.saveHashtags(content, postId);

        // Assert
        ArgumentCaptor<List<Hashtag>> captor = ArgumentCaptor.forClass(List.class);
        verify(hashtagRepository, times(1)).saveAll(captor.capture());

        List<Hashtag> savedHashtags = captor.getValue();
        assertThat(savedHashtags).hasSize(2);
        assertThat(savedHashtags.get(0).getName()).isEqualTo("#java");
        assertThat(savedHashtags.get(1).getName()).isEqualTo("#spring");
        assertThat(savedHashtags.get(0).getPostId()).isEqualTo(postId);
        assertThat(savedHashtags.get(1).getPostId()).isEqualTo(postId);
    }

    @Test
    @DisplayName("Should not save anything when content has no hashtags")
    void saveHashtags_WithNoHashtags_ShouldNotSave() {
        // Arrange
        String content = "Post without hashtags";
        Long postId = 1L;

        // Act
        hashtagService.saveHashtags(content, postId);

        // Assert
        verify(hashtagRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("Should save single hashtag correctly")
    void saveHashtags_WithSingleHashtag_ShouldSaveOne() {
        // Arrange
        String content = "Learning #java";
        Long postId = 1L;

        // Act
        hashtagService.saveHashtags(content, postId);

        // Assert
        ArgumentCaptor<List<Hashtag>> captor = ArgumentCaptor.forClass(List.class);
        verify(hashtagRepository, times(1)).saveAll(captor.capture());

        List<Hashtag> savedHashtags = captor.getValue();
        assertThat(savedHashtags).hasSize(1);
        assertThat(savedHashtags.get(0).getName()).isEqualTo("#java");
        assertThat(savedHashtags.get(0).getPostId()).isEqualTo(postId);
    }

    // ==================== findPostsByHashtagName() ====================

    @Test
    @DisplayName("Should return page with posts when hashtag exists")
    void findPostsByHashtagName_WithExistingHashtag_ShouldReturnPosts() {
        // Arrange
        String hashtagName = "#java";
        Pageable pageable = PageRequest.of(0, 20);

        List<Long> postIds = List.of(1L, 2L, 3L);
        List<Post> posts = createMockPosts();
        List<PostDto> postDtos = createMockPostDtos();

        when(hashtagRepository.findDistinctPostIdsByName(hashtagName)).thenReturn(postIds);
        when(postRepository.findAllByIdOrderByCreatedAtDesc(postIds)).thenReturn(posts);
        when(postMapper.toDtoList(posts)).thenReturn(postDtos);

        // Act
        Page<PostDto> result = hashtagService.findPostsByHashtagName(hashtagName, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.isEmpty()).isFalse();

        verify(hashtagRepository, times(1)).findDistinctPostIdsByName(hashtagName);
        verify(postRepository, times(1)).findAllByIdOrderByCreatedAtDesc(postIds);
        verify(postMapper, times(1)).toDtoList(posts);
    }

    @Test
    @DisplayName("Should return empty page when hashtag does not exist")
    void findPostsByHashtagName_WithNonExistingHashtag_ShouldReturnEmptyPage() {
        // Arrange
        String hashtagName = "#nonexistent";
        Pageable pageable = PageRequest.of(0, 20);

        when(hashtagRepository.findDistinctPostIdsByName(hashtagName)).thenReturn(Collections.emptyList());

        // Act
        Page<PostDto> result = hashtagService.findPostsByHashtagName(hashtagName, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.isEmpty()).isTrue();

        verify(hashtagRepository, times(1)).findDistinctPostIdsByName(hashtagName);
        verify(postRepository, never()).findAllByIdOrderByCreatedAtDesc(anyList());
        verify(postMapper, never()).toDtoList(anyList());
    }

    @Test
    @DisplayName("Should return correct page with pagination")
    void findPostsByHashtagName_WithPagination_ShouldReturnCorrectPage() {
        // Arrange
        String hashtagName = "#java";
        Pageable pageable = PageRequest.of(1, 2);

        List<Long> allPostIds = List.of(1L, 2L, 3L, 4L, 5L);
        List<Long> paginatedPostIds = List.of(3L, 4L);
        List<Post> posts = createMockPosts().subList(0, 2);
        List<PostDto> postDtos = createMockPostDtos().subList(0, 2);

        when(hashtagRepository.findDistinctPostIdsByName(hashtagName)).thenReturn(allPostIds);
        when(postRepository.findAllByIdOrderByCreatedAtDesc(paginatedPostIds)).thenReturn(posts);
        when(postMapper.toDtoList(posts)).thenReturn(postDtos);

        // Act
        Page<PostDto> result = hashtagService.findPostsByHashtagName(hashtagName, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalPages()).isEqualTo(3);
        assertThat(result.getNumber()).isEqualTo(1);

        verify(hashtagRepository, times(1)).findDistinctPostIdsByName(hashtagName);
        verify(postRepository, times(1)).findAllByIdOrderByCreatedAtDesc(paginatedPostIds);
    }

    @Test
    @DisplayName("Should return empty page when page is out of range")
    void findPostsByHashtagName_WithPageOutOfRange_ShouldReturnEmptyPage() {
        // Arrange
        String hashtagName = "#java";
        Pageable pageable = PageRequest.of(10, 20);

        List<Long> postIds = List.of(1L, 2L, 3L);

        when(hashtagRepository.findDistinctPostIdsByName(hashtagName)).thenReturn(postIds);

        // Act
        Page<PostDto> result = hashtagService.findPostsByHashtagName(hashtagName, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.isEmpty()).isTrue();

        verify(hashtagRepository, times(1)).findDistinctPostIdsByName(hashtagName);
        verify(postRepository, never()).findAllByIdOrderByCreatedAtDesc(anyList());
    }

    @Test
    @DisplayName("Should handle single post correctly")
    void findPostsByHashtagName_WithSinglePost_ShouldReturnOnePage() {
        // Arrange
        String hashtagName = "#java";
        Pageable pageable = PageRequest.of(0, 20);

        List<Long> postIds = List.of(1L);
        List<Post> posts = List.of(mock(Post.class));
        List<PostDto> postDtos = List.of(new PostDto(1L, "Test", 123L, LocalDateTime.now(), LocalDateTime.now()));

        when(hashtagRepository.findDistinctPostIdsByName(hashtagName)).thenReturn(postIds);
        when(postRepository.findAllByIdOrderByCreatedAtDesc(postIds)).thenReturn(posts);
        when(postMapper.toDtoList(posts)).thenReturn(postDtos);

        // Act
        Page<PostDto> result = hashtagService.findPostsByHashtagName(hashtagName, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isTrue();
    }

    // ==================== Helper Methods ====================

    private List<Post> createMockPosts() {
        return List.of(
                mock(Post.class),
                mock(Post.class),
                mock(Post.class)
        );
    }

    private List<PostDto> createMockPostDtos() {
        LocalDateTime now = LocalDateTime.now();
        return List.of(
                new PostDto(1L, "Post 1", 123L, now, now),
                new PostDto(2L, "Post 2", 456L, now, now),
                new PostDto(3L, "Post 3", 789L, now, now)
        );
    }
}
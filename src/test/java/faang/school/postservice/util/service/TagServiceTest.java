package faang.school.postservice.util.service;

import faang.school.postservice.dto.tag.TagAddDto;
import faang.school.postservice.dto.tag.TagDto;
import faang.school.postservice.dto.tag.TagRemoveDto;
import faang.school.postservice.dto.tag.TagSearchDto;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.exception.TagNotFoundException;
import faang.school.postservice.mapper.TagMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Tag;
import faang.school.postservice.repository.TagRepository;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.TagService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TagServiceTest {
    @Mock
    private PostService postService;
    @Mock
    private TagRepository tagRepository;
    @Spy
    private TagMapperImpl tagMapper;
    @InjectMocks
    private TagService tagService;

    private Post post;
    private Tag tag;
    @BeforeEach
    public void setUp() {
        post = Post.builder().id(1L).content("Some content").build();
        tag = Tag.builder().id(1L).name("Tag1").createdAt(LocalDateTime.now()).build();

        tag.setPosts(Set.of(post));
        post.setTags(Set.of(tag));
    }

    @Test
    public void getTagsForPostThrowPostNotFoundExceptionTest() {
        when(postService.findPostById(1L)).thenThrow(new PostNotFoundException("Post with id: " + 1L + " not found"));

        assertThrows(PostNotFoundException.class, () -> tagService.getTagsForPost(1L));
    }

    @Test
    public void getTagsForPostTest() {
        when(postService.findPostById(1L)).thenReturn(post);

        ResponseEntity<List<TagDto>> actualTagsResponseEntity = tagService.getTagsForPost(1L);

        List<TagDto> actualTags = actualTagsResponseEntity.getBody();
        assertEquals(HttpStatus.OK, actualTagsResponseEntity.getStatusCode());
        Assertions.assertNotNull(actualTags);
        assertEquals(1, actualTags.size());
        assertEquals("Tag1", actualTags.get(0).name());
    }


    @Test
    public void addToPostThrowPostNotFoundExceptionTest() {
        when(postService.findPostById(1L)).thenThrow(new PostNotFoundException("Post with id: " + 1L + " not found"));

        assertThrows(PostNotFoundException.class, () -> tagService.addToPost(getTagAddDto()));
    }

    @Test
    public void addToPostThrowTagNotFoundExceptionTest() {
        when(tagRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TagNotFoundException.class, () -> tagService.addToPost(getTagAddDto()));
    }

    @Test
    public void addToPostTest() {
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        when(postService.findPostById(1L)).thenReturn(post);

        TagAddDto tagAddDto = getTagAddDto();

        ResponseEntity<List<TagSearchDto>> actualTagsResponseEntity = tagService.addToPost(tagAddDto);

        verify(tagMapper, times(1)).toEntity(any(TagDto.class), any());
        verify(tagRepository, times(1)).saveAll(anyIterable());
        List<TagSearchDto> actualTags = actualTagsResponseEntity.getBody();
        assertEquals(HttpStatus.OK, actualTagsResponseEntity.getStatusCode());
        Assertions.assertNotNull(actualTags);
        assertEquals(2, actualTags.size());
        assertEquals(2, post.getTags().size());
    }

    @Test
    public void searchTagsLikeNameWhenTagNameIsNullTest() {
        List<Tag> tagsList = List.of(
                Tag.builder().id(1L).name("Tag1").createdAt(LocalDateTime.now()).build(),
                Tag.builder().id(2L).name("Tag2").createdAt(LocalDateTime.now()).build(),
                Tag.builder().id(3L).name("Tag3").createdAt(LocalDateTime.now()).build()
        );

        when(tagRepository.getMostPopularTags(anyInt())).thenReturn(tagsList);

        ResponseEntity<List<TagSearchDto>> actualTagsResponseEntity = tagService.searchTagsLikeName(null);
        verify(tagMapper, times(3)).mapToTagSearchDto(any(Tag.class));

        List<TagSearchDto> actualTags = actualTagsResponseEntity.getBody();
        assertEquals(HttpStatus.OK, actualTagsResponseEntity.getStatusCode());
        Assertions.assertNotNull(actualTags);
        assertEquals(3, actualTags.size());
    }

    @Test
    public void searchTagsLikeNameTest() {
        List<Tag> tagsList = List.of(
                Tag.builder().id(1L).name("Tag1").createdAt(LocalDateTime.now()).build(),
                Tag.builder().id(2L).name("Tag2").createdAt(LocalDateTime.now()).build(),
                Tag.builder().id(3L).name("Tag3").createdAt(LocalDateTime.now()).build()
        );

        when(tagRepository.findTagByNameLikeIgnoreCase(anyString())).thenReturn(tagsList);

        ResponseEntity<List<TagSearchDto>> actualTagsResponseEntity = tagService.searchTagsLikeName("t");
        verify(tagMapper, times(3)).mapToTagSearchDto(any(Tag.class));

        List<TagSearchDto> actualTags = actualTagsResponseEntity.getBody();
        assertEquals(HttpStatus.OK, actualTagsResponseEntity.getStatusCode());
        Assertions.assertNotNull(actualTags);
        assertEquals(3, actualTags.size());
    }

    @Test
    public void removeTagsFromPostTest() {
        tagService.removeTagsFromPost(new TagRemoveDto(List.of(1L, 2L), 1L, 1L));

        Mockito.verify(postService, times(1)).deleteTagsFromPost(1L, List.of(1L, 2L));
    }


    private TagAddDto getTagAddDto() {
        return new TagAddDto(
                List.of(new TagDto(1L, "Tag1"), new TagDto(null, "Tag2")), 1L, 1L);
    }
}

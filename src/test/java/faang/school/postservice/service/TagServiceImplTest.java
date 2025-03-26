package faang.school.postservice.service;

import faang.school.postservice.dto.tag.TagAddToPostDto;
import faang.school.postservice.dto.tag.TagAddedToPostDto;
import faang.school.postservice.dto.tag.TagCreateDto;
import faang.school.postservice.dto.tag.TagDto;
import faang.school.postservice.dto.tag.TagRemoveDto;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.mapper.TagMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Tag;
import faang.school.postservice.repository.TagRepository;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TagServiceImplTest {
    @Mock
    private CacheableTagSearchService cacheableTagSearchService;
    @Mock
    private PostService postService;
    @Mock
    private TagRepository tagRepository;
    @Spy
    private TagMapper tagMapper = Mappers.getMapper(TagMapper.class);
    @InjectMocks
    private TagServiceImpl tagServiceImpl;


    @Test
    public void getTagsForPostThrowPostNotFoundExceptionTest() {
        when(postService.findPostById(1L)).thenThrow(new PostNotFoundException("Post with id: " + 1L + " not found"));

        assertThrows(PostNotFoundException.class, () -> tagServiceImpl.getTagsForPost(1L));
    }

    @Test
    public void getTagsForPostTest() {
        when(postService.findPostById(1L))
                .thenReturn(
                        Post.builder().id(1L)
                                .content("Some content")
                                .tags(new HashSet<>(Set.of(Tag.builder().name("Tag1").id(1L).build())))
                                .build()
                );

        ResponseEntity<List<TagDto>> actualTagsResponseEntity = tagServiceImpl.getTagsForPost(1L);

        List<TagDto> actualTags = actualTagsResponseEntity.getBody();
        assertEquals(HttpStatus.OK, actualTagsResponseEntity.getStatusCode());
        assertNotNull(actualTags);
        assertEquals(1, actualTags.size());
        assertEquals("Tag1", actualTags.get(0).getName());
        verify(tagMapper, times(1)).mapToTagDto(any());
    }

    @Test
    public void createTagThrowEntityExistsExceptionTest() {
        when(tagRepository.findTagByName("Tag1")).thenReturn(Optional.of(Tag.builder().build()));

        assertThrows(EntityExistsException.class,
                () -> tagServiceImpl.createTag(new TagCreateDto("Tag1", 1L)));
    }

    @Test
    public void createTagTest() {
        when(cacheableTagSearchService.createTag(any())).thenReturn(new TagDto(1L, "Tag1"));

        ResponseEntity<TagDto> actualResult = tagServiceImpl.createTag(new TagCreateDto("Tag1", 1L));

        TagDto actualTagDto = actualResult.getBody();
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertNotNull(actualTagDto);
        assertEquals("Tag1", actualTagDto.getName());
    }

    @Test
    public void addToPostThrowPostNotFoundExceptionTest() {
        when(postService.findPostById(1L)).thenThrow(new PostNotFoundException("Post with id: " + 1L + " not found"));

        assertThrows(PostNotFoundException.class, () -> tagServiceImpl.getTagsForPost(1L));
    }

    @Test
    public void addToPostTest() {
        List<Tag> tags = List.of(
                Tag.builder().id(1L).name("Tag1").posts(new HashSet<>()).build(),
                Tag.builder().id(2L).name("Tag2").posts(new HashSet<>()).build()
        );

        when(tagRepository.findAllById(any())).thenReturn(tags);
        when(tagRepository.saveAll(any())).thenReturn(tags);
        when(postService.findPostById(1L)).thenReturn(Post.builder().id(1L).content("Some content").build());

        ResponseEntity<List<TagAddedToPostDto>> actualResult =
                tagServiceImpl.addToPost(1L, new TagAddToPostDto(List.of(1L, 2L), 1L));

        List<TagAddedToPostDto> actualTags = actualResult.getBody();
        assertNotNull(actualTags);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(2, actualTags.size());
        verify(tagMapper, times(2)).mapToTagAddedDto(any());
    }

    @Test
    public void searchTagsLikeNameTest() {
        when(cacheableTagSearchService.searchCachedTags(anyString()))
                .thenReturn(List.of(new TagDto(1L, "Tag1"), new TagDto(2L, "Tag2")));

        ResponseEntity<List<TagDto>> actualResult = tagServiceImpl.searchTagsLikeName("t");
        List<TagDto> actualTags = actualResult.getBody();
        assertNotNull(actualTags);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(2, actualTags.size());
        assertEquals("Tag1", actualTags.get(0).getName());
    }

    @Test
    public void removeTagsFromPostTest() {
        TagRemoveDto tagRemoveDto = new TagRemoveDto(List.of(1L, 2L), 1L);
        ResponseEntity<Void> actualResult = tagServiceImpl.removeTagsFromPost(1L, tagRemoveDto);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        verify(postService, times(1)).removeTagsFromPost(1L, List.of(1L, 2L));
    }
}

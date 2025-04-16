package faang.school.postservice.service;

import faang.school.postservice.dto.tag.TagDto;
import faang.school.postservice.mapper.TagMapper;
import faang.school.postservice.model.Tag;
import faang.school.postservice.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CacheableTagSearchServiceTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private TagMapper tagMapper;

    @InjectMocks
    private CacheableTagSearchService cacheableTagSearchService;

    private final int maxResults = 5;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(cacheableTagSearchService, "maxResults", maxResults);
    }

    @Test
    public void testCreateTag() {
        Tag tag = new Tag();
        tag.setName("test");
        TagDto expectedDto = new TagDto();

        when(tagRepository.save(any(Tag.class))).thenReturn(tag);
        when(tagMapper.mapToTagDto(any(Tag.class))).thenReturn(expectedDto);

        TagDto result = cacheableTagSearchService.createTag(tag);

        assertEquals(expectedDto, result);
        verify(tagRepository, times(1)).save(tag);
        verify(tagMapper, times(1)).mapToTagDto(tag);
    }

    @Test
    public void testSearchCachedTags() {
        String tagName = "Test";
        String searchPattern = "test";
        PageRequest pageRequest = PageRequest.of(0, maxResults);

        Tag tag1 = new Tag();
        Tag tag2 = new Tag();
        List<Tag> tags = List.of(tag1, tag2);
        TagDto dto1 = new TagDto();
        TagDto dto2 = new TagDto();
        List<TagDto> expectedDtos = List.of(dto1, dto2);

        when(tagRepository.findTagByNameLikeIgnoreCase(eq(searchPattern), eq(pageRequest)))
                .thenReturn(tags);
        when(tagMapper.mapToTagDto(tag1)).thenReturn(dto1);
        when(tagMapper.mapToTagDto(tag2)).thenReturn(dto2);

        List<TagDto> result = cacheableTagSearchService.searchCachedTags(tagName);

        assertEquals(expectedDtos, result);
        verify(tagRepository, times(1))
                .findTagByNameLikeIgnoreCase(searchPattern, pageRequest);
    }

    @Test
    public void testSearchCachedTags_VerifyLowerCaseConversion() {
        String tagName = "TeSt_WiTh_MiXeD_CaSe";
        String expectedSearchPattern = "test_with_mixed_case";

        when(tagRepository.findTagByNameLikeIgnoreCase(eq(expectedSearchPattern), any()))
                .thenReturn(List.of());

        cacheableTagSearchService.searchCachedTags(tagName);

        verify(tagRepository).findTagByNameLikeIgnoreCase(expectedSearchPattern, PageRequest.of(0, maxResults));
    }

    @Test
    public void testSearchCachedTags_VerifyPaginationParameters() {
        when(tagRepository.findTagByNameLikeIgnoreCase(any(), any()))
                .thenReturn(List.of());

        cacheableTagSearchService.searchCachedTags("any");

        verify(tagRepository).findTagByNameLikeIgnoreCase(
                any(),
                eq(PageRequest.of(0, maxResults))
        );
    }
}
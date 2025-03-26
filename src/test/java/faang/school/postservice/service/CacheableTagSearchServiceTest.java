package faang.school.postservice.service;

import faang.school.postservice.dto.tag.TagDto;
import faang.school.postservice.mapper.TagMapper;
import faang.school.postservice.model.Tag;
import faang.school.postservice.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class CacheableTagSearchServiceTest {
    @MockBean
    private TagRepository tagRepository;
    @Spy
    private TagMapper tagMapper = Mappers.getMapper(TagMapper.class);
    @Autowired
    private CacheableTagSearchService cacheableTagSearchService;

    @Autowired
    private CacheManager cacheManager;
    private Cache cache;

    @BeforeEach
    public void setUp() {
        cache = cacheManager.getCache("tags");
        if (cache != null) {
            cache.clear();
        }
    }

    @Test
    public void createTagTest() {
        Tag tag = Tag.builder().id(1L).name("Tag1").build();
        TagDto expectedDto = new TagDto(1L, "Tag1");

        when(tagRepository.save(any())).thenReturn(tag);
        when(tagMapper.mapToTagDto(tag)).thenReturn(expectedDto);

        TagDto actualResult = cacheableTagSearchService.createTag(tag);

        assertEquals(expectedDto, actualResult);

        assertNotNull(cache);

        TagDto cachedTag = cache.get(tag.getName(), TagDto.class);
        assertNotNull(cachedTag);
        assertEquals(expectedDto, cachedTag);
    }

    @Test
    public void searchCachedTagsTest() {
        cacheableTagSearchService.createTag(Tag.builder().id(1L).name("Tag1").build());
        cacheableTagSearchService.searchCachedTags("t");
        cacheableTagSearchService.searchCachedTags("t");

        verify(tagRepository, times(1))
                .findTagByNameLikeIgnoreCase("t", PageRequest.of(0, 2));
    }
}

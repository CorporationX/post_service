package faang.school.postservice.service;

import faang.school.postservice.dto.tag.TagDto;
import faang.school.postservice.mapper.TagMapper;
import faang.school.postservice.model.Tag;
import faang.school.postservice.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheableTagSearchService implements TagSearchService {
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Value("${post-service.tag.count-of-most-popular-tags}")
    private int maxResults;

    @CachePut(value = "tags", key = "#tag.getName()")
    public TagDto createTag(Tag tag) {
        log.debug("Creating new tag: {}", tag.getName());
        return tagMapper.mapToTagDto(tagRepository.save(tag));
    }

    @Cacheable(value = "tags", key = "#tagName")
    public List<TagDto> searchCachedTags(String tagName) {
        log.debug("Do first search tags like name: {}", tagName);
        return tagRepository
                .findTagByNameLikeIgnoreCase(tagName.toLowerCase(), PageRequest.of(0, maxResults)).stream()
                .map(tagMapper::mapToTagDto)
                .toList();
    }
}

package faang.school.postservice.service;

import faang.school.postservice.dto.tag.TagDto;
import faang.school.postservice.model.Tag;

import java.util.List;

public interface TagSearchService {

    TagDto createTag(Tag tag);

    List<TagDto> searchCachedTags(String tagName);
}

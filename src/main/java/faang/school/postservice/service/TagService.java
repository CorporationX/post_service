package faang.school.postservice.service;

import faang.school.postservice.dto.tag.TagAddToPostDto;
import faang.school.postservice.dto.tag.TagAddedToPostDto;
import faang.school.postservice.dto.tag.TagCreateDto;
import faang.school.postservice.dto.tag.TagDto;
import faang.school.postservice.dto.tag.TagRemoveDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TagService {
    ResponseEntity<List<TagDto>> getTagsForPost(Long postId);

    ResponseEntity<TagDto> createTag(TagCreateDto tagCreateDto);

    ResponseEntity<List<TagAddedToPostDto>> addToPost(Long postId, TagAddToPostDto tagAddToPostDto);

    ResponseEntity<List<TagDto>> searchTagsLikeName(String tagName);

    ResponseEntity<Void> removeTagsFromPost(Long postId, TagRemoveDto tagRemoveDto);
}

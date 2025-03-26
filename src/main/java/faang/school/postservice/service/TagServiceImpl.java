package faang.school.postservice.service;

import faang.school.postservice.dto.tag.TagAddToPostDto;
import faang.school.postservice.dto.tag.TagAddedToPostDto;
import faang.school.postservice.dto.tag.TagCreateDto;
import faang.school.postservice.dto.tag.TagDto;
import faang.school.postservice.exception.TagNotFoundException;
import faang.school.postservice.mapper.TagMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Tag;
import faang.school.postservice.repository.TagRepository;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagServiceImpl {
    private final PostService postService;
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;
    private final CacheableTagSearchService cacheableTagSearchService;

    public ResponseEntity<List<TagDto>> getTagsForPost(Long postId) {
        return ResponseEntity.ok(
                getPostById(postId).getTags().stream()
                        .map(tagMapper::mapToTagDto)
                        .toList());
    }

    public ResponseEntity<TagDto> createTag(TagCreateDto tagCreateDto) {
        log.debug("Creating new tag: {}", tagCreateDto.name());
        String tagName = tagCreateDto.name();

        validateTagExistByName(tagName);

        return ResponseEntity.ok(
                cacheableTagSearchService.createTag(
                        Tag.builder().name(tagName).creatorId(tagCreateDto.userId()).build())
        );
    }

    @Transactional
    public ResponseEntity<List<TagAddedToPostDto>> addToPost(Long postId, TagAddToPostDto tagAddToPostDto) {
        log.debug("Adding to post with id: tags: {} {}", postId, tagAddToPostDto.tagsId());
        Post post = getPostById(postId);

        Set<Tag> tags = tagAddToPostDto.tagsId().stream()
                .map(this::getTagById)
                .map(tag -> {
                    tag.getPosts().add(post);
                    return tagRepository.save(tag);
                })
                .collect(Collectors.toSet());

        post.setTags(tags);

        log.debug("All tags saved");
        return ResponseEntity.ok(tags.stream().map(tagMapper::mapToTagAddedDto).toList());
    }

    public ResponseEntity<List<TagDto>> searchTagsLikeName(String tagName) {
        log.debug("Searching tags like name {}", tagName);
        return ResponseEntity.ok(cacheableTagSearchService.searchCachedTags(tagName));
    }

    public ResponseEntity<Void> removeTagsFromPost(Long postId, List<Long> tagsId) {
        log.debug("Removing tags from post with id: {} {}", postId, tagsId);
        postService.removeTagsFromPost(postId, tagsId);

        return ResponseEntity.ok().build();
    }

    private Post getPostById(Long id) {
        return postService.findPostById(id);
    }

    private Tag getTagById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new TagNotFoundException("Tag with id: " + id + " not found"));
    }

    private void validateTagExistByName(String tagName) {
        tagRepository.findTagByName(tagName)
                .ifPresent(tag -> {
                    throw new EntityExistsException("Tag with name: " + tagName + " already exists");
                });
    }
}

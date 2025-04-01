package faang.school.postservice.service;

import faang.school.postservice.dto.tag.TagAddToPostDto;
import faang.school.postservice.dto.tag.TagAddedToPostDto;
import faang.school.postservice.dto.tag.TagCreateDto;
import faang.school.postservice.dto.tag.TagDto;
import faang.school.postservice.dto.tag.TagRemoveDto;
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
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagServiceImpl implements TagService {
    private final PostService postService;
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;
    private final CacheableTagSearchService cacheableTagSearchService;

    @Override
    public ResponseEntity<List<TagDto>> getTagsForPost(Long postId) {
        return ResponseEntity.ok(tagMapper.mapToTagDtoList(getPostById(postId).getTags()));
    }

    @Override
    public ResponseEntity<TagDto> createTag(TagCreateDto tagCreateDto) {
        log.debug("Creating new tag: {}", tagCreateDto.name());
        String tagName = tagCreateDto.name();

        validateTagExistByName(tagName);

        return ResponseEntity.ok(
                cacheableTagSearchService.createTag(
                        Tag.builder().name(tagName).creatorId(tagCreateDto.userId()).build())
        );
    }

    @Override
    @Transactional
    public ResponseEntity<List<TagAddedToPostDto>> addToPost(Long postId, TagAddToPostDto tagAddToPostDto) {
        List<Long> tagIds = tagAddToPostDto.tagsId();
        Post post = getPostById(postId);

        log.debug("Adding to post with id: tags: {} {}", postId, tagIds);

        Set<Tag> tagsByIds = StreamSupport.stream(tagRepository.findAllById(tagIds).spliterator(), false)
                .collect(Collectors.toSet());

        tagsByIds.forEach(tag -> tag.getPosts().add(post));
        tagRepository.saveAll(tagsByIds);

        post.setTags(tagsByIds);

        log.debug("All tags saved");
        return ResponseEntity.ok(tagsByIds.stream().map(tagMapper::mapToTagAddedDto).toList());
    }

    @Override
    public ResponseEntity<List<TagDto>> searchTagsLikeName(String tagName) {
        log.debug("Searching tags like name {}", tagName);
        return ResponseEntity.ok(cacheableTagSearchService.searchCachedTags(tagName));
    }

    @Override
    public ResponseEntity<Void> removeTagsFromPost(Long postId, TagRemoveDto tagRemoveDto) {
        List<Long> tagsId = tagRemoveDto.tagsId();

        log.debug("Removing tags from post with id: {} {}", postId, tagsId);
        postService.removeTagsFromPost(postId, tagsId);
        return ResponseEntity.ok().build();
    }

    private Post getPostById(Long id) {
        return postService.findPostById(id);
    }

    private void validateTagExistByName(String tagName) {
        tagRepository.findTagByName(tagName)
                .ifPresent(tag -> {
                    throw new EntityExistsException("Tag with name: " + tagName + " already exists");
                });
    }
}

package faang.school.postservice.service;

import faang.school.postservice.dto.tag.TagAddDto;
import faang.school.postservice.dto.tag.TagDto;
import faang.school.postservice.dto.tag.TagRemoveDto;
import faang.school.postservice.dto.tag.TagSearchDto;
import faang.school.postservice.exception.TagNotFoundException;
import faang.school.postservice.mapper.TagMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Tag;
import faang.school.postservice.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagService {
    private final PostService postService;
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Value("${post-service.tag.count-of-most-popular-tags}")
    private int mostPopularOf;

    public ResponseEntity<List<TagDto>> getTagsForPost(Long postId) {
        return ResponseEntity.ok(
                getPostById(postId).getTags().stream()
                        .map(tagMapper::mapToTagDto)
                        .toList());
    }

    @Transactional
    public ResponseEntity<List<TagSearchDto>> addToPost(TagAddDto tagAddDto) {
        Long postId = tagAddDto.postId();
        Post post = getPostById(postId);

        Set<Tag> tags = new HashSet<>();
        for (TagDto tagDto : tagAddDto.tagDtos()) {
            Long tagId = tagDto.id();
            Tag tag;

            if (tagId != null) {
                tag = getTagById(tagId);
                tag.getPosts().add(post);
                log.debug("Existing tag: {} will be added to post: {}", tagDto.name(), postId);
            } else {
                tag = tagMapper.toEntity(tagDto, tagAddDto.userId());
                tag.setPosts(new HashSet<>(Set.of(post)));
                log.debug("Tag with name: {} will be created and add to post: {}", tagDto.name(), postId);
            }

            tags.add(tag);
        }

        tagRepository.saveAll(tags);
        log.debug("All tags saved");
        post.setTags(tags);

        return ResponseEntity.ok(
                post.getTags().stream()
                        .map(tagMapper::mapToTagSearchDto)
                        .toList());
    }

    public ResponseEntity<List<TagSearchDto>> searchTagsLikeName(String tagName) {
        if (tagName == null || tagName.isBlank()) {
            log.debug("Search most popular tags");
            return ResponseEntity.ok(
                    tagRepository.getMostPopularTags(mostPopularOf).stream()
                            .map(tagMapper::mapToTagSearchDto)
                            .toList());
        }
        log.debug("Search tags like: {}", tagName);
        return ResponseEntity.ok(
                tagRepository.findTagByNameLikeIgnoreCase(tagName).stream()
                        .map(tagMapper::mapToTagSearchDto)
                        .toList());
    }

    public void removeTagsFromPost(TagRemoveDto tagRemoveDto) {
        Long postId = tagRemoveDto.postId();
        List<Long> tagsId = tagRemoveDto.tagsId();

        log.debug("Remove tags: {} from the post: {}", tagsId, postId);
        postService.deleteTagsFromPost(postId, tagsId);
    }

    private Post getPostById(Long id) {
        return postService.findPostById(id);
    }

    private Tag getTagById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new TagNotFoundException("Tag with id: " + id + " not found"));
    }
}

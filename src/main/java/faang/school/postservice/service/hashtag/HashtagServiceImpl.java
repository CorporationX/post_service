package faang.school.postservice.service.hashtag;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.HashtagRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@Service
public class HashtagServiceImpl implements HashtagService {

    private static final Pattern HASHTAG_PATTERN = Pattern.compile("#\\w+");

    private final HashtagRepository hashtagRepository;
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Override
    public List<String> extractHashtagNames(String content) {
        log.debug("Extracting hashtags from content: {}", content);

        List<String> hashtags = HASHTAG_PATTERN.matcher(content)
                .results()
                .map(MatchResult::group)
                .toList();

        log.debug("Extracted {} hashtags: {}", hashtags.size(), hashtags);
        return hashtags;
    }

    @Transactional
    @Override
    public void saveHashtags(String content, Long postId) {
        log.info("Saving hashtags for post ID: {}", postId);

        List<Hashtag> hashtags = extractHashtagNames(content).stream()
                .map(name -> Hashtag.builder()
                        .name(name)
                        .postId(postId)
                        .build())
                .toList();

        if (hashtags.isEmpty()) {
            log.info("No hashtags found in post ID: {}", postId);
            return;
        }

        hashtagRepository.saveAll(hashtags);
        log.info("Successfully saved {} hashtags for post ID: {}", hashtags.size(), postId);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<PostDto> findPostsByHashtagName(String name, int page, int size) {
        log.info("Finding posts by hashtag: '{}', page: {}, size: {}", name, page, size);

        Pageable pageable = PageRequest.of(page, size);

        List<Long> postIds = hashtagRepository.findDistinctPostIdByName(name);
        log.debug("Found {} unique post IDs for hashtag: '{}'", postIds.size(), name);

        if (postIds.isEmpty()) {
            log.info("No posts found for hashtag: '{}'", name);
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), postIds.size());

        if (start >= postIds.size()) {
            log.warn("Page {} is out of range for hashtag: '{}'. Total posts: {}",
                    pageable.getPageNumber(), name, postIds.size());
            return new PageImpl<>(Collections.emptyList(), pageable, postIds.size());
        }

        List<Long> paginatedPostIds = postIds.subList(start, end);
        log.debug("Fetching {} posts (IDs: {} to {}) from database",
                paginatedPostIds.size(), start, end - 1);

        List<Post> posts = postRepository.findAllByIdInOrderByCreatedAtDesc(paginatedPostIds);
        log.debug("Retrieved {} posts from database", posts.size());

        List<PostDto> postDtos = postMapper.toPostDtos(posts);
        log.info("Successfully found {} posts (total: {}) for hashtag: '{}'",
                postDtos.size(), postIds.size(), name);

        return new PageImpl<>(postDtos, pageable, postIds.size());
    }
}
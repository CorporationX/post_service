package faang.school.postservice.service;

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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Page<PostDto> findPostsByHashtagName(String name, Pageable pageable) {
        log.info("Finding posts by hashtag: '{}', page: {}, size: {}",
                name, pageable.getPageNumber(), pageable.getPageSize());

        // 1. Найти все ID постов с этим хештегом
        List<Long> postIds = hashtagRepository.findDistinctPostIdsByName(name);

        log.debug("Found {} unique post IDs for hashtag: '{}'", postIds.size(), name);

        // Проверка: если нет постов с таким хештегом
        if (postIds.isEmpty()) {
            log.info("No posts found for hashtag: '{}'", name);
            return new PageImpl<>(List.of(), pageable, 0);
        }

        // 2. Применить пагинацию к списку ID
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), postIds.size());

        // Проверка: если страница вне диапазона
        if (start >= postIds.size()) {
            log.warn("Page {} is out of range for hashtag: '{}'. Total posts: {}",
                    pageable.getPageNumber(), name, postIds.size());
            return new PageImpl<>(List.of(), pageable, postIds.size());
        }

        List<Long> paginatedPostIds = postIds.subList(start, end);
        log.debug("Fetching {} posts (IDs: {} to {}) from database",
                paginatedPostIds.size(), start, end - 1);

        // 3. Загрузить посты из БД по ID с сортировкой
        List<Post> posts = postRepository.findAllByIdOrderByCreatedAtDesc(paginatedPostIds);

        log.debug("Retrieved {} posts from database", posts.size());

        // 4. Преобразовать в DTO
        List<PostDto> postDtos = postMapper.toDtoList(posts);

        log.info("Successfully found {} posts (total: {}) for hashtag: '{}'",
                postDtos.size(), postIds.size(), name);

        // 5. Создать Page<PostDto>
        return new PageImpl<>(postDtos, pageable, postIds.size());
    }
}
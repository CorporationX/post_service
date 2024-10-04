package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModerationPostService {

    private final PostRepository postRepository;
    private final ModerationAsyncService moderationAsyncService;

    @Value("${moderation.sublist-size}")
    private int sublistSize;

    @Transactional
    public void moderateUnverifiedPosts() {
        List<Post> unverifiedPosts = postRepository.findUnverifiedPosts();
        log.info("Найдено непроверенных постов: {}", unverifiedPosts.size());

        List<List<Post>> sublists = splitListIntoSublists(unverifiedPosts, sublistSize);
        log.info("Разделение на подгруппы для модерации, размер каждой группы: {}", sublistSize);

        sublists.parallelStream().forEach(sublist -> moderationAsyncService.moderatePostsSublistAsync(sublist));
    }

    private List<List<Post>> splitListIntoSublists(List<Post> posts, int sublistSize) {
        if (sublistSize <= 0) {
            throw new IllegalArgumentException("Sublist size must be greater than zero.");
        }
        return IntStream.range(0, (posts.size() + sublistSize - 1) / sublistSize)
                .mapToObj(i -> posts.subList(i * sublistSize, Math.min(posts.size(), (i + 1) * sublistSize)))
                .collect(Collectors.toList());
    }
}
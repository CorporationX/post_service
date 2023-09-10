package faang.school.postservice.service.post;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    @Value("${author_banner.count_offensive_content_for_ban}")
    private long countOffensiveContentForBan;

    public Post getPostById(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("This post was not found"));
    }

    public List<Long> getByPostIsVerifiedFalse() {
        return postRepository.findByVerifiedIsFalse().stream()
                .collect(Collectors.groupingBy(Post::getAuthorId, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() >= countOffensiveContentForBan)
                .map(Map.Entry::getKey)
                .toList();
    }
}
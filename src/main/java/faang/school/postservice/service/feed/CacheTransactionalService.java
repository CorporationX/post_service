package faang.school.postservice.service.feed;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheTransactionalService {
    private final CacheService cacheService;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Value("${data.redis.cache.feed.showLastComments}")
    private int showLastComments;
    @Transactional
    public List<PostDto> getPostDtosFromDB(List<Long> postsIds) {
        Iterable<Post> missingPosts = postRepository.findAllById(postsIds);
        List<Post> posts = new ArrayList<>();
        missingPosts.forEach(posts::add);

        return posts.stream()
                .filter(post -> {
                    if (post.isDeleted()) {
                        log.info("Post with ID {} was found in DB but it was deleted", post.getId());
                        cacheService.handlePostDeletion(post.getId());
                        return false;
                    }
                    return true;
                })
                .map(postMapper::toDto)
                .toList();
    }

    @Transactional
    public Map<Long, List<CommentDto>> getCommentsFromDB(Set<Long> postIds) {
        List<Comment> comments = commentRepository.findLastsByPostId(postIds, showLastComments);

        return comments.stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getPost().getId(),
                        Collectors.mapping(commentMapper::toDto, Collectors.toList())
                ));
    }
}

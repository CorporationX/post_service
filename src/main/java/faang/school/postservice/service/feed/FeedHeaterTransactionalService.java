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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedHeaterTransactionalService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final CacheService cacheService;
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Value("${data.redis.cache.feed.showLastComments}")
    private int showLastComments;
    @Value("${feed-heater.postLimit}")
    private int postLimit;

    @Transactional
    public Map<Long, List<CommentDto>> processComments(List<PostDto> postDtos) {
        Set<Long> postIds = postDtos.stream()
                .map(PostDto::getId)
                .collect(Collectors.toSet());

        List<Comment> comments = commentRepository.findLastsByPostId(postIds, showLastComments);

        return cacheComments(comments).stream()
                .collect(Collectors.groupingBy(CommentDto::getPostId));
    }

    private List<CommentDto> cacheComments(List<Comment> comments) {
        List<CommentDto> commentDtos = comments.stream()
                .map(commentMapper::toDto)
                .toList();

        commentDtos.stream()
                .collect(Collectors.groupingBy(CommentDto::getPostId))
                .forEach(cacheService::saveComments);

        return commentDtos;
    }

    @Transactional
    public List<PostDto> processPosts(List<Long> authorIds, Pageable pageable) {
        Page<Post> postPage = postRepository.findTopPostsByAuthors(authorIds, postLimit, pageable);
        List<Post> posts = postPage.getContent();

        return posts.isEmpty() ? Collections.emptyList() : cachePosts(posts);
    }

    private List<PostDto> cachePosts(List<Post> posts) {
        List<PostDto> postDtos = posts.stream()
                .map(postMapper::toDto)
                .toList();
        cacheService.savePosts(postDtos);
        return postDtos;
    }

}

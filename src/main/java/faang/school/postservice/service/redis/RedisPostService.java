package faang.school.postservice.service.redis;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.entity.redis.Posts;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.redis.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisPostService {
    private final RedisPostRepository redisPostRepository;
    @Value("${spring.data.redis.cache.ttl.posts}")
    private long ttlPosts;
    @Value("${spring.data.redis.cache.capacity.queue}")
    private int capacityQueue;

    public void save(long postId, Post post, int capacityQueue) {
        Posts posts = redisPostRepository.save(Posts.builder()
                .id(postId)
                .content(post.getContent())
                .authorId(post.getAuthorId())
                .likes(new AtomicLong(0))
                .comments(new LinkedBlockingQueue<>(capacityQueue))
                .createdAt(LocalDateTime.now())
                .ttlPosts(ttlPosts)
                .build());
        log.info("Posts - {} saved in Redis", posts);
    }

//    public void updateCommentDtoInPost(CommentDto commentDto) {
//        Posts posts = redisPostRepository.findById(commentDto.getId()).orElse(null);
//
//        Queue<CommentDto> commentDtos = posts.getPostDto().getComments();
//        if (commentDtos.size() == capacityQueue) {
//            commentDtos.poll();
//        }
//        commentDtos.offer(commentDto);
//        posts.getPostDto().setComments(commentDtos);
//
//        redisPostRepository.save(posts);
//    }
}

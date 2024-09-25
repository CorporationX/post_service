package faang.school.postservice.service.redis;

import faang.school.postservice.dto.comment.CommentFeedDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostFeedDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.redis.PostForCache;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostForFeedService {

    private final PostService postService;
    private final CommentForFeedService commentForFeedService;
    private final UserForFeedService userForFeedService;
    private final PostMapper postMapper;

    @Value("${cache.last_comments_amount}")
    private long lastCommentsAmount;

    public PostFeedDto getPostDtoForFeed(PostForCache postForCache) {
        Long authorId = postForCache.getAuthorId();
        String authorName = userForFeedService.getUserName(authorId);
        List<Long> lastCommentIds = postForCache.getLastCommentIds();
        List<CommentFeedDto> lastCommentFeedDtos = commentForFeedService.getLastCommentFeedDtos(lastCommentIds);
        PostFeedDto postFeedDto = postMapper.toPostDtoForFeedFromPostForCache(postForCache);
        postFeedDto.setAuthorName(authorName);
        postFeedDto.setLastComments(lastCommentFeedDtos);
        return postFeedDto;
    }

    public List<PostFeedDto> getPostDtosForFeedFromDB(List<PostForCache> postsFromCache, List<Long> postIdsFromFeed) {
        List<Long> receivedPostIds = postsFromCache.stream().map(PostForCache::getId).toList();
        List<Long> notReceivedPostIds = postIdsFromFeed.stream().dropWhile(receivedPostIds::contains).toList();
        List<PostDto> postsFromDB = postService.getPostsByIds(notReceivedPostIds);
        List<PostFeedDto> postFeedDtos = new ArrayList<>();
        postsFromDB.forEach(
                postDto -> {
                    Long authorId = postDto.getAuthorId();
                    String authorName = userForFeedService.getUserName(authorId);
                    List<Long> allCommentIds = postDto.getCommentIds();
                    List<Long> lastCommentIds = allCommentIds.stream().limit(lastCommentsAmount).toList();
                    List<CommentFeedDto> lastCommentFeedDtos = commentForFeedService.getLastCommentFeedDtos(lastCommentIds);
                    int commentAmount = allCommentIds.size();
                    PostFeedDto postFeedDto = postMapper.toPostDtoForFeedFromPostDto(postDto);
                    postFeedDto.setAuthorName(authorName);
                    postFeedDto.setLastComments(lastCommentFeedDtos);
                    postFeedDto.setCommentsAmount(commentAmount);
                    postFeedDtos.add(postFeedDto);
        });
        return postFeedDtos;
    }


}

package faang.school.postservice.service.redis;

import faang.school.postservice.dto.comment.CommentFeedDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostFeedDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.cache.PostForCache;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostForFeedService {

    private final PostService postService;
    private final CommentForFeedService commentForFeedService;
    private final UserForFeedService userForFeedService;
    private final PostMapper postMapper;

    @Value("${cache.last_comments_amount}")
    private long lastCommentsAmount;

    public PostFeedDto getPostDtoForFeedFromCache(PostForCache postForCache) {
        Long authorId = postForCache.getAuthorId();
        String authorName = userForFeedService.getUserName(authorId);
        TreeSet<Long> lastCommentIds = postForCache.getLastCommentIds();
        TreeSet<CommentFeedDto> lastCommentFeedDtos = commentForFeedService.getLastCommentFeedDtos(lastCommentIds);
        PostFeedDto postFeedDto = postMapper.toPostDtoForFeedFromPostForCache(postForCache);
        postFeedDto.setAuthorName(authorName);
        postFeedDto.setLastComments(lastCommentFeedDtos);
        return postFeedDto;
    }

    public PostFeedDto getPostDtoForFeedFromDB(PostDto postDto) {
        Long authorId = postDto.getAuthorId();
        String authorName = userForFeedService.getUserName(authorId);
        List<Long> commentIds = postDto.getCommentIds();
        TreeSet<Long> lastCommentIds = (TreeSet<Long>) postDto.getCommentIds().stream()
                .skip(commentIds.size() - lastCommentsAmount)
                .collect(Collectors.toSet());
        TreeSet<CommentFeedDto> lastCommentFeedDtos = commentForFeedService.getLastCommentFeedDtos(lastCommentIds);
        PostFeedDto postFeedDto = postMapper.toPostDtoForFeedFromPostDto(postDto);
        postFeedDto.setAuthorName(authorName);
        postFeedDto.setLastComments(lastCommentFeedDtos);
        return postFeedDto;
    }

    public List<PostFeedDto> getPostDtosForFeedFromDB(TreeSet<PostForCache> postsFromCache, List<Long> postIdsFromFeed) {
        List<Long> receivedPostIds = postsFromCache.stream().map(PostForCache::getId).toList();
        List<Long> notReceivedPostIds = postIdsFromFeed.stream().dropWhile(receivedPostIds::contains).toList();
        List<PostDto> postsFromDB = postService.getPostsByIds(notReceivedPostIds);
        List<PostFeedDto> postFeedDtos = new ArrayList<>();
        postsFromDB.forEach(postDto -> postFeedDtos.add(getPostDtoForFeedFromDB(postDto)));
        return postFeedDtos;
    }
}
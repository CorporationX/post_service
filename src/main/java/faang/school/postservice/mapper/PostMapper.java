package faang.school.postservice.mapper;

import faang.school.postservice.dto.feed.FeedPostDto;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostPublishedEvent;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.PostCache;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE
)
public abstract class PostMapper {

    @Autowired
    CommentMapper commentMapper;

    public abstract Post toPost(CreatePostDto createPostDto);

    public abstract void update(UpdatePostDto updatePostDto, @MappingTarget Post entity);

    public abstract PostDto toPostDto(Post post);

    public abstract List<PostDto> toPostDtoList(List<Post> posts);

    @Mapping(target = "postId", source = "id")
    @Mapping(target = "subscribers", ignore = true)
    public abstract PostPublishedEvent toPostPublishedEvent(Post post);

    @Mapping(target = "lastComments", ignore = true)
    public abstract PostCache toPostCache(Post post);

    @Mapping(target = "authorUser", ignore = true)
    @Mapping(target = "lastComments", ignore = true)
    public abstract FeedPostDto toFeedPostDto(PostCache postCache);

    @Mapping(target = "authorUser", ignore = true)
    @Mapping(target = "lastComments", ignore = true)
    public abstract FeedPostDto toFeedPostDto(Post post);

    @AfterMapping
    protected void fillLastComments(PostCache postCache, @MappingTarget FeedPostDto feedPostDto) {
        if (postCache.getLastComments() == null) {
            feedPostDto.setLastComments(List.of());
        } else {
            feedPostDto.setLastComments(
                    postCache.getLastComments().stream().map(commentMapper::toFeedCommentDto).toList()
            );
        }
    }
}

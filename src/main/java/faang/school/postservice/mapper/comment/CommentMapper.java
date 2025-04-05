package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.feed.FeedItemCommentDto;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.post.PostCommentEvent;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class CommentMapper {

    private LikeMapper likeMapper;
    private PostMapper postMapper;

    @Autowired
    public void setLikeMapper(LikeMapper likeMapper) {
        this.likeMapper = likeMapper;
    }

    @Autowired
    public void setPostMapper(PostMapper postMapper) {
        this.postMapper = postMapper;
    }

    @Mapping(target = "post", ignore = true)
    @Mapping(target = "likes", ignore = true)
    public abstract Comment toCommentEntity(CommentRequestDto commentRequestDto);

    @Mapping(source = "post", target = "postDto", qualifiedByName = "mapPost")
    @Mapping(source = "likes", target = "likeDtos", qualifiedByName = "mapLikes")
    public abstract CommentResponseDto toCommentResponseDto(Comment comment);

    public abstract List<CommentResponseDto> toCommentResponseDtos(List<Comment> comment);

    @Mapping(source = "postDto.id", target = "postId")
    public abstract FeedItemCommentDto toFeedItemCommentDto(CommentResponseDto commentResponseDto);

    public abstract List<FeedItemCommentDto> toFeedItemCommentDtos(List<CommentResponseDto> commentResponseDtos);

    public abstract FeedItemCommentDto toFeedItemCommentDto(PostCommentEvent postCommentEvent);

    @Named("mapLikes")
    protected List<LikeDto> mapLikes(List<Like> likes) {
        if (likes != null) {
            return likes.stream()
                    .map(likeMapper::toLikeDto)
                    .toList();
        }
        return new ArrayList<>();
    }

    @Named("mapPost")
    protected PostDto mapPost(Post post) {
        if (post != null) {
            return postMapper.toPostDto(post);
        }
        return null;
    }
}

package faang.school.postservice.mapper;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-08-20T20:19:30+0300",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.4.1 (Oracle Corporation)"
)
@Component
public class LikeMapperImpl implements LikeMapper {

    @Override
    public LikeDto toDto(Like like) {
        if ( like == null ) {
            return null;
        }

        LikeDto.LikeDtoBuilder likeDto = LikeDto.builder();

        likeDto.postId( likePostId( like ) );
        likeDto.commentId( likeCommentId( like ) );
        likeDto.id( like.getId() );
        likeDto.userId( like.getUserId() );

        return likeDto.build();
    }

    @Override
    public Like toModel(LikeDto likeDto) {
        if ( likeDto == null ) {
            return null;
        }

        Like.LikeBuilder like = Like.builder();

        if ( likeDto.getId() != null ) {
            like.id( likeDto.getId() );
        }
        like.userId( likeDto.getUserId() );

        return like.build();
    }

    private Long likePostId(Like like) {
        if ( like == null ) {
            return null;
        }
        Post post = like.getPost();
        if ( post == null ) {
            return null;
        }
        long id = post.getId();
        return id;
    }

    private Long likeCommentId(Like like) {
        if ( like == null ) {
            return null;
        }
        Comment comment = like.getComment();
        if ( comment == null ) {
            return null;
        }
        long id = comment.getId();
        return id;
    }
}

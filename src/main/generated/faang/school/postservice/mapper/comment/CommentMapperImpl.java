package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-06-01T19:08:47+0300",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.10 (Amazon.com Inc.)"
)
@Component
public class CommentMapperImpl implements CommentMapper {

    @Override
    public Comment toEntity(CreateCommentDto createCommentDto) {
        if ( createCommentDto == null ) {
            return null;
        }

        Comment.CommentBuilder comment = Comment.builder();

        comment.post( createCommentDtoToPost( createCommentDto ) );
        if ( createCommentDto.getId() != null ) {
            comment.id( createCommentDto.getId() );
        }
        comment.content( createCommentDto.getContent() );
        if ( createCommentDto.getAuthorId() != null ) {
            comment.authorId( createCommentDto.getAuthorId() );
        }

        return comment.build();
    }

    @Override
    public CreateCommentDto toDto(Comment comment) {
        if ( comment == null ) {
            return null;
        }

        CreateCommentDto.CreateCommentDtoBuilder createCommentDto = CreateCommentDto.builder();

        createCommentDto.postId( commentPostId( comment ) );
        createCommentDto.id( comment.getId() );
        createCommentDto.content( comment.getContent() );
        createCommentDto.authorId( comment.getAuthorId() );

        return createCommentDto.build();
    }

    protected Post createCommentDtoToPost(CreateCommentDto createCommentDto) {
        if ( createCommentDto == null ) {
            return null;
        }

        Post.PostBuilder post = Post.builder();

        if ( createCommentDto.getPostId() != null ) {
            post.id( createCommentDto.getPostId() );
        }

        return post.build();
    }

    private Long commentPostId(Comment comment) {
        if ( comment == null ) {
            return null;
        }
        Post post = comment.getPost();
        if ( post == null ) {
            return null;
        }
        long id = post.getId();
        return id;
    }
}

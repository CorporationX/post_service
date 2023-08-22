package faang.school.postservice.mapper;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-08-13T15:01:17+0300",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.4.1 (Oracle Corporation)"
)
@Component
public class PostMapperImpl implements PostMapper {

    @Override
    public PostDto toDto(Post post) {
        if ( post == null ) {
            return null;
        }

        PostDto.PostDtoBuilder postDto = PostDto.builder();

        postDto.likes( mapLikesToDto( post.getLikes() ) );
        postDto.id( post.getId() );
        postDto.content( post.getContent() );
        postDto.authorId( post.getAuthorId() );
        postDto.projectId( post.getProjectId() );
        postDto.createdAt( post.getCreatedAt() );
        postDto.publishedAt( post.getPublishedAt() );
        postDto.published( post.isPublished() );
        postDto.deleted( post.isDeleted() );

        return postDto.build();
    }

    @Override
    public Post toEntity(PostDto postDto) {
        if ( postDto == null ) {
            return null;
        }

        Post.PostBuilder post = Post.builder();

        post.id( postDto.getId() );
        post.content( postDto.getContent() );
        post.authorId( postDto.getAuthorId() );
        post.projectId( postDto.getProjectId() );
        post.likes( likeDtoListToLikeList( postDto.getLikes() ) );
        post.published( postDto.isPublished() );
        post.publishedAt( postDto.getPublishedAt() );
        post.deleted( postDto.isDeleted() );
        post.createdAt( postDto.getCreatedAt() );

        return post.build();
    }

    protected Like likeDtoToLike(LikeDto likeDto) {
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

    protected List<Like> likeDtoListToLikeList(List<LikeDto> list) {
        if ( list == null ) {
            return null;
        }

        List<Like> list1 = new ArrayList<Like>( list.size() );
        for ( LikeDto likeDto : list ) {
            list1.add( likeDtoToLike( likeDto ) );
        }

        return list1;
    }
}

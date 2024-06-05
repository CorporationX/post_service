package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-06-01T19:08:46+0300",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.10 (Amazon.com Inc.)"
)
@Component
public class PostMapperImpl implements PostMapper {

    @Override
    public PostDto toDto(Post post) {
        if ( post == null ) {
            return null;
        }

        PostDto.PostDtoBuilder postDto = PostDto.builder();

        postDto.id( post.getId() );
        postDto.content( post.getContent() );
        postDto.authorId( post.getAuthorId() );
        postDto.projectId( post.getProjectId() );
        postDto.published( post.isPublished() );
        postDto.deleted( post.isDeleted() );
        postDto.publishedAt( post.getPublishedAt() );
        postDto.updatedAt( post.getUpdatedAt() );

        return postDto.build();
    }

    @Override
    public Post toEntity(PostDto postDto) {
        if ( postDto == null ) {
            return null;
        }

        Post.PostBuilder post = Post.builder();

        if ( postDto.getId() != null ) {
            post.id( postDto.getId() );
        }
        post.content( postDto.getContent() );
        post.authorId( postDto.getAuthorId() );
        post.projectId( postDto.getProjectId() );
        post.published( postDto.isPublished() );
        post.publishedAt( postDto.getPublishedAt() );
        post.deleted( postDto.isDeleted() );
        post.updatedAt( postDto.getUpdatedAt() );

        return post.build();
    }
}

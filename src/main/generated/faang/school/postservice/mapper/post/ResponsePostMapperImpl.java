package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.ad.Ad;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-07-31T10:30:50+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.7 (Oracle Corporation)"
)
@Component
public class ResponsePostMapperImpl implements ResponsePostMapper {

    @Override
    public ResponsePostDto toDto(Post entity) {
        if ( entity == null ) {
            return null;
        }

        ResponsePostDto.ResponsePostDtoBuilder responsePostDto = ResponsePostDto.builder();

        responsePostDto.adId( entityAdId( entity ) );
        responsePostDto.likesIds( likesToLikesIds( entity.getLikes() ) );
        responsePostDto.commentsIds( commentsToCommentsIds( entity.getComments() ) );
        responsePostDto.albumsIds( albumsToAlbumsIds( entity.getAlbums() ) );
        responsePostDto.id( entity.getId() );
        responsePostDto.content( entity.getContent() );
        responsePostDto.authorId( entity.getAuthorId() );
        responsePostDto.projectId( entity.getProjectId() );
        responsePostDto.published( entity.isPublished() );
        responsePostDto.publishedAt( entity.getPublishedAt() );
        responsePostDto.scheduledAt( entity.getScheduledAt() );
        responsePostDto.deleted( entity.isDeleted() );
        responsePostDto.createdAt( entity.getCreatedAt() );
        responsePostDto.updatedAt( entity.getUpdatedAt() );

        return responsePostDto.build();
    }

    @Override
    public List<ResponsePostDto> toDtoList(List<Post> entities) {
        if ( entities == null ) {
            return null;
        }

        List<ResponsePostDto> list = new ArrayList<ResponsePostDto>( entities.size() );
        for ( Post post : entities ) {
            list.add( toDto( post ) );
        }

        return list;
    }

    private Long entityAdId(Post post) {
        if ( post == null ) {
            return null;
        }
        Ad ad = post.getAd();
        if ( ad == null ) {
            return null;
        }
        long id = ad.getId();
        return id;
    }
}

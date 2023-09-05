package faang.school.postservice.mapper.album;

import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumUpdateDto;
import faang.school.postservice.dto.album.AuthorAlbumDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.FIELD)
public interface AlbumMapper {

    Album toAlbumCreate(AlbumCreateDto albumCreateDto);

    @Mapping(target = "posts", source = "postsId", qualifiedByName = "toPosts")
    Album toAlbum(AlbumDto albumDto);

    @Mapping(target = "postsId", source = "posts", qualifiedByName = "toPostsId")
    AlbumDto toAlbumDto(Album album);

    @Mapping(target = "postsId", source = "posts", qualifiedByName = "toPostsId")
    AuthorAlbumDto toAuthorAlbumDto(Album album);

    @Mapping(target = "posts", source = "postsId", qualifiedByName = "toPosts")
    void updateAlbum(AlbumUpdateDto albumUpdateDto, @MappingTarget Album album);

    @Named(value = "toPostsId")
    default List<Long> toId(List<Post> posts) {
        if (posts == null) {
            return new ArrayList<>();
        }
        return posts.stream().map(Post::getId).toList();
    }

    @Named(value = "toPosts")
    default List<Post> toPosts(List<Long> ids) {
        if (ids == null) {
            return new ArrayList<>();
        }
        List<Post> posts = new ArrayList<>();
        for (Long id : ids) {
            posts.add(Post.builder().id(id).build());
        }
        return posts;
    }
}

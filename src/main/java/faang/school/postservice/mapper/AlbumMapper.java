package faang.school.postservice.mapper;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.model.Album;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AlbumMapper {

    private final UserServiceClient userServiceClient;
    private final PostMapper postMapper;

    public AlbumDto toDto(Album album) {
        return AlbumDto.builder()
                .id(album.getId())
                .title(album.getTitle())
                .description(album.getDescription())
                .author(userServiceClient.getUser(album.getAuthorId()))
                .posts(postMapper.toDtos(album.getPosts()))
                .createdAt(album.getCreatedAt())
                .updatedAt(album.getUpdatedAt())
                .build();
    }
}

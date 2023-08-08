package faang.school.postservice.service.album;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.exception.album.AlbumException;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final UserContext userContext;


    @Transactional
    public AlbumDto updateAlbum(long albumId, AlbumDto updatedAlbum) {

        Album existingAlbum = albumRepository.findById(albumId)
                .orElseThrow(() -> new AlbumException("Album not found"));

        if (existingAlbum.getAuthorId() != userContext.getUserId()) {
            throw new AlbumException("You can only update your own albums");
        }

        Album updatedEntity = albumMapper.toEntity(updatedAlbum);
        updatedAlbum.setAuthorId(existingAlbum.getAuthorId());

        existingAlbum.setTitle(updatedEntity.getTitle());
        existingAlbum.setDescription(updatedEntity.getDescription());
        existingAlbum.setPosts(new ArrayList<>(updatedEntity.getPosts()));
        existingAlbum.setUpdatedAt(LocalDateTime.now());

        Album savedAlbum = albumRepository.save(existingAlbum);
        return albumMapper.toDto(savedAlbum);
    }
}

package faang.school.postservice.service.album;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.validator.album.AccessValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository repository;
    private final AlbumMapper mapper;
    private final ObjectMapper objectMapper;
    private final AccessValidator accessValidator;

    @Transactional
    public AlbumDto createAlbum(AlbumDto dto) throws JsonProcessingException {
        var album = mapper.toEntity(dto);
        setAllowedUsers(dto, album);
        var alDto = mapper.toDto(repository.save(album));
        setAllowedUsers(album, alDto);
        return alDto;
    }

    @Transactional
    public AlbumDto getAlbum(Long albumId, long userId) throws JsonProcessingException {
        var album = repository.findById(albumId).orElseThrow(() -> new EntityNotFoundException("Entity wasn`t found"));
        accessValidator.validateAccess(album, userId);
        var responseDto = mapper.toDto(album);
        setAllowedUsers(album, responseDto);
        return responseDto;
    }

    @Transactional
    public AlbumDto update(AlbumDto albumDto, long userId) throws JsonProcessingException {
        accessValidator.validateUpdateAccess(albumDto, userId);
        var album = mapper.toEntity(albumDto);
        setAllowedUsers(albumDto, album);
        var responseDto = mapper.toDto(repository.save(album));
        setAllowedUsers(album, responseDto);
        return responseDto;
    }


    private void setAllowedUsers(Album album, AlbumDto dto) throws JsonProcessingException {
        dto.setAllowedUsersIds(objectMapper.readValue(album.getAllowedUsersIds(), new TypeReference<>() {
        }));
    }

    private void setAllowedUsers(AlbumDto dto, Album album) throws JsonProcessingException {
        album.setAllowedUsersIds(objectMapper.writeValueAsString(dto.getAllowedUsersIds()));
    }
}

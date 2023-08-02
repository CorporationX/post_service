package faang.school.postservice.service.album;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.album.AlbumMapper;
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
        album.setAllowedUsersIds(objectMapper.writeValueAsString(dto.getAllowedUsersIds()));
        var alDto = mapper.toDto(repository.save(album));
        alDto.setAllowedUsersIds(objectMapper.readValue(album.getAllowedUsersIds(), new TypeReference<>() {
        }));
        return alDto;
    }

    @Transactional
    public AlbumDto getAlbum(Long albumId, long userId) throws JsonProcessingException {
        var album = repository.findById(albumId).orElseThrow(() -> new EntityNotFoundException("Entity wasn`t found"));
        accessValidator.validateAccess(album, userId);
        var res = mapper.toDto(album);
        res.setAllowedUsersIds(objectMapper.readValue(album.getAllowedUsersIds(), new TypeReference<>() {
        }));
        return res;
    }
}

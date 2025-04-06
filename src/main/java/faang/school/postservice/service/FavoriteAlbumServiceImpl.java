package faang.school.postservice.service;

import faang.school.postservice.dto.FavoriteAlbumDto;
import faang.school.postservice.mapper.FavoriteAlbumMapper;
import faang.school.postservice.repository.FavoriteAlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteAlbumServiceImpl implements FavoriteAlbumService {
    private final FavoriteAlbumRepository favoriteAlbumRepository;
    private final FavoriteAlbumMapper favoriteAlbumMapper;

    @Override
    public FavoriteAlbumDto addAlbumToFavorite(FavoriteAlbumDto dto) {
        return favoriteAlbumMapper.toFavoriteAlbumDto(
                favoriteAlbumRepository.save(favoriteAlbumMapper.toFavoriteAlbum(dto)));
    }
}

package faang.school.postservice.controller;

import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumUpdateDto;
import faang.school.postservice.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService albumService;

    public AlbumDto createAlbum(AlbumCreateDto albumCreateDto) {
        createValidation(albumCreateDto);
        return albumService.createAlbum(albumCreateDto);
    }

    public void addPostToAlbum(Long albumId, Long postId) {
        validateId(albumId);
        validateId(postId);
        albumService.addPostToAlbum(albumId, postId);
    }

    public void deletePostFromAlbum(Long albumId, Long postId) {
        validateId(albumId);
        validateId(postId);
        albumService.deletePostFromAlbum(albumId, postId);
    }

    public void addAlbumToFavorites(Long albumId) {
        validateId(albumId);
        albumService.addAlbumToFavorites(albumId);
    }

    public void deleteAlbumFromFavorites(Long albumId) {
        validateId(albumId);
        albumService.deleteAlbumFromFavorites(albumId);
    }

    public AlbumDto findByWithPosts(Long albumId) {
        validateId(albumId);
        return albumService.findByIdWithPosts(albumId);
    }

    public List<AlbumDto> findAListOfAllYourAlbums(AlbumFilterDto albumFilterDto) {
        return albumService.findAListOfAllYourAlbums(albumFilterDto);
    }

    public List<AlbumDto> findListOfAllAlbumsInTheSystem(AlbumFilterDto albumFilterDto) {
        return albumService.findListOfAllAlbumsInTheSystem(albumFilterDto);
    }

    public List<AlbumDto> findListOfAllYourFavorites(AlbumFilterDto albumFilterDto) {
        return albumService.findAListOfAllYourFavoriteAlbums(albumFilterDto);
    }

    public AlbumDto updateAlbum(Long albumId, AlbumUpdateDto albumUpdateDto) {
        validateId(albumId);
        return albumService.updateAlbumAuthor(albumId, albumUpdateDto);
    }

    public void deleteAlbum(Long albumId) {
        validateId(albumId);
        albumService.deleteAlbum(albumId);
    }

    private void validateId(Long Id) {
        if (Id == null || Id == 0) {
            throw new IllegalArgumentException("Id is null");
        }
    }

    private void createValidation(AlbumCreateDto albumCreateDto) {
        if (albumCreateDto.getAuthorId() == null) {
            throw new IllegalArgumentException("AuthorId is null");
        }
        if (albumCreateDto.getTitle() == null) {
            throw new IllegalArgumentException("Title is null");
        }
        if (albumCreateDto.getDescription() == null) {
            throw new IllegalArgumentException("Description is null");
        }
    }
}

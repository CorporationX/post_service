package faang.school.postservice.controller;

import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumUpdateDto;
import faang.school.postservice.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class AlbumController {
    private final AlbumService albumService;

    @PostMapping("/albums")
    public AlbumDto createAlbum(@RequestBody AlbumCreateDto albumCreateDto) {
        createValidation(albumCreateDto);
        return albumService.createAlbum(albumCreateDto);
    }

    @PostMapping("/albums/{albumId}/posts/{postId}")
    public void addPostToAlbum(@PathVariable Long albumId,@PathVariable Long postId) {
        validateId(albumId);
        validateId(postId);

        albumService.addPostToAlbum(albumId, postId);
    }

    @DeleteMapping("/albums/{albumId}/posts/{postId}")
    public void deletePostFromAlbum(@PathVariable Long albumId,@PathVariable Long postId) {
        validateId(albumId);
        validateId(postId);

        albumService.deletePostFromAlbum(albumId, postId);
    }

    @PostMapping("/albums/favorites/{albumId}")
    public void addAlbumToFavorites(@PathVariable Long albumId) {
        validateId(albumId);

        albumService.addAlbumToFavorites(albumId);
    }

    @DeleteMapping("/albums/favorites/{albumId}")
    public void deleteAlbumFromFavorites(@PathVariable Long albumId) {
        validateId(albumId);

        albumService.deleteAlbumFromFavorites(albumId);
    }

    @GetMapping("/albums/{albumId}")
    public AlbumDto findByWithPosts(@PathVariable Long albumId) {
        validateId(albumId);

        return albumService.findByIdWithPosts(albumId);
    }

    @PostMapping("/albums/filter/all")
    public List<AlbumDto> findAListOfAllYourAlbums(@RequestBody AlbumFilterDto albumFilterDto) {
        return albumService.findAListOfAllYourAlbums(albumFilterDto);
    }

    @PostMapping("/albums/filter/all/systems")
    public List<AlbumDto> findListOfAllAlbumsInTheSystem(@RequestBody AlbumFilterDto albumFilterDto) {
        return albumService.findListOfAllAlbumsInTheSystem(albumFilterDto);
    }

    @PostMapping("/albums/filter/all/favorites")
    public List<AlbumDto> findListOfAllYourFavorites(@RequestBody AlbumFilterDto albumFilterDto) {
        return albumService.findAListOfAllYourFavoriteAlbums(albumFilterDto);
    }

    @PutMapping("/albums/{albumId}")
    public AlbumDto updateAlbum(@PathVariable Long albumId,@RequestBody AlbumUpdateDto albumUpdateDto) {
        validateId(albumId);

        return albumService.updateAlbumAuthor(albumId, albumUpdateDto);
    }

    @DeleteMapping("/albums/{albumId}")
    public void deleteAlbum(@PathVariable Long albumId) {
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

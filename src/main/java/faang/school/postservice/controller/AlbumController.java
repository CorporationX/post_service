package faang.school.postservice.controller;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.service.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/album")
public class AlbumController {
    private final AlbumService albumService;

    @ResponseBody
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public AlbumDto createAlbum(@RequestBody @Valid AlbumDto albumDto) {
        return albumService.createAlbum(albumDto);
    }

    @PutMapping("/{albumId}/post/{postId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void addPostToAlbum(@PathVariable Long albumId,
                               @PathVariable Long postId) {
        albumService.addPostToAlbum(postId, albumId);
    }

    @DeleteMapping("/{albumId}/post/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public void removePostFromAlbum(@PathVariable Long albumId,
                                    @PathVariable Long postId) {
        albumService.removePostFromAlbum(postId, albumId);
    }

    @PostMapping("/favourites/{albumId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void addAlbumToFavourites(@PathVariable Long albumId) {
        albumService.addAlbumToFavourites(albumId);
    }

    @DeleteMapping("/favourites/{albumId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeAlbumFromFavourites(@PathVariable Long albumId) {
        albumService.removeAlbumFromFavourites(albumId);
    }

    @ResponseBody
    @GetMapping("/{albumId}")
    @ResponseStatus(HttpStatus.OK)
    public AlbumDto getAlbumById(@PathVariable Long albumId) {
        return albumService.getAlbumById(albumId);
    }

    @ResponseBody
    @PostMapping("/author/{authorId}")
    @ResponseStatus(HttpStatus.OK)
    public List<AlbumDto> getAuthorFilteredAlbums(@PathVariable Long authorId,
                                                  @RequestBody AlbumFilterDto albumFilterDto) {
        return albumService.getAuthorFilteredAlbums(authorId, albumFilterDto);
    }

    @ResponseBody
    @PostMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<AlbumDto> getAllFilteredAlbums(@RequestBody AlbumFilterDto albumFilterDto) {
        return albumService.getAllFilteredAlbums(albumFilterDto);
    }

    @ResponseBody
    @PostMapping("/favorites")
    @ResponseStatus(HttpStatus.OK)
    public List<AlbumDto> getUserFavoriteAlbums(@RequestBody AlbumFilterDto albumFilterDto) {
        return albumService.getUserFavoriteAlbums(albumFilterDto);
    }

    @ResponseBody
    @PutMapping("/{albumId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AlbumDto updateAlbum(@PathVariable Long albumId,
                                @RequestBody @Valid AlbumDto albumDto) {
        return albumService.updateAlbum(albumId, albumDto);
    }

    @DeleteMapping("/{albumId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAlbum(@PathVariable Long albumId) {
        albumService.deleteAlbum(albumId);
    }
}
package faang.school.postservice.controller;

import faang.school.postservice.dto.AlbumDto;
import faang.school.postservice.dto.AlbumFilterDto;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/album")
public class AlbumController {
    private final AlbumService albumService;

    @ResponseBody
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public AlbumDto createAlbum(@RequestBody @Valid AlbumDto albumDto) {
        return albumService.createAlbum(albumDto);
    }

    @PutMapping("/add/post")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void addPostToAlbum(@RequestParam Long authorId,
                               @RequestParam Long postId,
                               @RequestParam Long albumId) {
        albumService.addPostToAlbum(authorId, postId, albumId);
    }

    @PutMapping("/remove/post")
    @ResponseStatus(HttpStatus.OK)
    public void removePostFromAlbum(@RequestParam Long authorId,
                                    @RequestParam Long postId,
                                    @RequestParam Long albumId) {
        albumService.removePostFromAlbum(authorId, postId, albumId);
    }

    @PutMapping("/add/favourites/{albumId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void addAlbumToFavourites(@PathVariable Long albumId,
                                     @RequestParam Long userId) {
        albumService.addAlbumToFavourites(albumId, userId);
    }

    @PutMapping("/remove/favourites/{albumId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeAlbumFromFavourites(@PathVariable Long albumId,
                                          @RequestParam Long userId) {
        albumService.removeAlbumFromFavourites(albumId, userId);
    }

    @ResponseBody
    @GetMapping("/get/{albumId}")
    @ResponseStatus(HttpStatus.OK)
    public AlbumDto getAlbumById(@PathVariable Long albumId) {
        return albumService.getAlbumById(albumId);
    }

    @ResponseBody
    @PostMapping("/get/all/{authorId}")
    @ResponseStatus(HttpStatus.OK)
    public List<AlbumDto> getAuthorFilteredAlbums(@PathVariable Long authorId,
                                                  @RequestBody AlbumFilterDto albumFilterDto) {
        return albumService.getAuthorFilteredAlbums(authorId, albumFilterDto);
    }

    @ResponseBody
    @PostMapping("/get/all")
    @ResponseStatus(HttpStatus.OK)
    public List<AlbumDto> getAllFilteredAlbums(@RequestBody AlbumFilterDto albumFilterDto) {
        return albumService.getAllFilteredAlbums(albumFilterDto);
    }

    @ResponseBody
    @PostMapping("/get/favorites/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<AlbumDto> getUserFavoriteAlbums(@PathVariable Long userId,
                                                @RequestBody AlbumFilterDto albumFilterDto) {
        return albumService.getUserFavoriteAlbums(userId, albumFilterDto);
    }

    @ResponseBody
    @PutMapping("/update/{albumId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AlbumDto updateAlbum(@PathVariable Long albumId,
                                @RequestBody @Valid AlbumDto albumDto) {
        return albumService.updateAlbum(albumId, albumDto);
    }

    @DeleteMapping("/delete/{albumId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAlbum(@PathVariable Long albumId,
                            @RequestParam Long authorId) {
        albumService.deleteAlbum(albumId, authorId);
    }
}
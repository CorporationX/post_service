package faang.school.postservice.controller;

import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.SearchDto;
import faang.school.postservice.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/albums")
@RestController
public class AlbumController {

    private final AlbumService albumService;

    @PostMapping("/all")
    public Page<AlbumDto> findAll(Pageable pageable, @RequestBody SearchDto searchDto) {
        return albumService.findAll(pageable, searchDto);
    }

    @PostMapping("/my")
    public Page<AlbumDto> findAllMy() {
        throw new NotImplementedException();
    }

    @GetMapping("{id}")
    public AlbumDto findById(@PathVariable Long id) {
        throw new NotImplementedException();
    }

    @PostMapping
    public AlbumDto create(@RequestBody AlbumCreateDto albumCreateDto) {
        return albumService.create(albumCreateDto);
    }

    @PatchMapping
    public AlbumDto update() {
        throw new NotImplementedException();
    }

    @DeleteMapping("/{albumId}")
    public void delete(@PathVariable String albumId) {
        throw new NotImplementedException();
    }

    @PostMapping("/{albumId}/post-addition")
    public void addPost(@PathVariable Long albumId, @RequestParam Long postId) {
        albumService.addPost(albumId, postId);
    }

    @PostMapping("/favourites/{albumId}")
    public void addToFavourites(@PathVariable Long albumId) {
        throw new NotImplementedException();
    }

    @DeleteMapping("/favourites/{albumId}")
    public void removeFromFavourites(@PathVariable String albumId) {
        throw new NotImplementedException();
    }
}

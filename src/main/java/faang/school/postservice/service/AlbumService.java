package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.SearchDto;
import faang.school.postservice.dto.user_service.user.UserDto;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class AlbumService {

    private final UserContext userContext;
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;

    @Transactional(readOnly = true)
    public Page<AlbumDto> findAll(Pageable pageable, SearchDto searchDto) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Example<Album> example = Example.of(Album.builder()
                .id(searchDto.getId())
                .title(searchDto.getTitle())
                .build(), matcher);

        Page<Album> albumPage = albumRepository.findAll(example, pageable);
        return albumPage.map(albumMapper::toDto);
    }

    @Transactional
    public AlbumDto create(AlbumCreateDto createDto) {
        long currentUserId = userContext.getUserId();
        UserDto albumAuthor = userServiceClient.getUser(currentUserId);

        Album albumToSave = Album.builder()
                .title(createDto.getTitle())
                .description(createDto.getDescription())
                .authorId(albumAuthor.id())
                .build();

        Album albumSaved = albumRepository.save(albumToSave);
        return albumMapper.toDto(albumSaved);
    }

    @Transactional
    public void addPost(Long albumId, Long postId) {
        long currentUserId = userContext.getUserId();
        UserDto currentUserDto = userServiceClient.getUser(currentUserId);

        Album album = albumRepository.findById(albumId).orElseThrow();
        Post post = postRepository.findById(postId).orElseThrow();

        verifyUserIsAlbumAuthor(album, currentUserDto.id());
        album.addPost(post);
        albumRepository.save(album);
    }

    private void verifyUserIsAlbumAuthor(Album album, Long userId) {
        if (!Objects.equals(album.getAuthorId(), userId)) {
            throw new IllegalArgumentException("The user is not the author of this album");
        }
    }
}

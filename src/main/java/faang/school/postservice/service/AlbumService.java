package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumUpdateDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final UserServiceClient userServiceClient;
    private final AlbumMapper albumMapper;
    private final UserContext userContext;
    private final PostRepository postRepository;

    //Пользователь может создать пустой альбом с каким-либо названием и описанием,
    // а затем добавлять туда те посты, которые посчитает нужным.
    // Любой пост может содержаться во множестве альбомов одного или нескольких пользователей.
    // У альбома обязательно должны быть название и описание.
    // Название альбома должно быть уникальным для конкретного пользователя.
    // Т.е. два пользователя могут иметь альбомы с одинаковым названием,
    // но один и тот же пользователь не может иметь два альбома с одинаковым названием.
    // Пользователь должен быть существующим пользователем в системе.
    public AlbumDto createAlbum(AlbumDto albumDto) {
        if (albumRepository.existsByTitleAndAuthorId(albumDto.getTitle(), albumDto.getAuthorId())) {
            throw new DataValidationException("Album with this title already exists");
        }
        if (userServiceClient.getUser(albumDto.getAuthorId()) == null) {
            throw new DataValidationException("AuthorId is null");
        }
        return albumMapper.toAlbumDto(albumRepository.save(albumMapper.toAlbum(albumDto)));
    }


    //Затем пользователь может добавить любой пост (свой или чужой) в любой выбранный им альбом.
    //Важно убедиться, что пользователь может добавлять посты только в свои альбомы, но не в чужие.
    //Добавляемые посты должны быть существующими постами в системе.
    //Также, затем пользователь может удалить любой пост из своего выбранного альбома.
    @Transactional
    public void addPostToAlbum(long albumId, long postId) {
        Album desiredAlbum = albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Album not found"));
        boolean byAuthorId = albumRepository.findByAuthorId(desiredAlbum.getAuthorId())
                .anyMatch(album -> album.getId() == albumId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        if (userContext.getUserId() != desiredAlbum.getAuthorId()) {
            throw new DataValidationException("You are not author of this album");
        }
        if (!byAuthorId) {
            throw new DataValidationException("Post already exists");
        }

        desiredAlbum.addPost(post);
        albumRepository.save(desiredAlbum);
    }

    public void deletePostFromAlbum(long albumId, long postId) {
        Album desiredAlbum = albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Album not found"));
        boolean byAuthorId = albumRepository.findByAuthorId(desiredAlbum.getAuthorId())
                .anyMatch(album -> album.getId() == albumId);
        postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        if (userContext.getUserId() != desiredAlbum.getAuthorId()) {
            throw new DataValidationException("You are not author of this album");
        }
        if (!byAuthorId) {
            throw new DataValidationException("Post already exists");
        }

        desiredAlbum.removePost(postId);
        albumRepository.save(desiredAlbum);
    }

    //Пользователь может добавить любой альбом в избранные альбомы, а также удалить его из избранных.

    public void addAlbumToFavorites(long albumId, long userId) {
        albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Album not found"));

        albumRepository.addAlbumToFavorites(albumId, userId);
    }

    public void deleteAlbumFromFavorites(long albumId, long userId) {
        albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Album not found"));

        albumRepository.deleteAlbumFromFavorites(albumId, userId);
    }

    //Пользователь может получить любой альбом по id этого альбома.
    public AlbumDto findByIdWithPosts(long albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Album not found"));

        return albumMapper.toAlbumDto(album);
    }

    //Пользователь может получить список всех своих альбомов и искать в этом списке.
    // Т.е. он сразу должен иметь возможность применить различные фильтры к этому поиску своих альбомов (по названию, дате и т.д.)

    //Пользователь может получить список вообще всех альбомов его и других пользователей
    // в приложении и применять фильтры и по этому списку тоже. Фильтры те же самые, что в пункте выше.

    //Пользователь может получить список всех своих избранных альбомов и применять фильтры поиска к этому списку.
    // Фильтры те же самые, что в пункте выше.

    //Пользователь может обновить любой свой существующий альбом.
    // Важно убедиться, что он обновляет именно свой альбом и не может изменить автора этого альбома.
    public AlbumDto updateAlbum(Long albumId, AlbumUpdateDto albumUpdateDto) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Album not found"));
        if (userContext.getUserId() != album.getAuthorId()) {
            throw new DataValidationException("You are not author of this album");
        }
        albumMapper.updateAlbum(albumUpdateDto, album);
        return albumMapper.toAlbumDto(albumRepository.save(album));
    }

    //Пользователь может удалить любой из своих альбомов.
    public void deleteAlbum(Long albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Album not found"));
        if (userContext.getUserId() != album.getAuthorId()) {
            throw new DataValidationException("You are not author of this album");
        }
        albumRepository.delete(album);
    }
    //findById(long id) — находит альбом с данным id в БД. Возвращает Optional, т.к. такого альбома в БД может и не найтись;
    //
    //findByAuthorId(long authorId) — находит в БД все альбомы, созданные пользователем с данным id;
    //
    //existsByTitleAndAuthorId(String title, long authorId) — проверяет, есть ли альбом с названием title у пользователя с id — authorId в БД;
    //
    //findByIdWithPosts(long id) — получает из БД альбом со всеми его постами. В сущности Album они будут лежать в поле posts;
    //
    //addAlbumToFavorites(long albumId, long userId) — добавляет альбом с id albumId в избранные для пользователя с id userId в БД;
    //
    //deleteAlbumFromFavorites(long albumId, long userId) — удаляет альбом с id albumId из избранных для пользователя с id userId в БД;
    //
    //findFavoriteAlbumsByUserId(long userId) — находит все избранные альбомы пользователя с id userId в БД
}

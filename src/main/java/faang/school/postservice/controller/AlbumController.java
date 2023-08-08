package faang.school.postservice.controller;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService albumService;

    //Пользователь может создать пустой альбом с каким-либо названием и описанием,
    // а затем добавлять туда те посты, которые посчитает нужным.
    // Любой пост может содержаться во множестве альбомов одного или нескольких пользователей.
    // У альбома обязательно должны быть название и описание.
    // Название альбома должно быть уникальным для конкретного пользователя.
    // Т.е. два пользователя могут иметь альбомы с одинаковым названием,
    // но один и тот же пользователь не может иметь два альбома с одинаковым названием.
    // Пользователь должен быть существующим пользователем в системе.
    public AlbumDto createAlbum(AlbumDto albumDto) {
        createValidation(albumDto);
        return albumService.createAlbum(albumDto);
    }

    private void createValidation(AlbumDto albumDto) {
        if (albumDto.getAuthorId() == 0 ) {
            throw new IllegalArgumentException("AuthorId is null");
        }
        if (albumDto.getTitle() == null) {
            throw new IllegalArgumentException("Title is null");
        }
        if (albumDto.getDescription() == null) {
            throw new IllegalArgumentException("Description is null");
        }
    }

    //Затем пользователь может добавить любой пост (свой или чужой) в любой выбранный им альбом.
    //Важно убедиться, что пользователь может добавлять посты только в свои альбомы, но не в чужие.
    // Добавляемые посты должны быть существующими постами в системе.
    // Также, затем пользователь может удалить любой пост из своего выбранного альбома.
    public void addPostToAlbum(long albumId, long postId) {
        albumService.addPostToAlbum(albumId,postId);
    }

    //Пользователь может добавить любой альбом в избранные альбомы, а также удалить его из избранных.

    //Пользователь может получить любой альбом по id этого альбома.

    //Пользователь может получить список всех своих альбомов и искать в этом списке.
    // Т.е. он сразу должен иметь возможность применить различные фильтры к этому поиску своих альбомов (по названию, дате и т.д.)

    //Пользователь может получить список вообще всех альбомов его и других пользователей
    // в приложении и применять фильтры и по этому списку тоже. Фильтры те же самые, что в пункте выше.

    //Пользователь может получить список всех своих избранных альбомов и применять фильтры поиска к этому списку.
    // Фильтры те же самые, что в пункте выше.

    //Пользователь может обновить любой свой существующий альбом.
    // Важно убедиться, что он обновляет именно свой альбом и не может изменить автора этого альбома.

    //Пользователь может удалить любой из своих альбомов.
}

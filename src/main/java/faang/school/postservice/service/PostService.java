package faang.school.postservice.service;

import faang.school.postservice.dto.post.CreatePostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.UpdatePostRequestDto;
import faang.school.postservice.model.Post;

import java.util.List;

/**
 * Сервис управления постами (пользователь/проект).
 * Функциональность:
 * • Создание черновика — создаёт пост со статусом draft (published=false, deleted=false).
 * • Публикация — публикует существующий пост, запрещено повторно публиковать; сохраняет дату публикации.
 * • Обновление — меняет содержимое поста; смена автора запрещена.
 * • Мягкое удаление — помечает пост deleted=true, запись остаётся в БД.
 * • Получение — возвращает пост по id.
 * • Списки — черновики/опубликованные для пользователя/проекта (только не удалённые):
 * - черновики сортируются по createdAt (DESC),
 * - опубликованные сортируются по publishedAt (DESC).
 * Валидация бизнес‑правил (ровно один автор: либо authorId, либо projectId; оба — нельзя; пустой контент — нельзя)
 * выполняется в реализации сервиса.
 */
public interface PostService {
    /** Создаёт черновик поста. */
    PostResponseDto createDraft(CreatePostRequestDto dto);


    /** Публикует пост по идентификатору. */
    PostResponseDto publish(long id);


    /** Обновляет содержимое поста (без смены автора). */
    PostResponseDto update(long id, UpdatePostRequestDto dto);


    /** Мягко удаляет пост. */
    void softDelete(long id);


    /** Возвращает пост по id. */
    PostResponseDto getById(long id);


    /** Возвращает не удалённые черновики пользователя, отсортированные по createdAt DESC. */
    List<PostResponseDto> getDraftsByUser(long userId);


    /** Возвращает не удалённые черновики проекта, отсортированные по createdAt DESC. */
    List<PostResponseDto> getDraftsByProject(long projectId);


    /** Возвращает не удалённые опубликованные посты пользователя, отсортированные по publishedAt DESC. */
    List<PostResponseDto> getPublishedByUser(long userId);

    /** Возвращает не удалённые опубликованные посты проекта, отсортированные по publishedAt DESC. */
    List<PostResponseDto> getPublishedByProject(long projectId);

    /** Возвращает сущность Post по id для внутреннего использования */
    Post getPostEntityById(long id);

    /** Проверяет и корректирует орфографию и пунктуацию неопубликованных постов через AI-сервис. */
    public void processTextChecking();
}
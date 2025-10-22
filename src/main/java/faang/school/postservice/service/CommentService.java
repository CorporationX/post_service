package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.dto.comment.ResponseCommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;

import java.util.List;

/**
 * Сервис для управления комментариями к постам.
 * Предоставляет функциональность для создания, обновления,
 * получения и удаления комментариев с валидацией.
 */
public interface CommentService {

    /**
     * Получить все комментарии к посту
     *
     * @param postId ID поста, чьи комментарии получаем
     * @return список комментариев к посту
     */
    List<ResponseCommentDto> getAllComments(long postId);

    /**
     * Создать новый комментарий
     *
     * @param postId ID поста, к которому оставляем комментарий
     * @param createCommentDto DTO с данными для создания комментария
     * @return созданный комментарий
     * @throws IllegalArgumentException если пост не существует
     * @throws IllegalArgumentException если автор не существует
     */
    ResponseCommentDto createComment(long postId, CreateCommentDto createCommentDto, long userId);

    /**
     * Обновить комментарий
     *
     * @param postId ID поста
     * @param commentId ID комментария для обновления
     * @param updateCommentDto DTO с данными для обновления
     * @return обновленный комментарий
     * @throws IllegalArgumentException если комментарий не существует
     * @throws IllegalArgumentException если комментарий не принадлежит указанному посту
     */
    ResponseCommentDto updateComment(long postId, long commentId, UpdateCommentDto updateCommentDto);

    /**
     * Удалить комментарий
     *
     * @param postId ID поста
     * @param commentId ID комментария для удаления
     * @throws IllegalArgumentException если комментарий не существует
     * @throws IllegalArgumentException если комментарий не принадлежит указанному посту
     */
    void deleteComment(long postId, long commentId);
}
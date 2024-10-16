package faang.school.postservice.controller;

import faang.school.postservice.model.dto.like.LikeDto;
import faang.school.postservice.model.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import faang.school.postservice.validator.LikeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;
    private final LikeValidator likeValidator;

    @GetMapping("/post/{postId}")
    public List<Long> getLikesForPost(@PathVariable Long postId) {
        log.info("Запрос на получение лайков для поста с ID: {}", postId);
        List<Long> userIds = likeService.getLikesFromPost(postId);
        log.info("Найдено {} лайков для поста с ID: {}", userIds.size(), postId);
        return userIds;
    }

    @GetMapping("/comment/{commentId}")
    public List<Long> getLikesForComment(@PathVariable Long commentId) {
        log.info("Запрос на получение лайков для комментария с ID: {}", commentId);
        List<Long> userIds = likeService.getLikesFromComment(commentId);
        log.info("Найдено {} лайков для комментария с ID: {}", userIds.size(), commentId);
        return userIds;
    }

    @PostMapping("/post/{postId}")
    public LikeDto addLikeToPost(@PathVariable Long postId, @RequestBody LikeDto likeDto) {
        likeValidator.likeValidation(likeDto);
        log.info("Попытка добавить лайк к посту с ID: {} от пользователя с ID: {}", postId, likeDto.getUserId());
        LikeDto result = likeService.addLikeToPost(postId, likeDto);
        log.info("Лайк успешно добавлен к посту с ID: {} от пользователя с ID: {}", postId, likeDto.getUserId());
        return result;
    }

    @DeleteMapping("/post/{postId}")
    public LikeDto removeLikeFromPost(@PathVariable Long postId, @RequestBody LikeDto likeDto) {
        likeValidator.likeValidation(likeDto); // Валидация
        log.info("Попытка удалить лайк с поста с ID: {} от пользователя с ID: {}", postId, likeDto.getUserId());
        LikeDto result = likeService.removeLikeFromPost(postId, likeDto);
        log.info("Лайк успешно удалён с поста с ID: {} от пользователя с ID: {}", postId, likeDto.getUserId());
        return  result;
    }

    @PostMapping("/comment/{commentId}")
    public LikeDto addLikeToComment(@PathVariable Long commentId, @RequestBody LikeDto likeDto) {
        likeValidator.likeValidation(likeDto); // Валидация
        log.info("Попытка добавить лайк к комментарию с ID: {} от пользователя с ID: {}", commentId, likeDto.getUserId());
        LikeDto result = likeService.addLikeToComment(commentId, likeDto);
        log.info("Лайк успешно добавлен к комментарию с ID: {} от пользователя с ID: {}", commentId, likeDto.getUserId());
        return  result;
    }

    @DeleteMapping("/comment/{commentId}")
    public LikeDto removeLikeFromComment(@PathVariable Long commentId, @RequestBody LikeDto likeDto) {
        likeValidator.likeValidation(likeDto); // Валидация
        log.info("Попытка удалить лайк с комментария с ID: {} от пользователя с ID: {}", commentId, likeDto.getUserId());
        LikeDto result = likeService.removeLikeFromComment(commentId, likeDto);
        log.info("Лайк успешно удалён с комментария с ID: {} от пользователя с ID: {}", commentId, likeDto.getUserId());
        return result;
    }

    @GetMapping(value = "/users/post/{postId}")
    public List<UserDto> getAllUsersLikedPost(@PathVariable long postId) {
        return likeService.getAllUsersLikedPost(postId);
    }

    @GetMapping(value = "/users/comment/{commentId}")
    public List<UserDto> getAllUsersLikedComment(@PathVariable long commentId) {
        return likeService.getAllUsersLikedComment(commentId);
    }
}
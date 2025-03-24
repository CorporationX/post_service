package faang.school.postservice.service;

import faang.school.postservice.dto.like.LikeRequestDto;
import faang.school.postservice.dto.like.LikeResponseDto;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.BadRequestException;
import faang.school.postservice.mapper.like.LikeRequestMapper;
import faang.school.postservice.mapper.like.LikeResponseMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.adapter.CommentRepositoryAdapter;
import faang.school.postservice.repository.adapter.PostRepositoryAdapter;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    private final UserServiceClient userServiceClient;

    private final LikeRepository likeRepository;

    private final PostRepositoryAdapter postRepositoryAdapter;
    private final CommentRepositoryAdapter commentRepositoryAdapter;
    private final LikeRequestMapper likeRequestMapper;
    private final LikeResponseMapper likeResponseMapper;
    private final PostValidator postValidator;

    @Value("${like.batch}")
    private int likeBatch;

    @Transactional
    public LikeResponseDto likePost(long postId, LikeRequestDto likeRequestDto) {
        long userId = likeRequestDto.userId();

        UserDto user = postValidator.getUserById(userId);
        Post post = postRepositoryAdapter.getByIdWithLikes(postId);

        if (likeRepository.findByPostIdAndUserId(postId, userId).isPresent()) {
            throw new BadRequestException("The user with ID " + userId + " has already liked the post with ID " + postId);
        }

        Like like = likeRequestMapper.toEntity(likeRequestDto);
        like.setUserId(user.id());
        like.setPost(post);
        like.setCreatedAt(LocalDateTime.now());
        likeRepository.save(like);

        log.info("The like with ID {} was given to the post with ID {}", like.getId(), postId);
        return likeResponseMapper.toDto(like);
    }

    @Transactional
    public LikeResponseDto removeLikeFromPost(long postId, LikeRequestDto likeRequestDto) {
        long userId = likeRequestDto.userId();

        postValidator.getUserById(userId);
        postRepositoryAdapter.getByIdWithLikes(postId);

        Optional<Like> optionalLike = likeRepository.findByPostIdAndUserId(postId, userId);

        if (optionalLike.isEmpty()) {
            throw new BadRequestException("There is no like for the user with ID " + userId + " on the post with ID "
                    + postId);
        }

        Like like = optionalLike.get();
        likeRepository.deleteByPostIdAndUserId(postId, userId);

        log.info("The like with ID {} has been deleted from the post with ID {}", like.getId(), postId);
        return likeResponseMapper.toDto(like);
    }

    @Transactional
    public LikeResponseDto likeComment(long commentId, LikeRequestDto likeRequestDto) {
        long userId = likeRequestDto.userId();

        UserDto user = postValidator.getUserById(userId);
        Comment comment = commentRepositoryAdapter.getById(commentId);

        if (likeRepository.findByCommentIdAndUserId(commentId, userId).isPresent()) {
            throw new BadRequestException("The user with ID " + userId + " has already liked the comment with ID "
                    + commentId);
        }

        Like like = likeRequestMapper.toEntity(likeRequestDto);
        like.setUserId(user.id());
        like.setComment(comment);
        like.setCreatedAt(LocalDateTime.now());
        likeRepository.save(like);

        log.info("The like with ID {} was given to the comment with ID {}", like.getId(), commentId);
        return likeResponseMapper.toDto(like);
    }

    @Transactional
    public LikeResponseDto removeLikeFromComment(long commentId, LikeRequestDto likeRequestDto) {
        long userId = likeRequestDto.userId();

        postValidator.getUserById(userId);
        commentRepositoryAdapter.getById(commentId);

        Optional<Like> optionalLike = likeRepository.findByCommentIdAndUserId(commentId, userId);

        if (optionalLike.isEmpty()) {
            throw new BadRequestException("There is no like for the user with ID " + userId + " on the comment with ID "
                    + commentId);
        }

        Like like = optionalLike.get();
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);

        log.info("The like with ID {} has been deleted from the comment with ID {}", like.getId(), commentId);
        return likeResponseMapper.toDto(like);
    }

    public List<UserDto> getUsersWhoLikedPost(long postId) {
        postRepositoryAdapter.getById(postId);

        List<Long> userIds = likeRepository.findUserIdsByPostId(postId);
        List<UserDto> users = getUsers(userIds);

        log.info("IDs of the users who liked the post with ID {}: {}", postId, users);
        return users;
    }

    public List<UserDto> getUsersWhoLikedComment(long commentId) {
        commentRepositoryAdapter.getById(commentId);

        List<Long> userIds = likeRepository.findUserIdsByCommentId(commentId);
        List<UserDto> users = getUsers(userIds);

        log.info("IDs of the users who liked the comment with ID {}: {}", commentId, users);
        return users;
    }

    private List<UserDto> getUsers(List<Long> userIds) {
        List<UserDto> users = new ArrayList<>();
        for (int i = 0; i < userIds.size(); i += likeBatch) {
            int toIndex = Math.min(i + likeBatch, userIds.size());
            List<Long> sublistOfUserIds = userIds.subList(i, toIndex);
            users.addAll(userServiceClient.getUsersByIds(sublistOfUserIds));
        }
        return users;
    }
}

package faang.school.postservice.service.impl;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.likes.BaseFilterDto;
import faang.school.postservice.dto.likes.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.CheckException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepositoryAdapter;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.LikeRepositoryAdapter;
import faang.school.postservice.repository.PostRepositoryAdapter;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final PostRepositoryAdapter postRepositoryAdapter;
    private final LikeRepositoryAdapter likeRepositoryAdapter;
    private final CommentRepositoryAdapter commentRepositoryAdapter;
    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final UserServiceClient userServiceClient;

    private static final int BATCH_SIZE = 100;

    @Transactional
    @Override
    public LikeDto likePost(long userId, long postId) {
        Like postLike = likeRepositoryAdapter.findLikeByPostIdAndUserId(userId, postId);
        if (postLike != null) {
            throw new CheckException("Вы уже поставили лайк на этот пост!");
        }
        Post post = postRepositoryAdapter.findById(postId);
        postLike = new Like();
        postLike.setUserId(userId);
        postLike.setPost(post);
        return likeMapper.toDto(likeRepository.save(postLike));
    }

    @Transactional
    @Override
    public void deletePostLike(long userId, long postId) {
        Like like = likeRepositoryAdapter.findLikeByPostIdAndUserId(userId, postId);
        if (like == null) {
            throw new CheckException("Лайк не найден!");
        }
        likeRepository.delete(like);
    }

    @Transactional
    @Override
    public LikeDto likeComment(long userId, long commentId) {
        Like commentLike = likeRepositoryAdapter.findLikeByCommentIdAndUserId(userId, commentId);
        if (commentLike != null) {
            throw new CheckException("Вы уже поставили лайк на этот комментарий!");
        }
        Comment comment = commentRepositoryAdapter.findById(commentId);
        commentLike = new Like();
        commentLike.setUserId(userId);
        commentLike.setComment(comment);
        return likeMapper.toDto(likeRepository.save(commentLike));
    }

    @Transactional
    @Override
    public void deleteCommentLike(long userId, long commentId) {
        Like like = likeRepositoryAdapter.findLikeByCommentIdAndUserId(userId, commentId);
        if (like == null) {
            throw new CheckException("Лайк не найден!");
        }
        likeRepository.delete(like);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> usersByPostId(long postId, BaseFilterDto filter) {
        postRepositoryAdapter.findById(postId);
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getCount());

        Slice<Like> likes = likeRepository.findLikesByPostId(postId, pageable);
        if (!likes.isEmpty()) {
            return getUsersByUserIds(likes.getContent());
        }
        return Collections.emptyList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> usersByCommentId(long commentId, BaseFilterDto filter) {
        commentRepositoryAdapter.findById(commentId);
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getCount());

        Slice<Like> likes = likeRepository.findLikesByCommentId(commentId, pageable);
        if (!likes.isEmpty()) {
            return getUsersByUserIds(likes.getContent());
        }
        return Collections.emptyList();
    }

    private List<UserDto> getUsersByUserIds(List<Like> likes) {
        List<Long> ids = likes.stream().map(Like::getUserId).collect(Collectors.toList());
        List<UserDto> users = new ArrayList<>();
        while (ids.size() > BATCH_SIZE) {
            List<Long> batch = ids.stream().limit(BATCH_SIZE).toList();
            users.addAll(userServiceClient.getUsersByIds(batch));
            ids.removeAll(batch);
        }
        if (!ids.isEmpty()) {
            users.addAll(userServiceClient.getUsersByIds(ids));
        }
        return users;
    }
}

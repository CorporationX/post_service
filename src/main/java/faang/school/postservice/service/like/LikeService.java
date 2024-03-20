package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final CommentRepository commentRepository;

    public List<UserDto> getUsersLikedPost(Long postId) {
        List<Like> likes = getPostLikes(postId);
        List<Long> userIds = getUsersIds(likes);
        return getUsers(userIds);
    }

    public List<UserDto> getUsersLikedComment(Long commentId) {
        List<Like> likes = getCommentLikes(commentId);
        List<Long> userIds = getUsersIds(likes);
        return getUsers(userIds);
    }

    private List<Like> getCommentLikes(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        return comment.getLikes();
    }

    private List<Like> getPostLikes(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post not found"));
        return post.getLikes();
    }

    private List<Long> getUsersIds(List<Like> likes) {
        return likes.stream()
                .map(Like::getUserId)
                .collect(Collectors.toList());
    }

    private List<List<Long>> getChoppedLists(List<Long> userIds) {
        List<List<Long>> choppedLists = new ArrayList<>();
        int size = 100;
        for (int i = 0; i < userIds.size(); i += size) {
            choppedLists.add(new ArrayList<>(userIds.subList(i, Math.min(userIds.size(), i + size))));
        }
        return choppedLists;
    }

    private List<UserDto> getUsers(List<Long> userIds) {
        List<List<Long>> choppedLists = getChoppedLists(userIds);
        List<UserDto> result = new ArrayList<>();
        for (List<Long> chunk : choppedLists) {
            List<UserDto> users = userServiceClient.getUsersByIds(chunk);
            result.addAll(users);
        }
        return result;
    }
}

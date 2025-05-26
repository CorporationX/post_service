package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final UserServiceClient userServiceClient;


    @Override
    public void addLikePost(Long postId, Long userId) {
       Post post = postRepository.findById(postId).orElseThrow(()
               -> new EntityNotFoundException("Post was not found"));

        userServiceClient.getUser(userId);
        if(post.getLikes().stream().anyMatch(like -> like.getUserId().equals(userId))){
            throw new IllegalArgumentException("Like already exists");
        }
        Like like = Like.builder()
                .post(post)
                .userId(userId)
                .build();
        likeRepository.save(like);
    }

    @Override
    public void removeLikePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(()
            -> new EntityNotFoundException("Post was not found"));

        userServiceClient.getUser(userId);
        likeRepository.deleteByPostIdAndUserId(postId, userId);

    }

    @Override
    public void addLikeComment(Long commentId, Long userId) {


    }

    @Override
    public void removeLikeComment(Long commentId, Long userId) {


    }
}

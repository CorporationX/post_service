package faang.school.postservice.service.post;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataValidationExceptions;
import faang.school.postservice.exception.NotFoundElementException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public Post validationAndPostReceived(LikeDto likeDto) {
        if (likeDto.getPostId() != null) {
            if (!postRepository.existsById(likeDto.getPostId())) {
                throw new DataValidationExceptions("no such postId exists postId: " + likeDto.getPostId());
            }
        } else {
            throw new DataValidationExceptions("arrived likeDto with postId equal to null");
        }
        return postRepository.findById(likeDto.getPostId()).orElseThrow(() ->
                new NotFoundElementException("Not found post by id: " + likeDto.getPostId()));
    }
}

package faang.school.postservice.service.feed.post;

import faang.school.postservice.dto.feed.PostFeedDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.PostFeedMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostFeedServiceImpl implements PostFeedService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final PostFeedMapper postFeedMapper;

    @Override
    @Transactional(readOnly = true)
    public PostFeedDto getPostsWithAuthor(long postId) {

        Post post = findById(postId);
        UserDto author = userService.getUserById(post.getAuthorId());

        return postFeedMapper.toDto(post, author);
    }

    private Post findById(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found with id: " + postId));
    }
}

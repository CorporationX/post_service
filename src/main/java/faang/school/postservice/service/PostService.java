package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.KafkaProducerConfig;
import faang.school.postservice.config.RedisConfig;
import faang.school.postservice.dto.post.PostCashDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.PostAndFollowersMapper;
import faang.school.postservice.mapper.PostCashDtoMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.post.PostCashRepository;
import faang.school.postservice.repository.post.PostRepository;
import faang.school.postservice.repository.user.UserDtoCashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private static final String POST_NOT_FOUND_PATTERN = "Post with ID: %s not found";
    private final KafkaProducerConfig kafkaProducerConfig;
    private  final RedisConfig redisConfig;
    private final KafkaLikeProducerService kafkaLikeProducerService;
    private  final PostMapper postMapper;
    private  final PostCashDtoMapper postCashDtoMapper;
    private  final PostAndFollowersMapper postAndFollowersMapper;
    private  final UserDtoCashRepository userCashRepository;
    private final UserServiceClient userServiceClient;

    public final PostRepository postRepository;
    private final PostCashRepository postCashRepository;

    public Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new NotFoundException(
                        String.format(POST_NOT_FOUND_PATTERN, postId)));
    }

    public Post createPost(Post post){
        Post postSaved = postRepository.save(post);
        postCashRepository.save(postCashDtoMapper.toDto(post, redisConfig.getPostTtl()));

        UserDto userDto = userServiceClient.getUser(postSaved.getAuthorId());
        userCashRepository.save(userDto);

        kafkaLikeProducerService.send(kafkaProducerConfig.getPostCreationTopicName(), postMapper.toDto(postSaved));
        kafkaLikeProducerService.send(kafkaProducerConfig.getPostAndFollowersTopicName(), postAndFollowersMapper.toDto(postSaved));
        log.info("Post is created {}", postSaved);
        return postSaved;
    }
}

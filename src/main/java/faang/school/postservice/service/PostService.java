package faang.school.postservice.service;

import faang.school.postservice.config.KafkaProducerConfig;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.PostAndFollowersMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private static final String POST_NOT_FOUND_PATTERN = "Post with ID: %s not found";
    private final KafkaProducerConfig kafkaProducerConfig;
    private final KafkaLikeProducerService kafkaLikeProducerService;
    private  final PostMapper postMapper;
    private  final PostAndFollowersMapper postAndFollowersMapper;

    public final PostRepository postRepository;

    public Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new NotFoundException(
                        String.format(POST_NOT_FOUND_PATTERN, postId)));
    }

    public Post createPost(Post post){
        Post postSaved = postRepository.save(post);
        kafkaLikeProducerService.send(kafkaProducerConfig.getPostCreationTopicName(), postMapper.toDto(postSaved));
        kafkaLikeProducerService.send(kafkaProducerConfig.getPostAndFollowersTopicName(), postAndFollowersMapper.toDto(postSaved));
        log.info("Post is created {}", postSaved);
        return postSaved;
    }
}

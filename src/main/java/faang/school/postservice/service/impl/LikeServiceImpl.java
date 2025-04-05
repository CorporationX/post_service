package faang.school.postservice.service.impl;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.kafka.producer.KafkaProducer;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static faang.school.postservice.controller.ControllerExceptionHandler.DEFAULT_SERVICE_NAME;
import static faang.school.postservice.service.impl.PostServiceImpl.POST_WITH_ID_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserContext userContext;
    private final KafkaProducer kafkaProducer;
    private final LikeMapper likeMapper;

    @Override
    public void addLike(PostDto postDto) {
        Post post = postRepository.findById(postDto.getId())
                .orElseThrow(() -> new PostNotFoundException(DEFAULT_SERVICE_NAME,
                        String.format(POST_WITH_ID_NOT_FOUND, postDto.getId())));
        Like like = likeRepository.save(new Like(userContext.getUserId(), null, post, LocalDateTime.now()));
        kafkaProducer.sendMessage(likeMapper.toEvent(like));
    }
}

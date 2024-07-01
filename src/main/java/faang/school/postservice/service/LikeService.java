package faang.school.postservice.service;

import faang.school.postservice.dto.event.LikeKafkaEvent;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaLikeProducer;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validator.LikeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostService postService;
    private final LikeMapper likeMapper;
    private final LikeValidator likeValidator;
    private final KafkaLikeProducer kafkaLikeProducer;

    @Transactional
    public LikeDto addLikeToPost(long postId, LikeDto likeDto) {
        long userId = likeDto.getUserId();
        log.info("Add like to post ID: {}, from user ID: {}", postId, userId);
        likeValidator.likeValidate(postId, userId);

        Post post = postService.findById(postId);
        Like like = likeMapper.toEntity(likeDto);
        like.setPost(post);
        LikeDto savedDto = likeMapper.toDto(likeRepository.save(like));

        kafkaLikeProducer.sendEvent(new LikeKafkaEvent(userId, postId, null));
        return savedDto;
    }
}

package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event.PostLikeEventDto;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.event.like.LikeKafkaEvent;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.kafka.KafkaLikeProducer;
import faang.school.postservice.publisher.PostLikePublisher;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;

    @Mock
    private LikeMapper likeMapper;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private PostService postService;

    @Mock
    private PostLikePublisher postLikePublisher;

    @Mock
    private CommentService commentService;

    @Mock
    private KafkaLikeProducer kafkaLikeProducer;

    @Test
    void createLikeToPost_ShouldThrowExceptionWhenLikeIdIsNotNull() {
        LikeDto likeDto = LikeDto.builder().likeId(1L).build();

        Exception exception = assertThrows(DataValidationException.class, () ->
                likeService.createLikeToPost(likeDto)
        );

        assertEquals("When creating a like, the likeId field must be empty!", exception.getMessage());
    }

    @Test
    void createLikeToPost_ShouldCreateLikeSuccessfully() {
        LikeDto likeDto = LikeDto.builder().userId(1L).postId(1L).build();

        Post post = new Post();
        Like like = new Like();

        when(postService.validationAndPostReceived(likeDto)).thenReturn(post);
        when(likeMapper.toEntity(likeDto)).thenReturn(like);
        when(likeRepository.save(like)).thenReturn(like);
        doNothing().when(postLikePublisher).publish(any(PostLikeEventDto.class));
        when(likeMapper.toDto(like)).thenReturn(likeDto);
        when(likeMapper.toEvent(likeDto)).thenReturn(new LikeKafkaEvent());

        LikeDto result = likeService.createLikeToPost(likeDto);

        assertNotNull(result);
        verify(likeRepository, times(1)).save(any(Like.class));
    }

    @Test
    void removeLikeToPost_ShouldThrowExceptionWhenLikeIdIsNull() {
        LikeDto likeDto = LikeDto.builder().userId(1L).postId(1L).build();

        Exception exception = assertThrows(DataValidationException.class, () ->
                likeService.removeLikeToPost(likeDto)
        );

        assertEquals("It is not possible to delete a like with a null likeId or a non-existent like in the database likeId: null", exception.getMessage());
    }

    @Test
    void removeLikeToPost_ShouldThrowExceptionWhenLikeIdDoesNotExist() {
        LikeDto likeDto = LikeDto.builder().likeId(1L).userId(1L).postId(1L).build();

        when(likeRepository.existsById(likeDto.getLikeId())).thenReturn(false);

        Exception exception = assertThrows(DataValidationException.class, () ->
                likeService.removeLikeToPost(likeDto)
        );

        assertEquals("It is not possible to delete a like with a null likeId or a non-existent like in the database likeId: 1", exception.getMessage());
    }

    @Test
    void removeLikeToPost_ShouldRemoveLikeSuccessfully() {
        LikeDto likeDto = LikeDto.builder().likeId(1L).userId(1L).postId(1L).build();
        Post post = new Post();
        Like like = new Like();
        post.setLikes(new ArrayList<>());
        post.getLikes().add(like);

        when(likeRepository.existsById(likeDto.getLikeId())).thenReturn(true);
        when(postService.validationAndPostReceived(likeDto)).thenReturn(post);
        when(likeRepository.findById(likeDto.getLikeId())).thenReturn(Optional.of(like));
        when(likeMapper.toEntity(likeDto)).thenReturn(like);
        when(likeMapper.toEvent(likeDto)).thenReturn(new LikeKafkaEvent());

        LikeDto result = likeService.removeLikeToPost(likeDto);

        assertNotNull(result);
        verify(likeRepository, times(1)).delete(like);
    }

    @Test
    void createLikeToComment_ShouldThrowExceptionWhenLikeIdIsNotNullOrExists() {
        LikeDto likeDto = LikeDto.builder().likeId(1L).build();

        assertThrows(DataValidationException.class, () ->
                likeService.createLikeToComment(likeDto));
    }

    @Test
    void createLikeToComment_ShouldCreateLikeSuccessfully() {
        LikeDto likeDto = LikeDto.builder().userId(1L).commentId(1L).build();
        Comment comment = new Comment();
        Like like = new Like();
        comment.setLikes(new ArrayList<>());
        comment.getLikes().add(like);

        when(commentService.validationAndCommentsReceived(likeDto)).thenReturn(comment);
        when(likeMapper.toEntity(likeDto)).thenReturn(like);
        when(likeRepository.save(like)).thenReturn(like);
        when(likeMapper.toDto(like)).thenReturn(likeDto);

        LikeDto result = likeService.createLikeToComment(likeDto);

        assertNotNull(result);
        verify(likeRepository, times(1)).save(like);
    }

    @Test
    void removeLikeToComment_ShouldThrowExceptionWhenLikeIdIsNull() {
        LikeDto likeDto = LikeDto.builder().userId(1L).commentId(1L).build();

        Exception exception = assertThrows(DataValidationException.class, () ->
                likeService.removeLikeToComment(likeDto)
        );

        assertEquals("It is not possible to delete a like with a null likeId or a non-existent like in the database likeId: null", exception.getMessage());
    }

    @Test
    void removeLikeToComment_ShouldThrowExceptionWhenLikeIdDoesNotExist() {
        LikeDto likeDto = LikeDto.builder().likeId(1L).userId(1L).commentId(1L).build();

        when(likeRepository.existsById(likeDto.getLikeId())).thenReturn(false);

        Exception exception = assertThrows(DataValidationException.class, () ->
                likeService.removeLikeToComment(likeDto)
        );

        assertEquals("It is not possible to delete a like with a null likeId or a non-existent like in the database likeId: 1", exception.getMessage());
    }

    @Test
    void removeLikeToComment_ShouldRemoveLikeSuccessfully() {
        LikeDto likeDto = LikeDto.builder().likeId(1L).userId(1L).commentId(1L).build();
        Comment comment = new Comment();
        Like like = new Like();
        comment.setLikes(new ArrayList<>());
        comment.getLikes().add(like);

        when(likeRepository.existsById(likeDto.getLikeId())).thenReturn(true);
        when(commentService.validationAndCommentsReceived(likeDto)).thenReturn(comment);
        when(likeRepository.findById(likeDto.getLikeId())).thenReturn(Optional.of(like));
        when(likeMapper.toEntity(likeDto)).thenReturn(like);

        LikeDto result = likeService.removeLikeToComment(likeDto);

        assertNotNull(result);
        verify(likeRepository, times(1)).delete(any(Like.class));
    }
}
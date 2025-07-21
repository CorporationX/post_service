package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

import java.util.List;

public interface PostService {
    PostDto create(CreatePostDto createPostDto);

    PostDto publish(@NonNull Long postId);

    PostDto update(UpdatePostDto updatePostDto);

    void delete(@NotNull Long postId);

    PostDto getById(@NotNull Long postId);

    List<PostDto> getDraftsByUser(@NotNull Long userId);

    List<PostDto> getDraftsByProject(@NotNull Long projectId);

    List<PostDto> getPublishedByUser(@NotNull Long userId);

    List<PostDto> getPublishedByProject(@NotNull Long projectId);
}

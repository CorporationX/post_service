package faang.school.postservice.controller.post;

import faang.school.postservice.dto.common.PageResponse;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springframework.data.domain.Pageable;

public interface PostApi {
    @Operation(description = "Create post",
            parameters = @Parameter(name = "x-user-id", in = ParameterIn.HEADER, required = true)
    )
    PostDto createPostAsDraft(PostCreateDto postCreateDto);

    @Operation(description = "Publish post",
            parameters = @Parameter(name = "x-user-id", in = ParameterIn.HEADER, required = true)
    )
    PostDto publishPost(Long postId);

    @Operation(description = "Delete post",
            parameters = @Parameter(name = "x-user-id", in = ParameterIn.HEADER, required = true)
    )
    void deletePostSoftly(Long postId);

    @Operation(description = "Find published posts with filter",
            parameters = @Parameter(name = "x-user-id", in = ParameterIn.HEADER, required = true)
    )
    PageResponse<PostDto> findAllPublished(
            Long authorId,
            Long projectId,
            Pageable pageable
    );

    @Operation(description = "Find post by id",
            parameters = @Parameter(name = "x-user-id", in = ParameterIn.HEADER, required = true)
    )
    PostDto findById(Long postId);

    @Operation(description = "Update post by id",
            parameters = @Parameter(name = "x-user-id", in = ParameterIn.HEADER, required = true)
    )
    PostDto updatePost(Long postId, PostUpdateDto postUpdateDto);

    @Operation(description = "Find draft posts by author",
            parameters = @Parameter(name = "x-user-id", in = ParameterIn.HEADER, required = true)
    )
    PageResponse<PostDto> findAllDraftsByAuthor(Pageable pageable);

    @Operation(description = "Find draft posts by project",
            parameters = @Parameter(name = "x-user-id", in = ParameterIn.HEADER, required = true)
    )
    PageResponse<PostDto> findAllDraftsByProject(Long projectId, Pageable pageable);
}

package faang.school.postservice.controller.post;

import faang.school.postservice.dto.common.PageResponse;
import faang.school.postservice.dto.post.PostV2CreateDto;
import faang.school.postservice.dto.post.PostV2Dto;
import faang.school.postservice.dto.post.PostV2UpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Pageable;

public interface PostV2Api {
    @Operation(description = "Create post",
            parameters = @Parameter(name = "x-user-id", in = ParameterIn.HEADER, required = true)
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Draft created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostV2Dto.class)))
    })
    PostV2Dto createPostAsDraft(
            @Parameter(description = "Payload to create a post") PostV2CreateDto postV2CreateDto
    );

    @Operation(description = "Publish post",
            parameters = @Parameter(name = "x-user-id", in = ParameterIn.HEADER, required = true)
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post published successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostV2Dto.class)))
    })
    PostV2Dto publishPost(
            @Parameter(description = "Post identifier") Long postId
    );

    @Operation(description = "Delete post",
            parameters = @Parameter(name = "x-user-id", in = ParameterIn.HEADER, required = true)
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Post marked as deleted", content = @Content)
    })
    void deletePostSoftly(
            @Parameter(description = "Post identifier") Long postId
    );

    @Operation(description = "Find published posts with filter",
            parameters = @Parameter(name = "x-user-id", in = ParameterIn.HEADER, required = true)
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Published posts list",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageResponse.class)))
    })
    PageResponse<PostV2Dto> findAllPublished(
            @Parameter(description = "Author identifier to filter by") Long authorId,
            Pageable pageable
    );

    @Operation(description = "Find post by id",
            parameters = @Parameter(name = "x-user-id", in = ParameterIn.HEADER, required = true)
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostV2Dto.class)))
    })
    PostV2Dto findById(
            @Parameter(description = "Post identifier") Long postId
    );

    @Operation(description = "Update post by id",
            parameters = @Parameter(name = "x-user-id", in = ParameterIn.HEADER, required = true)
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostV2Dto.class)))
    })
    PostV2Dto updatePost(
            @Parameter(description = "Post identifier") Long postId,
            @Parameter(description = "Payload to update a post") PostV2UpdateDto postV2UpdateDto
    );

    @Operation(description = "Find draft posts by author",
            parameters = @Parameter(name = "x-user-id", in = ParameterIn.HEADER, required = true)
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Author drafts",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageResponse.class)))
    })
    PageResponse<PostV2Dto> findAllDraftsByAuthor(Pageable pageable);
}

package faang.school.postservice.controller;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("${endpoint.context-path.post}")
public class PostController {

    private final PostService postService;

    @PostMapping("/create")
    @Operation(
            summary = "Creating a draft post",
            description = "Exactly one author user or project creates a draft of the post",
            parameters = {@Parameter(in = ParameterIn.HEADER, name = "x-user-id", description = "User ID", required = true)}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The post has been created"),
            @ApiResponse(responseCode = "500", description = "There is no access to the database of users or projects")
    })
    public PostDto createPost(@Valid @RequestBody PostDto postDto) {
        return postService.createPost(postDto);
    }


    public PostDto publishPost(@PathVariable @Min(1) long postId) {
        return postService.publishPost(postId);
    }

}
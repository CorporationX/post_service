package faang.school.postservice.mapper.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.ReadPostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Post;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class PostToReadPostDtoMapper {

    @Autowired
    protected UserServiceClient userServiceClient;
    @Autowired
    protected ProjectServiceClient projectServiceClient;

    @Mapping(target = "project", expression = "java(findProjectById(post.getProjectId()))")
    @Mapping(target = "author", expression = "java(findAuthorById(post.getAuthorId()))")
    public abstract ReadPostDto map(Post post);

    //TODO:11.08.2024 позжу протестировать Retryable + обработку других исключений. Сейчас в ProjectService и UserService, методы поиска не реализованы
    @Retryable(value = {FeignException.class}, maxAttempts = 5, backoff = @Backoff(delay = 5000, multiplier = 2))
    protected ProjectDto findProjectById(Long id) {
//        return Optional.ofNullable(projectServiceClient.getProject(id)).orElseThrow(() -> {
//            log.error(String.format("PostToReadPostDtoMapper.findProjectById: Project with id %s not found", id));
//            return new IllegalArgumentException("Project with id " + id + " not found");
//        });
        return ProjectDto.builder()
                .id(id)
                .build();
    }

    @Retryable(value = {FeignException.class}, maxAttempts = 5, backoff = @Backoff(delay = 5000, multiplier = 2))
    protected UserDto findAuthorById(Long id) {
//        return Optional.ofNullable(userServiceClient.getUser(id)).orElseThrow(() -> {
//            log.error(String.format("PostToReadPostDtoMapper.findAuthorById: Author with id %s not found", id));
//            return new IllegalArgumentException("Author with id " + id + " not found");
//        });
        return UserDto.builder()
                .id(id)
                .build();
    }
}
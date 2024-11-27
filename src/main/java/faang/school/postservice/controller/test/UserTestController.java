package faang.school.postservice.controller.test;

import faang.school.postservice.model.CacheableUser;
import faang.school.postservice.repository.UserCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test/user")
@RequiredArgsConstructor
public class UserTestController {
    private final UserCacheRepository repository;

    @GetMapping("/{userId}")
    public CacheableUser getUser(@PathVariable long userId) {
        return repository.findById(userId).orElse(null);
    }
}

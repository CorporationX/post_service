package faang.school.postservice.service.cache;

import faang.school.postservice.dto.redis.cash.UserCache;
import faang.school.postservice.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cacheUser")
@RequiredArgsConstructor
public class KekUserController {

    private final UserCacheService userCacheService;

    @GetMapping("/{id}")
    public UserCache get(@PathVariable long id) {
        return userCacheService.get(id);
    }

    @PostMapping
    public void save(@RequestBody UserDto userDto) {
        userCacheService.save(userDto);
    }

    @PutMapping
    public void update(@RequestBody UserDto userDto) {
        userCacheService.update(userDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        userCacheService.delete(id);
    }
}

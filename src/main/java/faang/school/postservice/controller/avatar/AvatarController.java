package faang.school.postservice.controller.avatar;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.service.AvatarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
public class AvatarController {
    private final AvatarService avatarService;
    private final UserServiceClient userServiceClient;

    @PostMapping("/avatars/save")
    public void uploadAvatar(@RequestParam(value = "file") MultipartFile file,
                           @RequestHeader(value = "x-user-id") long userId) {
        avatarService.saveAvatar(userId, file);
    }

    @GetMapping("/avatars/{key}")
    public void getAvatar(@PathVariable String key) {
        avatarService.getAvatar(key);
    }

    @DeleteMapping("/avatars/delete/{smallImageKey}/{largeImageKey}")
    public void deleteAvatar(@RequestHeader(value = "x-user-id") long userId,
                             @PathVariable(value = "largeImageKey") String largeImageKey,
                             @PathVariable(value = "smallImageKey") String smallImageKey) {
        avatarService.deleteAvatar(userId, largeImageKey, smallImageKey);
    }

    @GetMapping("/user/username")
    public String getUserName(@RequestHeader("x-user-id") long userId) {
        return userServiceClient.getUserById(userId).getUsername();
    }
}
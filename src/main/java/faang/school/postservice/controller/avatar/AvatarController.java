package faang.school.postservice.controller.avatar;

import faang.school.postservice.service.AvatarService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
public class AvatarController {
    private final AvatarService avatarService;

    @PostMapping("/avatars/upload")
    public void uploadAvatar(@RequestParam(value = "file") MultipartFile file,
                           @RequestHeader(value = "x-user-id") long userId) {
        avatarService.saveAvatar(userId, file);
    }

    @GetMapping(value = "/avatars/download", produces = MediaType.IMAGE_PNG_VALUE)
    public InputStreamResource downloadAvatar(@RequestBody String key) {
        return avatarService.getAvatar(key);
    }

    @DeleteMapping("/avatars/delete")
    public void deleteAvatar(@RequestHeader(value = "x-user-id") long userId) {
        avatarService.deleteAvatar(userId);
    }
}
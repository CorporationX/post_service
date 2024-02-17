package faang.school.postservice.controller;

import faang.school.postservice.dto.AdDto;
import faang.school.postservice.service.ad.AdService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ads")
@RequiredArgsConstructor
public class AdController {
    private final AdService adService;

    @PostMapping
    public AdDto create(@Valid @RequestBody AdDto adDto) {
        return adService.create(adDto);
    }

    @DeleteMapping
    public AdDto remove(@PathVariable Long id) {
        return adService.remove(id);
    }
}

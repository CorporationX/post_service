package faang.school.postservice.controller.ad;

import faang.school.postservice.model.dto.ad.AdDto;
import faang.school.postservice.service.AdService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ad")
public class AdController {

    private final AdService adService;

    @PostMapping
    public AdDto buyAd(@Valid @RequestBody AdDto dto) {
        return adService.buyAd(dto);
    }
}

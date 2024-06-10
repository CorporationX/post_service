package faang.school.postservice.filter;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface AlbumFilter {

    boolean isApplicable(AlbumFilterDto filterDto);

    void apply(List<AlbumDto> albums, AlbumFilterDto filterDto);
}

package faang.school.postservice.dto.album;

import lombok.Data;

import java.time.LocalDate;


@Data
public class AlbumFilterDto {
    private String titlePattern;
    private String descriptionPattern;
    private Integer maxPostsCountPattern;
    private Integer minPostsCountPattern;
    private LocalDate fromCreatedDatePattern;
    private LocalDate fromUpdatedDatePattern;
}

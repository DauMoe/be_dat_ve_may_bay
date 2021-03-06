package com.outsource.bookingticket.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightNewsDTO {
    private Integer flightNewsId;

    private String title;

    private String content;

    private String mainImage;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdTime;

    private String author;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime updatedTime;

    private String updateBy;

    private MultipartFile fileImage;

    private List<MultipartFile> extraImage;

    private String[] imageIDs;

    private String[] imageNames;
}

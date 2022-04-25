package com.outsource.bookingticket.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AirplaneDTO {
    @JsonProperty("airplane_name")
    private String airplaneName;
    private String brand;
    @JsonProperty("link_img_brand")
    private String linkImgBrand;
}

package com.outsource.bookingticket.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AirplaneDTO {
    @JsonProperty("airplane_id")
    private Integer airplaneId;
    @JsonProperty("airplane_name")
    private String airplaneName;
    private Integer capacity;
    private String brand;
    @JsonProperty("link_img_brand")
    private String linkImgBrand;
}

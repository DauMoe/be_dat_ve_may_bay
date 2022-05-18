package com.outsource.bookingticket.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailPriceDTO {
    @JsonProperty("number_people")
    private Integer numberPeople;

    private Long price;

    @JsonProperty("total_price")
    private Long totalPrice;
}

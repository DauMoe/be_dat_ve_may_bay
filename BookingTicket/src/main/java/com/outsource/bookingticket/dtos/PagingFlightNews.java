package com.outsource.bookingticket.dtos;

import com.outsource.bookingticket.entities.flight_news.FlightNews;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PagingFlightNews {

    private int currentPage;
    private int totalPages;
    private long startCount;
    private long endCount;
    private long totalItems;
    private String sortField;
    private String sortDir;
    private String keyword;
    private String reverseSortDir;
    private List<FlightNews> flightNewsList = new ArrayList<>();
}

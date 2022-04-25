package com.outsource.bookingticket.entities.flight_news;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.*;

@Entity
@Table(name = "flight_news_image")
@NoArgsConstructor
@AllArgsConstructor
public class FlightNewsImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flight_news_image_id", nullable = false, unique = true)
    private Integer flightNewsImageId;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "flight_news_id")
    @JsonIgnore
    private FlightNews flightNews;

    public FlightNewsImage(String name, FlightNews flightNews) {
        this.name = name;
        this.flightNews = flightNews;
    }

    @Transient
    public String getImagePath() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("BookingTicket/images/flight-news-image/" + flightNews.getFlightNewsId() + "/extras/" + this.name).toUriString();
    }

    public Integer getFlightNewsImageId() {
        return flightNewsImageId;
    }

    public void setFlightNewsImageId(Integer flightNewsImageId) {
        this.flightNewsImageId = flightNewsImageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FlightNews getFlightNews() {
        return flightNews;
    }

    public void setFlightNews(FlightNews flightNews) {
        this.flightNews = flightNews;
    }
}

package com.outsource.bookingticket.entities.flight_news;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Entity
@Table(name = "flight_news")
@NoArgsConstructor
@AllArgsConstructor
public class FlightNews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flight_news_id")
    private Integer flightNewsId;

    @Column(name = "title", length = 150)
    private String title;

    @Column(name = "content", length = 4000)
    private String content;

    @Column(name = "main_image", nullable = false)
    private String mainImage;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "author", length = 150)
    private String author;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Column(name = "update_by", length = 150)
    private String updateBy;

    @OneToMany(mappedBy = "flightNews", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FlightNewsImage> images =  new HashSet<>();

    @Transient
    public String getMainImagePath() {
        if (flightNewsId == null || mainImage == null) return ServletUriComponentsBuilder.fromCurrentContextPath().path("BookingTicket/images/flight-news-image/image-thumbnail.png").toUriString();

        return ServletUriComponentsBuilder.fromCurrentContextPath().path("BookingTicket/images/flight-news-image/" + this.flightNewsId + "/" + this.mainImage).toUriString();
    }

    // Extra Image to Set Collection
    public void addExtraImage(String imageName) {
        this.images.add(new FlightNewsImage(imageName, this));
    }

    public boolean containsImageName(String imageName) {
        Iterator<FlightNewsImage> iterator = images.iterator();

        while (iterator.hasNext()) {
            FlightNewsImage image = iterator.next();
            if (image.getName().equals(imageName)) {
                return true;
            }
        }

        return false;
    }

    public Integer getFlightNewsId() {
        return flightNewsId;
    }

    public void setFlightNewsId(Integer flightNewsId) {
        this.flightNewsId = flightNewsId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Set<FlightNewsImage> getImages() {
        return images;
    }

    public void setImages(Set<FlightNewsImage> images) {
        this.images = images;
    }
}

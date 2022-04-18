package com.outsource.bookingticket.utils;

import com.outsource.bookingticket.entities.flight_news.FlightNews;
import com.outsource.bookingticket.entities.flight_news.FlightNewsImage;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FlightNewsSaveHelper {

    public static void setMainImageName(MultipartFile mainImageFile, FlightNews flightNews) {
        if (!mainImageFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageFile.getOriginalFilename());
            flightNews.setMainImage(fileName);
        }
    }

    // Cập nhật các extraImage ra 1 Set mới
    public static void setExistingExtraImageNames(String[] imageIDs, String[] imageNames, FlightNews flightNews) {
        if (imageIDs == null || imageIDs.length == 0) return;

        Set<FlightNewsImage> images = new HashSet<>();

        for (int count = 0; count < imageIDs.length; count++) {
            Integer id = Integer.parseInt(imageIDs[count]);
            String name = imageNames[count];
            images.add(new FlightNewsImage(id, name, flightNews));
        }

        flightNews.setImages(images); // Vì giá trị ProductImage lưu trong Set, lên khi thay đổi thì chúng sẽ bị tác động
    }

    public static void setNewExtraImageNames(List<MultipartFile> extraImageFiles, FlightNews flightNews) {
        if (extraImageFiles.size() > 0) {
            for (MultipartFile multipartFile : extraImageFiles) {
                if (!multipartFile.isEmpty()) {
                    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

                    if (!flightNews.containsImageName(fileName)) { // Check image exit
                        flightNews.addExtraImage(fileName);
                    }
                }
            }
        }
    }

    public static void saveUploadImages(MultipartFile mainImage, List<MultipartFile> extraImage, FlightNews flightNews) throws IOException {
        if (!mainImage.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImage.getOriginalFilename());
            String uploadDir = "BookingTicket/images/flight-news-image/" + flightNews.getFlightNewsId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, mainImage);
        }

        if (extraImage.size() > 0) {
            String uploadDir = "BookingTicket/images/flight-news-image/" + flightNews.getFlightNewsId() + "/extras/";

            for (MultipartFile multipartFile : extraImage) {
                if (multipartFile.isEmpty()) continue;

                String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
            }
        }
    }

    public static void deleteExtraImagesWereRemovedOnForm(FlightNews flightNews) { // Xóa ảnh trong folder
        String extraImageDir = "BookingTicket/images/product-photo/" + flightNews.getFlightNewsId() + "/extras/";

        Path dir = Paths.get(extraImageDir);

        try {
            Files.list(dir).forEach(file -> {
                String fileName = file.toFile().getName();

                if (!flightNews.containsImageName(fileName)) {
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Could not delete extra image: " + fileName);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not list directory: " + dir);
        }
    }
}

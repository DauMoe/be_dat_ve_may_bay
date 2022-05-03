package com.outsource.bookingticket.controllers;

import com.outsource.bookingticket.constants.Constants;
import com.outsource.bookingticket.dtos.EditFlightNewsDTO;
import com.outsource.bookingticket.dtos.FlightNewsDTO;
import com.outsource.bookingticket.dtos.LocationRequestDTO;
import com.outsource.bookingticket.dtos.PagingFlightNews;
import com.outsource.bookingticket.dtos.commons.ResponseCommon;
import com.outsource.bookingticket.entities.flight_news.FlightNews;
import com.outsource.bookingticket.entities.users.UserEntity;
import com.outsource.bookingticket.pojo.SignupRequest;
import com.outsource.bookingticket.utils.FileUploadUtil;
import com.outsource.bookingticket.utils.FlightNewsSaveHelper;
import com.outsource.bookingticket.utils.Helper;
import com.outsource.bookingticket.utils.MessageUtil;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController extends BaseController {

    // API lấy hết danh sách thông tin chuyến bay
    @CrossOrigin(maxAge = 3600, origins = "*")
    @GetMapping(path = "/list-flight")
    ResponseEntity<?> getAllFlight(@RequestParam(value = "from_airport", required = false) Integer fromAirportId,
                                   @RequestParam(value = "to_airport", required = false) Integer toAirportId,
                                   @RequestParam(value = "flight_no", required = false) String flightNo) {
        return flightService.getAllFlight(fromAirportId, toAirportId, flightNo);
    }

    // API lấy hết danh sách thông tin vé theo schedule ID
    @CrossOrigin(maxAge = 3600, origins = "*")
    @GetMapping(path = "/list-ticket")
    ResponseEntity<?> getAllTicket(@RequestParam(value = "flight_schedule_id") Integer flightScheduleId) {
        return ticketService.getAllTicketByScheduleId(flightScheduleId);
    }

    // API cancel vé theo ID của vé
    @CrossOrigin(maxAge = 3600, origins = "*")
    @PutMapping(path = "/cancel-ticket")
    public ResponseEntity<?> cancelTicket(@RequestParam("ticket_id") Integer ticketId) {
        ResponseCommon response = ticketService.cancelTicket(ticketId);
        return ResponseEntity.ok(response);
    }

    // API khoá chuyến bay
    @CrossOrigin(maxAge = 3600, origins = "*")
    @PutMapping(path = "/lock-flight/{flight_id}")
    ResponseEntity<?> getAllFlight(@PathVariable("flight_id") Integer flightId) {
        return flightService.updateFlightState(flightId);
    }

    @CrossOrigin(maxAge = 3600, origins = "*")
    @PostMapping(path = "/location-add")
    ResponseEntity<?> addLocation(@RequestBody LocationRequestDTO locationRequestDTO) {
        return locationService.addLocation(locationRequestDTO);
    }

    @CrossOrigin(maxAge = 3600, origins = "*")
    @PutMapping(path = "/location-update/{location_id}")
    ResponseEntity<?> addLocation(@PathVariable("location_id") Integer locationId,
                                  @RequestBody LocationRequestDTO locationRequestDTO) {
        return locationService.editLocation(locationId, locationRequestDTO);
    }

    /*************API Quản lý Tin Tức Chuyến Bay******************/
    // API cho lấy tất cả các tin tuc chuyến bay
    @CrossOrigin(maxAge = 3600, origins = "*")
    @GetMapping(value = "/flight-news", produces = "application/json")
    public ResponseEntity<?> listAllFlightNews() {
        return new ResponseEntity<>(flightNewsService.listAll(), HttpStatus.OK);
    }

    // API Lấy danh sách trang đầu tiên
    @CrossOrigin(maxAge = 3600, origins = "*")
    @GetMapping(value = "/flight-news/firstPage", produces = "application/json")
    public ResponseEntity<?> listFirstPage() {
        return listFlightNews(1, "title", "asc", null);
    }

    // API Lấy danh sách trang yêu cầu
    @CrossOrigin(maxAge = 3600, origins = "*")
    @GetMapping(value = "/flight-news/page", produces = "application/json")
    public ResponseEntity<?> listFlightNews(@RequestParam(value = "pageNum") Integer pageNum,
                                         @RequestParam(value = "sortField") String sortField,
                                         @RequestParam(value = "sortDir") String sortDir,
                                         @RequestParam(value = "keyword", required = false) String keyword) {
        Page<FlightNews> page = flightNewsService.listByPage(pageNum, sortField, sortDir, keyword);

        List<FlightNews> flightNews = page.getContent();
        long startCount = (pageNum - 1) * Constants.FLIGHT_NEWS_PER_PAGE + 1; // Start at index element
        long endCount = startCount + Constants.FLIGHT_NEWS_PER_PAGE - 1; // Index of End element

        if (endCount > page.getTotalElements()) { // The last page
            endCount = page.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
        PagingFlightNews pagingFlightNews = new PagingFlightNews();

        pagingFlightNews.setFlightNewsList(flightNews);
        pagingFlightNews.setCurrentPage(pageNum);
        pagingFlightNews.setTotalPages(page.getTotalPages());
        pagingFlightNews.setStartCount(startCount);
        pagingFlightNews.setEndCount(endCount);
        pagingFlightNews.setTotalItems(page.getTotalElements());
        pagingFlightNews.setSortField(sortField);
        pagingFlightNews.setSortDir(sortDir);
        pagingFlightNews.setKeyword(keyword);
        pagingFlightNews.setReverseSortDir(reverseSortDir);

        ResponseCommon responseCommon = new ResponseCommon();
        responseCommon.setCode(200);
        responseCommon.setResult(pagingFlightNews);

        return new ResponseEntity<>(responseCommon, HttpStatus.OK);
    }

    // API tạo 1 tin tức chuyến bay
    @CrossOrigin(maxAge = 3600, origins = "*")
    @PostMapping(value = "/flight-news/save", produces = "application/json", consumes = {"multipart/form-data", "application/json"})
    public ResponseEntity<?> saveFlightNews(@ModelAttribute FlightNewsDTO flightNewsDTO) throws IOException {
        // Convert dữ liệu từ FrontEnd về đối tượng ở BackEnd
        FlightNews flightNews = convertToFlightNews(flightNewsDTO);
        // Lưu ảnh chính và các ảnh phụ
        FlightNewsSaveHelper.setMainImageName(flightNewsDTO.getFileImage(), flightNews);
        FlightNewsSaveHelper.setExistingExtraImageNames(flightNewsDTO.getImageIDs(), flightNewsDTO.getImageNames(), flightNews); // Set image for Extra Image already have in server

        FlightNewsSaveHelper.setNewExtraImageNames(flightNewsDTO.getExtraImage(), flightNews); // Set newExtraImage to the Set collection
        // Lưu đối tượng FlightNews
        FlightNews savedFlightNews = flightNewsService.saveFlightNews(flightNews);
        // Lưu file ảnh xuống server
        FlightNewsSaveHelper.saveUploadImages(flightNewsDTO.getFileImage(), flightNewsDTO.getExtraImage(), flightNews);
        // Xoá các ảnh phụ cũ
        FlightNewsSaveHelper.deleteExtraImagesWereRemovedOnForm(flightNews);

        ResponseCommon responseCommon = new ResponseCommon();
        responseCommon.setCode(200);
        responseCommon.setResult(savedFlightNews);

        return new ResponseEntity(responseCommon, HttpStatus.OK);
    }

    // API lấy thông tin của tin tức chuyến bay sẽ sửa
    @CrossOrigin(maxAge = 3600, origins = "*")
    @GetMapping(value = "/flight-news/edit", produces = "application/json")
    public ResponseEntity<?> editFlightNews(@RequestParam(value = "id") Integer id) {
        // Gọi tầng service lất đối tượng theo id
        FlightNews flightNews = flightNewsService.get(id);
        // Lấy số lượng ảnh phụ của đối tượng
        Integer numberOfExistingExtraImages = flightNews.getImages().size();

        EditFlightNewsDTO editFlightNewsDTO = new EditFlightNewsDTO();
        editFlightNewsDTO.setFlightNews(flightNews);
        editFlightNewsDTO.setNumberOfExistingExtraImages(numberOfExistingExtraImages);

        ResponseCommon responseCommon = new ResponseCommon();
        responseCommon.setCode(200);
        responseCommon.setResult(editFlightNewsDTO);

        return new ResponseEntity<>(responseCommon, HttpStatus.OK);
    }

    @CrossOrigin(maxAge = 3600, origins = "*")
    @GetMapping(value = "/flight-news/delete", produces = "application/json")
    public ResponseEntity<?> deleteFlightNews(@RequestParam(value = "id") Integer id) {
        // Xoá đối tượng tin tức chuyến bay theo ID.
        flightNewsService.deleteFlightNewsById(id);

        String flightNewsExtraImagesDir = "BookingTicket/images/flight-news-image/" + id + "/extras/";
        String flightNewsImagesDir = "BookingTicket/images/flight-news-image/" + id;
        // Xoá ảnh phụ ở trong server
        FileUploadUtil.removeDir(flightNewsExtraImagesDir);
        // Xoá ảnh chính ở trong server
        FileUploadUtil.removeDir(flightNewsImagesDir);

        ResponseCommon responseCommon = new ResponseCommon();
        responseCommon.setCode(200);
        responseCommon.setResult("Delete FlightNews with " + id + " success!");

        return new ResponseEntity<>(responseCommon, HttpStatus.OK);
    }

    // Hàm convert dữ liệu từ FrontEnd về đối tượng ở BackEnd
    private static FlightNews convertToFlightNews(FlightNewsDTO flightNewsDTO) {
        FlightNews flightNews = new FlightNews();

        if (flightNewsDTO.getFlightNewsId() != null) {
            flightNews.setFlightNewsId(flightNewsDTO.getFlightNewsId());
        }

        flightNews.setAuthor(flightNewsDTO.getAuthor());
        flightNews.setContent(flightNewsDTO.getContent());
        flightNews.setTitle(flightNewsDTO.getTitle());
        flightNews.setCreatedTime(flightNewsDTO.getCreatedTime());
        flightNews.setUpdatedTime(flightNewsDTO.getUpdatedTime());
        flightNews.setUpdateBy(flightNewsDTO.getUpdateBy());

        return flightNews;
    }

    // API Quản lý Account Admin
    @CrossOrigin(maxAge = 3600, origins = "*")
    @PostMapping(value = "/account/create", produces = "application/json")
    public ResponseEntity<?> createUser(@RequestBody SignupRequest signupRequest) {
        // đăng ký account
        ResponseCommon responseCommon = new ResponseCommon();
        // Gọi tới hàm kiểm tra email hợp và tồn tại không
        if (userService.exitUserByEmail(signupRequest.getEmail())) {
            responseCommon.setCode(204);
            responseCommon.setResult("There has error!");
            return new ResponseEntity<>(responseCommon, HttpStatus.OK);
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(signupRequest.getUsername());
        userEntity.setEmail(signupRequest.getEmail());
        userEntity.setPassword(signupRequest.getPassword());
        // Gọi hàm tạo user
        userService.registerUser(userEntity);

        responseCommon.setCode(200);
        responseCommon.setResult("Registration success");

        return new ResponseEntity<>(responseCommon, HttpStatus.OK);
    }

    //API xóa location
    @CrossOrigin(maxAge = 3600, origins = "*")
    @DeleteMapping(value = "/location-delete/{location-id}")
    public ResponseEntity<?> deleteLocation(@PathVariable(name = "location-id") Integer locationId){
        return ResponseEntity.ok(locationService.deleteLocation(locationId));
    }
}

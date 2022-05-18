package com.outsource.bookingticket.services;

import com.outsource.bookingticket.dtos.LocationRequestDTO;
import com.outsource.bookingticket.dtos.LocationResponseDTO;
import com.outsource.bookingticket.dtos.commons.ResponseCommon;
import com.outsource.bookingticket.entities.airport.AirportGeo;
import com.outsource.bookingticket.entities.location.Location;
import com.outsource.bookingticket.exception.ErrorException;
import com.outsource.bookingticket.utils.Helper;
import com.outsource.bookingticket.utils.MessageUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class LocationService extends BaseService {

    // Hàm thêm địa điểm mới
    public ResponseEntity<?> addLocation(LocationRequestDTO locationRequestDTO) {
        Optional<Location> existLocationOp =
                locationRepository.findLocationsByCountryNameAndCityName(
                        locationRequestDTO.getCountryName(),
                        locationRequestDTO.getCityName());
        if (existLocationOp.isPresent()) {
            Optional<AirportGeo> existAirportGeo =
                    airportGeoRepository.findByAirportNameAndLocationId(
                            locationRequestDTO.getAirportName(),
                            existLocationOp.get().getLocationId());
            if (existAirportGeo.isPresent()) {
                throw new ErrorException(MessageUtil.INSERT_NOT_SUCCESS);
            }
        }
        // Tìm kiếm thông tin quốc gia đã tồn tại ở bản ghi khác dưới DB chưa
        Optional<Location> existCountryOp =
                locationRepository.findFirstByCountryName(locationRequestDTO.getCountryName());
        // Kiểm tra tham số truyền vào có rỗng hay không, nếu có sẽ trả ra lỗi
        if (!StringUtils.hasLength(locationRequestDTO.getCityName()) ||
                !StringUtils.hasLength(locationRequestDTO.getCountryName()) ||
                !StringUtils.hasLength(locationRequestDTO.getAirportName())) {
            throw new ErrorException(MessageUtil.INSERT_NOT_SUCCESS);
        }
        // Nếu đã tồn tại quốc gia trong DB
        if (existCountryOp.isPresent()) {
            // Lấy thông tin quốc gia tồn tại đó
            Location existCountry = existCountryOp.get();
            // Gán thông tin quốc gia đã tồn tại vào địa điểm mới cùng với tên thành phố mới
            Location newLocation = Location.builder()
                    .countryCode(existCountry.getCountryCode())
                    .countryName(existCountry.getCountryName())
                    .longitude(existCountry.getLongitude())
                    .latitude(existCountry.getLatitude())
                    .cityId(existCountry.getCityId() + 1)
                    .cityName(locationRequestDTO.getCityName())
                    .build();
            // Lưu xuống DB
            newLocation = locationRepository.save(newLocation);
            // Gán thông tin quốc gia đã tồn tại vào địa điểm mới cùng với tên sân bay mới
            AirportGeo newAirportGeo = AirportGeo.builder()
                    .locationId(newLocation.getLocationId())
                    .airportName(locationRequestDTO.getAirportName())
                    .build();
            // Lưu xuống DB
            airportGeoRepository.save(newAirportGeo);
        // Chưa tồn tại quốc gia trong DB
        } else {
            // Lấy thông tin bản ghi cuối cùng của địa điểm
            Location lastCountry = locationRepository.findFirstByOrderByLocationIdDesc();
            // Tạo đối tượng mới để lưu xuống DB
            Location newLocation;
            if (Objects.nonNull(lastCountry)) {
                newLocation = Location.builder()
                        .countryCode(lastCountry.getCountryCode() + 1)
                        .countryName(locationRequestDTO.getCountryName())
                        .longitude(lastCountry.getLongitude() + 1)
                        .latitude(lastCountry.getLatitude() + 1)
                        .cityId(lastCountry.getCityId() + 1)
                        .cityName(locationRequestDTO.getCityName())
                        .build();
            } else {
                newLocation = Location.builder()
                        .countryCode(1)
                        .countryName(locationRequestDTO.getCountryName())
                        .longitude(1L)
                        .latitude(1F)
                        .cityId(1)
                        .cityName(locationRequestDTO.getCityName())
                        .build();
            }

            // Lưu xuống DB
            newLocation = locationRepository.save(newLocation);
            // Tạo đối tượng mới để lưu xuống DB
            AirportGeo newAirportGeo = AirportGeo.builder()
                    .locationId(newLocation.getLocationId())
                    .airportName(locationRequestDTO.getAirportName())
                    .build();
            // Lưu xuống DB
            airportGeoRepository.save(newAirportGeo);
        }
        // Trả về thêm thành công
        return ResponseEntity.ok(Helper.createSuccessCommon(MessageUtil.INSERT_SUCCESS));
    }

    public ResponseCommon deleteLocation(Integer locationId) {
        if (checkLocationUsedInFlight(locationId)) {
            Location location = locationRepository.findById(locationId)
                    .orElseThrow(() -> new ErrorException(MessageUtil.LOCATION_NOT_FOUND));
            locationRepository.delete(location);
            return Helper.createSuccessCommon(MessageUtil.DELETE_LOCATION_SUCCESS);

        } else throw new ErrorException(MessageUtil.DELETE_LOCATION_NOT_SUCCESS);
    }

    // Hàm sửa địa điểm
    public ResponseEntity<?> editLocation(Integer locationId, LocationRequestDTO locationRequestDTO) {
        // Kiểm tra địa điểm thay đổi đã được sử dụng chuyến bay nào chưa
        boolean isLocationUsedInFlight = checkLocationUsedInFlight(locationId);
        // Đã sử dụng rồi sẽ trả ra lỗi
        if (!isLocationUsedInFlight) {
            throw new ErrorException(MessageUtil.UPDATED_EXCEPTION);
        }
        // Tìm kiếm thông tin địa điểm theo Id địa điểm, nếu không tồn tại sẽ trả ra lỗi
        Location updatedLocation = locationRepository.findById(locationId).orElseThrow(() -> {
            throw new ErrorException(MessageUtil.UPDATED_EXCEPTION);
        });
        // Gán thông tin mới vào đối tượng vừa tìm được để thay đổi
        updatedLocation.setCityName(locationRequestDTO.getCityName());
        updatedLocation.setCountryName(locationRequestDTO.getCountryName());
        // Gọi đến hàm thay đổi để lưu xuống DB
        locationRepository.save(updatedLocation);
        // Trả về thay đổi thành công
        return ResponseEntity.ok(Helper.createSuccessCommon(MessageUtil.UPDATED_SUCCESS));
    }

    // Hàm lấy địa điểm
    public ResponseEntity<?> getAllLocation(String filter) {
        // Khởi tạo danh sách chứa đia điểm lấy từ DB
        List<Location> locationList;
        // Kiểm tra nếu lọc theo tên danh sách sẽ lấy hết địa điểm
        if (Objects.isNull(filter)) {
            locationList = locationRepository.findAll();
        } else {
            // Lọc ra các địa điểm có tên giống với tên cần tìm
            locationList = locationRepository.findLocationsByCityNameContaining(filter);
        }
        // Kiểm tra danh sách tìm kiếm rỗng sẽ trả về lỗi
        if (CollectionUtils.isEmpty(locationList)) {
            throw new ErrorException(MessageUtil.LOCATION_NOT_FOUND);
        }
        // Gán giá trị để trả về
        Map<String, List<LocationResponseDTO.LocationDTO>> map = new HashMap<>();
        locationList.forEach(i -> {
            map.putIfAbsent(i.getCountryName(), new ArrayList<>());
            if (map.containsKey(i.getCountryName())) {
                map.get(i.getCountryName()).add(new LocationResponseDTO.LocationDTO(i.getLocationId(), i.getCityName()));
            }
        });
        List<LocationResponseDTO> responseDTOList = map.keySet().stream()
                .map(i -> new LocationResponseDTO(i, map.get(i)))
                .collect(Collectors.toList());
        // Trả về thành công
        return ResponseEntity.ok(Helper.createSuccessListCommon(new ArrayList<>(responseDTOList)));
    }
}

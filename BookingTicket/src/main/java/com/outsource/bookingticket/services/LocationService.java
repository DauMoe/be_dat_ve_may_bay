package com.outsource.bookingticket.services;

import com.outsource.bookingticket.dtos.LocationRequestDTO;
import com.outsource.bookingticket.entities.airport.AirportGeo;
import com.outsource.bookingticket.entities.location.Location;
import com.outsource.bookingticket.exception.ErrorException;
import com.outsource.bookingticket.utils.Helper;
import com.outsource.bookingticket.utils.MessageUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class LocationService extends BaseService {

    public ResponseEntity<?> addLocation(LocationRequestDTO locationRequestDTO) {
        // Tìm kiếm thông tin quốc gia đã tồn tại ở bản ghi khác dưới DB chưa
        Optional<Location> existCountryOp =
                locationRepository.findLocationByCountryName(locationRequestDTO.getCountryName());
        if (!StringUtils.hasLength(locationRequestDTO.getCityName()) ||
                !StringUtils.hasLength(locationRequestDTO.getCountryName()) ||
                !StringUtils.hasLength(locationRequestDTO.getAirportName())) {
            throw new ErrorException(MessageUtil.INSERT_NOT_SUCCESS);
        }
        if (existCountryOp.isPresent()) {
            Location existCountry = existCountryOp.get();
            Location newLocation = Location.builder()
                    .countryCode(existCountry.getCountryCode())
                    .countryName(existCountry.getCountryName())
                    .longitude(existCountry.getLongitude())
                    .latitude(existCountry.getLatitude())
                    .cityId(existCountry.getCityId() + 1)
                    .cityName(locationRequestDTO.getCityName())
                    .build();

            newLocation = locationRepository.save(newLocation);

            AirportGeo newAirportGeo = AirportGeo.builder()
                    .locationId(newLocation.getLocationId())
                    .airportName(locationRequestDTO.getAirportName())
                    .build();

            airportGeoRepository.save(newAirportGeo);
        } else {
            Location lastCountry = locationRepository.findLocationByOrderByLocationIdDesc();
            Location newLocation = Location.builder()
                    .countryCode(lastCountry.getCountryCode() + 1)
                    .countryName(locationRequestDTO.getCountryName())
                    .longitude(lastCountry.getLongitude() + 1)
                    .latitude(lastCountry.getLatitude() + 1)
                    .cityId(lastCountry.getCityId() + 1)
                    .cityName(locationRequestDTO.getCityName())
                    .build();

            newLocation = locationRepository.save(newLocation);

            AirportGeo newAirportGeo = AirportGeo.builder()
                    .locationId(newLocation.getLocationId())
                    .airportName(locationRequestDTO.getAirportName())
                    .build();

            airportGeoRepository.save(newAirportGeo);
        }

        return ResponseEntity.ok(Helper.createSuccessCommon(MessageUtil.INSERT_SUCCESS));
    }

}

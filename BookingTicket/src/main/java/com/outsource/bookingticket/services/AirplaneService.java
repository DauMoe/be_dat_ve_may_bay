package com.outsource.bookingticket.services;

import com.outsource.bookingticket.dtos.AirplaneDTO;
import com.outsource.bookingticket.entities.airplane.Airplane;
import com.outsource.bookingticket.exception.ErrorException;
import com.outsource.bookingticket.utils.Helper;
import com.outsource.bookingticket.utils.MessageUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AirplaneService extends BaseService {

    // API thêm máy bay
    public ResponseEntity<?> addAirplane(AirplaneDTO airplaneDTO) {
        Optional<Airplane> existAirplane =
                airplaneRepository.findByAirplaneNameAndBrand(airplaneDTO.getAirplaneName(), airplaneDTO.getBrand());
        if (existAirplane.isPresent()) {
            throw new ErrorException(MessageUtil.AIRPLANE_IS_EXIST);
        }

        Airplane saved = Airplane.builder()
                .airplaneName(airplaneDTO.getAirplaneName())
                .capacity(airplaneDTO.getCapacity())
                .brand(airplaneDTO.getBrand())
                .linkImgBrand(airplaneDTO.getLinkImgBrand())
                .build();
        airplaneRepository.save(saved);
        return ResponseEntity.ok(Helper.createSuccessCommon(MessageUtil.INSERT_SUCCESS));
    }

    // Lấy hết danh sách máy bay
    public ResponseEntity<?> getAllAirplane() {
        List<Airplane> airplaneList = airplaneRepository.findAll();

        if (CollectionUtils.isEmpty(airplaneList)) {
            throw new ErrorException(MessageUtil.AIRPLANE_IS_EMPTY);
        }

        List<AirplaneDTO> resultList = airplaneList
                .stream()
                .map(this::mapToAirplaneDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Helper.createSuccessListCommon(new ArrayList<>(resultList)));
    }

    private AirplaneDTO mapToAirplaneDTO(Airplane airplane) {
        return AirplaneDTO.builder()
                .airplaneId(airplane.getAirplaneId())
                .airplaneName(airplane.getAirplaneName())
                .capacity(airplane.getCapacity())
                .brand(airplane.getBrand())
                .linkImgBrand(airplane.getLinkImgBrand())
                .build();
    }
}

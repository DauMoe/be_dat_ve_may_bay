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
        // Kiểm tra xem đã tồn tại máy bay này chưa
        Optional<Airplane> existAirplane =
                airplaneRepository.findByAirplaneNameAndBrand(airplaneDTO.getAirplaneName(), airplaneDTO.getBrand());
        // Nếu đã tồn tại, trả ra lỗi như bên dưới
        if (existAirplane.isPresent()) {
            throw new ErrorException(MessageUtil.AIRPLANE_IS_EXIST);
        }

        // Khởi tạo đối tượng để lưu vào DB
        Airplane saved = Airplane.builder()
                .airplaneName(airplaneDTO.getAirplaneName())
                .capacity(airplaneDTO.getCapacity())
                .brand(airplaneDTO.getBrand())
                .linkImgBrand(airplaneDTO.getLinkImgBrand())
                .build();
        // Lưu máy bay
        airplaneRepository.save(saved);

        // Trả về thông báo thêm thành công
        return ResponseEntity.ok(Helper.createSuccessCommon(MessageUtil.INSERT_SUCCESS));
    }

    // Lấy hết danh sách máy bay
    public ResponseEntity<?> getAllAirplane() {
        // Lấy hết danh sách máy bay
        List<Airplane> airplaneList = airplaneRepository.findAll();

        // Kiểm tra nếu danh sách rỗng sẽ trả về thông báo
        if (CollectionUtils.isEmpty(airplaneList)) {
            throw new ErrorException(MessageUtil.AIRPLANE_IS_EMPTY);
        }

        // Gán danh sách vừa tìm được để trả về hiển thị
        List<AirplaneDTO> resultList = airplaneList
                .stream()
                .map(this::mapToAirplaneDTO)
                .collect(Collectors.toList());

        // Trả về hiển thị
        return ResponseEntity.ok(Helper.createSuccessListCommon(new ArrayList<>(resultList)));
    }
}

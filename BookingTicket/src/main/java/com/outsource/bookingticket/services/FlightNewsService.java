package com.outsource.bookingticket.services;

import com.outsource.bookingticket.constants.Constants;
import com.outsource.bookingticket.entities.flight_news.FlightNews;
import com.outsource.bookingticket.exception.ErrorException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class FlightNewsService extends BaseService {

    public List<FlightNews> listAll() {
        return (List<FlightNews>) flightNewsRepository.findAll();
    }

    public Page<FlightNews> listByPage(int pageNum, String sortField, String sortDir,
                                    String keyword) {
        Sort sort = Sort.by(sortField);

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, Constants.FLIGHT_NEWS_PER_PAGE, sort);

//        if (keyword != null && !keyword.isEmpty()) { // Search by filter, need check empty
//            return flightNewsRepository.findAll(keyword, pageable);
//        }
        return flightNewsRepository.findAll(pageable);
    }

    public FlightNews saveFlightNews(FlightNews flightNews) {
        return flightNewsRepository.save(flightNews);
    }

    public FlightNews get(Integer id) throws ErrorException {
        try {
            return flightNewsRepository.findById(id).get();
        } catch (NoSuchElementException ex) {
            throw new ErrorException("Counld not find FlightNews with id: " + id);
        }
    }

    public void deleteFlightNewsById(Integer id) throws ErrorException {
        Long count = flightNewsRepository.countByFlightNewsId(id);

        if (count == 0 || count == null) {
            throw new ErrorException("Counld not found FlightNews with id: " + id);
        }

        flightNewsRepository.deleteById(id);
    }
}

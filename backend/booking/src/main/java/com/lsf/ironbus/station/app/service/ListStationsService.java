package com.lsf.ironbus.station.app.service;

import com.lsf.ironbus.station.repository.StationRepository;
import com.lsf.ironbus.station.app.response.StationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListStationsService {

    private final StationRepository stationRepository;

    @Transactional(readOnly = true)
    public List<StationResponse> listActive() {
        return stationRepository
                .findAllByActiveTrueOrderByNameAsc()
                .stream()
                .map(StationResponse::from)
                .toList();
    }
}
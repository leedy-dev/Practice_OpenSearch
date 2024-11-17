package com.dylabo.opensearch.domain.taxitrip.controller;

import com.dylabo.opensearch.domain.taxitrip.service.FHVTripRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fhv-trip-record")
public class FHVTripRecordController {

    private final FHVTripRecordService fhvTripRecordService;

    @PostMapping("/sync-opensearch")
    public String syncDataToOpenSearch() {
        fhvTripRecordService.syncAllRecordsToOpenSearch();
        return "Data synchronization started.";
    }

}

package com.dylabo.opensearch.domain.opensearch.service;

import com.dylabo.opensearch.domain.taxitrip.entity.FHVTripRecord;

import java.util.List;

public interface OpenSearchService {

    void indexFHVTripRecord(FHVTripRecord record) throws Exception;

    void indexFHVTripRecordsInBulk(List<FHVTripRecord> records) throws Exception;

    List<FHVTripRecord> searchFHVTripRecords(String query) throws Exception;

}

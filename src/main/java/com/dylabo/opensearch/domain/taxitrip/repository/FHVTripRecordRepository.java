package com.dylabo.opensearch.domain.taxitrip.repository;

import com.dylabo.opensearch.domain.taxitrip.entity.FHVTripRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FHVTripRecordRepository extends JpaRepository<FHVTripRecord, Long> {

}

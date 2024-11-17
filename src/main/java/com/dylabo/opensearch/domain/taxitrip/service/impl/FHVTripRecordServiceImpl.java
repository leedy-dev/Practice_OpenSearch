package com.dylabo.opensearch.domain.taxitrip.service.impl;

import com.dylabo.opensearch.domain.opensearch.service.OpenSearchService;
import com.dylabo.opensearch.domain.taxitrip.entity.FHVTripRecord;
import com.dylabo.opensearch.domain.taxitrip.repository.FHVTripRecordRepository;
import com.dylabo.opensearch.domain.taxitrip.service.FHVTripRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FHVTripRecordServiceImpl implements FHVTripRecordService {

    private static final int THREAD_COUNT = 4;
    public static final int BATCH_SIZE = 1000;

    private final FHVTripRecordRepository fhvTripRecordRepository;
    private final OpenSearchService openSearchService;

    private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT, r -> {
        Thread thread = new Thread(r);
        thread.setDaemon(true); // 데몬 스레드 설정
        return thread;
    });

    @Override
    public void syncAllRecordsToOpenSearch() {
        long totalElements = fhvTripRecordRepository.count();
        int totalPages = (int) Math.ceil((double) totalElements / BATCH_SIZE);

        for (int page = 0; page < totalPages; page++) {
            final int currentPage = page;
            executorService.submit(() -> {
                Page<FHVTripRecord> recordPage = fhvTripRecordRepository.findAll(PageRequest.of(currentPage, BATCH_SIZE));
                try {
                    openSearchService.indexFHVTripRecordsInBulk(recordPage.getContent());
                    log.info("Synced batch " + currentPage + " of " + totalPages);
                } catch (Exception e) {
                    log.error("Error syncing batch " + currentPage + ": " + e.getMessage());
                    e.printStackTrace();
                }
            });
        }

        executorService.shutdown();

//        int page = 0;
//
//        while (true) {
//            Page<FHVTripRecord> recordPage = fhvTripRecordRepository.findAll(PageRequest.of(page, BATCH_SIZE));
//
//            if (recordPage.isEmpty()) {
//                break;
//            }
//
//            try {
//                openSearchService.indexFHVTripRecordsInBulk(recordPage.getContent());
//                log.info("Synced batch " + page);
//            } catch (Exception e) {
//                log.error("Error syncing batch " + page + ": " + e.getMessage());
//            }
//
//            page++;
//        }
    }

}

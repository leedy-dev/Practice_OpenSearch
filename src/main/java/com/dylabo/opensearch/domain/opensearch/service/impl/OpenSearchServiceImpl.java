package com.dylabo.opensearch.domain.opensearch.service.impl;

import com.dylabo.opensearch.domain.opensearch.service.OpenSearchService;
import com.dylabo.opensearch.domain.taxitrip.entity.FHVTripRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch.core.*;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenSearchServiceImpl implements OpenSearchService {

    private final OpenSearchClient openSearchClient;

    @Override
    public void indexFHVTripRecord(FHVTripRecord record) throws Exception {
        // OpenSearch 인덱싱 요청
        IndexRequest<Object> request = new IndexRequest.Builder<>()
                .index("records") // OpenSearch 인덱스 이름
                .id(record.getId().toString())
                .document(record)
                .build();

        openSearchClient.index(request);

    }

    @Override
    public void indexFHVTripRecordsInBulk(List<FHVTripRecord> records) throws Exception {
        BulkRequest.Builder bulkRequest = new BulkRequest.Builder();

        for (FHVTripRecord record : records) {
            bulkRequest.operations(op -> op
                    .index(idx -> idx
                            .index("products") // OpenSearch 인덱스 이름
                            .id(record.getId().toString())
                            .document(record)
                    )
            );
        }

        BulkResponse response = openSearchClient.bulk(bulkRequest.build());
        if (response.errors()) {
            System.err.println("Bulk indexing errors occurred.");
        }
    }

    @Override
    public List<FHVTripRecord> searchFHVTripRecords(String query) throws Exception {
        // OpenSearch 검색 요청
        SearchRequest request = new SearchRequest.Builder()
                .index("records")
                .query(q -> q
                        .match(m -> m
                                .field("name")
                                .query(FieldValue.of(query))
                        )
                )
                .build();

        SearchResponse<FHVTripRecord> response = openSearchClient.search(request, FHVTripRecord.class);

        // 검색 결과 반환
        return response.hits().hits().stream()
                .map(Hit::source)
                .toList();
    }

}

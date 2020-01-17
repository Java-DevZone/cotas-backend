package com.javadevzone.cotas.repository;

import com.javadevzone.cotas.entity.Asset;
import com.javadevzone.cotas.entity.AssetHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AssetHistoryRepository extends CrudRepository<AssetHistory, Long> {

    AssetHistory findByAssetAndDateTime(Asset asset, LocalDateTime dateTime);

}

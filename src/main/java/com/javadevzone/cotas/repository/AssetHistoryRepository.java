package com.javadevzone.cotas.repository;

import com.javadevzone.cotas.entity.Asset;
import com.javadevzone.cotas.entity.AssetHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssetHistoryRepository extends CrudRepository<AssetHistory, Long> {

    AssetHistory findByAssetAndDateTime(Asset asset, LocalDate dateTime);

    Optional<List<AssetHistory>> findAllByAssetAndDateTimeAfterOrderByDateTimeAsc(Asset asset, LocalDate dateTime);

}

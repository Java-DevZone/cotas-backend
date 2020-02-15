package com.javadevzone.cotas.repository;

import com.javadevzone.cotas.entity.Asset;
import com.javadevzone.cotas.entity.AssetHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssetHistoryRepository extends JpaRepository<AssetHistory, Long>, CrudRepository<AssetHistory, Long> {

    Optional<AssetHistory> findByAssetAndDate(Asset asset, LocalDate date);

    Optional<AssetHistory> findFirstByAssetOrderByDateDesc(Asset asset);

    Optional<AssetHistory> findFirstByAssetAndDate(Asset asset, LocalDate date);

    Optional<List<AssetHistory>> findAllByAssetAndDateAfterOrderByDateAsc(Asset asset, LocalDate dateTime);

    Optional<AssetHistory> findFirstByAssetAndDateOrderByDateDesc(Asset asset, LocalDate date);
}

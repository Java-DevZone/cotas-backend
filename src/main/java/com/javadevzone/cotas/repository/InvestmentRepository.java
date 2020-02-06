package com.javadevzone.cotas.repository;

import com.javadevzone.cotas.entity.Asset;
import com.javadevzone.cotas.entity.Investment;
import com.javadevzone.cotas.entity.Wallet;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvestmentRepository extends CrudRepository<Investment, Long> {

    Optional<List<Investment>> findAllByAssetOrderByDateAsc(Asset asset);

    List<Investment> findAllByWalletOrderByDateAsc(Wallet wallet);

    List<Investment> findAllByWalletAndDate(Wallet wallet, LocalDate date);

    List<Investment> findAllByWalletAndDateBefore(Wallet wallet, LocalDate date);

    @Query("select sum(i.quantity) from Investment i where i.wallet = :wallet and i.asset = :asset and i.date <= :date")
    Optional<Long> getQuantityByWalletAndAssetAndDateBefore(Wallet wallet, Asset asset, LocalDate date);

    @Query("select sum(i.quantity) from Investment i where i.wallet = :wallet and i.asset = :asset")
    Optional<Long> getQuantityByWalletAndAssetAndDateBefore(Wallet wallet, Asset asset);

}

package com.javadevzone.cotas.repository;

import com.javadevzone.cotas.entity.Asset;
import com.javadevzone.cotas.entity.Investment;
import com.javadevzone.cotas.entity.Wallet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvestmentRepository extends CrudRepository<Investment, Long> {

    Optional<List<Investment>> findAllByAssetOrderByDateAsc(Asset asset);

    List<Investment> findAllByWallet(Wallet wallet);
}

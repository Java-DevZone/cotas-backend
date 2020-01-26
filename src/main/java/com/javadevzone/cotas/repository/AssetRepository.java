package com.javadevzone.cotas.repository;

import com.javadevzone.cotas.entity.Asset;
import com.javadevzone.cotas.entity.Wallet;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends CrudRepository<Asset, String> {

    @Query("select distinct asset from Wallet wallet " +
            "join wallet.investments investment " +
            "join investment.asset asset " +
            "where wallet = :wallet")
    Optional<List<Asset>> findAssetsByWallet(Wallet wallet);

}

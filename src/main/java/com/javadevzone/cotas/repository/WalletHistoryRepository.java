package com.javadevzone.cotas.repository;

import com.javadevzone.cotas.entity.Wallet;
import com.javadevzone.cotas.entity.WalletHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface WalletHistoryRepository extends CrudRepository<WalletHistory, Wallet> {

    Optional<WalletHistory> findFirstByWalletAndRegisterDateIsBeforeOrderByRegisterDateDesc(Wallet wallet, LocalDate registerDate);

}

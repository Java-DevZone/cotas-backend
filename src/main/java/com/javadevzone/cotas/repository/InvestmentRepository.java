package com.javadevzone.cotas.repository;

import com.javadevzone.cotas.entity.Investment;
import com.javadevzone.cotas.entity.Wallet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestmentRepository extends CrudRepository<Investment, Long> {

    List<Investment> findAllByWallet(Wallet wallet);
}

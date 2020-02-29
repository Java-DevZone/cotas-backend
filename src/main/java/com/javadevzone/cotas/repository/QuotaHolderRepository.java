package com.javadevzone.cotas.repository;

import com.javadevzone.cotas.entity.QuotaHolder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuotaHolderRepository extends CrudRepository<QuotaHolder, Long> {

}

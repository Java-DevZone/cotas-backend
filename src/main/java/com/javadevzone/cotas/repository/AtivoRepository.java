package com.javadevzone.cotas.repository;

import com.javadevzone.cotas.entity.Asset;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AtivoRepository extends CrudRepository<Asset, String> {

}

package com.javadevzone.cotas.repository;

import com.javadevzone.cotas.entity.Ativo;
import com.javadevzone.cotas.entity.Fechamento;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface AtivoRepository extends CrudRepository<Ativo, String> {

}

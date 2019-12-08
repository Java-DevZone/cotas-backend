package com.javadevzone.cotas.repository;

import com.javadevzone.cotas.entity.Ativo;
import com.javadevzone.cotas.entity.Fechamento;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;

public interface FechamentoRepository extends CrudRepository<Fechamento, Long> {

    Fechamento findByTicket(Ativo ticket, LocalDate data);

}

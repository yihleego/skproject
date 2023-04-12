package io.leego.ah.openapi.repository;

import io.leego.ah.openapi.entity.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Leego Yih
 */
public interface ExchangeRepository extends JpaRepository<Exchange, Long>, QuerydslRepository<Exchange> {

    Optional<Exchange> findFirstByOrderByIdDesc();

}

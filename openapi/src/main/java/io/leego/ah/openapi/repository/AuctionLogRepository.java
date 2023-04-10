package io.leego.ah.openapi.repository;

import io.leego.ah.openapi.entity.AuctionLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Leego Yih
 */
public interface AuctionLogRepository extends JpaRepository<AuctionLog, Long>, QuerydslRepository<AuctionLog> {
}

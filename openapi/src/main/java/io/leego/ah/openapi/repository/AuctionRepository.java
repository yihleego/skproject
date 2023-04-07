package io.leego.ah.openapi.repository;

import io.leego.ah.openapi.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * @author Leego Yih
 */
public interface AuctionRepository extends JpaRepository<Auction, Long>, QuerydslRepository<Auction> {

    List<Auction> findByIdNotInAndTimeLeftIn(List<Long> ids, List<String> timeLefts);

    @Transactional
    @Modifying
    @Query("update Auction set timeLeft = ?2, updatedTime = ?3 where id in ?1")
    int updateLeftTime(List<Long> ids, String timeLeft, Instant updatedTime);

    @Transactional
    @Modifying
    @Query("update Auction set updatedTime = ?2 where id in ?1")
    int updateUpdatedTime(List<Long> ids, Instant updatedTime);

}

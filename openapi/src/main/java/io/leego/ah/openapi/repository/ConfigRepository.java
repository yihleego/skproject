package io.leego.ah.openapi.repository;

import io.leego.ah.openapi.entity.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * @author Leego Yih
 */
public interface ConfigRepository extends JpaRepository<Config, Long>, QuerydslRepository<Config> {

    Optional<Config> findByKey(String key);

    @Query("select version from Config where key in ?1")
    Integer findVersionByKey(String key);

}

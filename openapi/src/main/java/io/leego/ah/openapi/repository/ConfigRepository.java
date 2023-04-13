package io.leego.ah.openapi.repository;

import io.leego.ah.openapi.entity.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * @author Leego Yih
 */
public interface ConfigRepository extends JpaRepository<Config, Long>, QuerydslRepository<Config> {

    List<Config> findByGroup(String group);

    Optional<Config> findByGroupAndKey(String group, String key);

    @Query("select version from Config where group in ?1 and key in ?2")
    Optional<Integer> findVersionByGroupAndKey(String group, String key);

}

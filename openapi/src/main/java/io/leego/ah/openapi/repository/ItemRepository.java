package io.leego.ah.openapi.repository;

import io.leego.ah.openapi.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Leego Yih
 */
public interface ItemRepository extends JpaRepository<Item, String>, QuerydslRepository<Item> {
}

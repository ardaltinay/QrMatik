package com.qrmatik.server.repository;

import com.qrmatik.server.model.OrderItemEntity;
import com.qrmatik.server.model.OrderStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, UUID> {

    // Returns rows of [menuItemId (UUID), totalQty (Long)] ordered by totalQty desc
    @Query("select li.menuItem.id as id, sum(coalesce(li.quantity,0)) as qty " + "from OrderItemEntity li "
            + "where (:tenant is null or (li.order.tenant is not null and li.order.tenant.code = :tenant)) "
            + "and (li.order.status <> :canceled) " + "group by li.menuItem.id order by qty desc")
    List<Object[]> topMenuItemCounts(@Param("tenant") String tenant, @Param("canceled") OrderStatus canceled,
            Pageable pageable);
}

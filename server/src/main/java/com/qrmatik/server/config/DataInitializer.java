package com.qrmatik.server.config;

import com.qrmatik.server.model.MenuItemEntity;
import com.qrmatik.server.model.TableEntity;
import com.qrmatik.server.model.TableStatus;
import com.qrmatik.server.model.TenantEntity;
import com.qrmatik.server.model.UserEntity;
import com.qrmatik.server.model.UserRole;
import com.qrmatik.server.repository.MenuItemRepository;
import com.qrmatik.server.repository.TableRepository;
import com.qrmatik.server.repository.TenantRepository;
import com.qrmatik.server.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MenuItemRepository menuRepository;
    private final TableRepository tableRepository;
    private final TenantRepository tenantRepository;
    private final JdbcTemplate jdbcTemplate;

    public DataInitializer(UserRepository userRepository, MenuItemRepository menuRepository,
            TableRepository tableRepository, TenantRepository tenantRepository, JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.menuRepository = menuRepository;
        this.tableRepository = tableRepository;
        this.tenantRepository = tenantRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        tryNormalizeVersions();
        tryDeduplicateUsers();
        tryMigrateUserUniqueConstraint();
        tryDropLegacyColumns();
        ensureUserPasswords();
        if (userRepository.count() == 0) {
            TenantEntity t = tenantRepository.findByCode("default")
                    .orElseGet(() -> tenantRepository.save(TenantEntity.builder().code("default").name("Demo Tenant")
                            .logoUrl("").primaryColor("#0f172a").accentColor("#6366f1").build()));
            BCryptPasswordEncoder pe = new BCryptPasswordEncoder();
            userRepository.save(UserEntity.builder().username("admin").role(UserRole.ADMIN).tenant(t)
                    .passwordHash(pe.encode("admin123")).build());
            userRepository.save(UserEntity.builder().username("kitchen").role(UserRole.KITCHEN).tenant(t)
                    .passwordHash(pe.encode("kitchen123")).build());
            userRepository.save(UserEntity.builder().username("bar").role(UserRole.BAR).tenant(t)
                    .passwordHash(pe.encode("bar123")).build());
            userRepository.save(UserEntity.builder().username("test").role(UserRole.STAFF).tenant(t)
                    .passwordHash(pe.encode("test123")).build());
            userRepository.save(UserEntity.builder().username("super").role(UserRole.SUPERADMIN).tenant(t)
                    .passwordHash(pe.encode("super123")).build());
        }

        if (menuRepository.count() == 0) {
            TenantEntity tenant = tenantRepository.findByCode("default").orElse(null);
            menuRepository
                    .save(MenuItemEntity.builder().name("Margherita").price(BigDecimal.valueOf(45.0)).category("food")
                            .subcategory("pizza").image("https://picsum.photos/seed/1/400/240").tenant(tenant).build());
            menuRepository
                    .save(MenuItemEntity.builder().name("Pepperoni").price(BigDecimal.valueOf(55.0)).category("food")
                            .subcategory("pizza").image("https://picsum.photos/seed/2/400/240").tenant(tenant).build());
            menuRepository
                    .save(MenuItemEntity.builder().name("Caesar Salad").price(BigDecimal.valueOf(30.0)).category("food")
                            .subcategory("salad").image("https://picsum.photos/seed/3/400/240").tenant(tenant).build());
            menuRepository
                    .save(MenuItemEntity.builder().name("Coca-Cola").price(BigDecimal.valueOf(10.0)).category("drink")
                            .subcategory("soda").image("https://picsum.photos/seed/4/400/240").tenant(tenant).build());
            menuRepository
                    .save(MenuItemEntity.builder().name("Red Wine").price(BigDecimal.valueOf(80.0)).category("drink")
                            .subcategory("wine").image("https://picsum.photos/seed/5/400/240").tenant(tenant).build());
            menuRepository.save(MenuItemEntity.builder().name("Tiramisu").price(BigDecimal.valueOf(35.0))
                    .category("food").subcategory("dessert").image("https://picsum.photos/seed/6/400/240")
                    .tenant(tenant).build());
        }
        if (tableRepository.count() == 0) {
            TenantEntity tenant = tenantRepository.findByCode("default").orElse(null);
            tableRepository.save(TableEntity.builder().code("A1").description("Masa A1").tenant(tenant)
                    .status(TableStatus.AVAILABLE).build());
            tableRepository.save(TableEntity.builder().code("B3").description("Masa B3").tenant(tenant)
                    .status(TableStatus.AVAILABLE).build());
            tableRepository.save(TableEntity.builder().code("Bar-01").description("Bar 1").tenant(tenant)
                    .status(TableStatus.AVAILABLE).build());
            tableRepository.save(TableEntity.builder().code("C2").description("Masa C2").tenant(tenant)
                    .status(TableStatus.AVAILABLE).build());
            tableRepository.save(TableEntity.builder().code("guest").description("Masa guest").tenant(tenant)
                    .status(TableStatus.AVAILABLE).build());
        }
        if (menuRepository.count() >= 0) {
        }
        try {
        } catch (Exception ignored) {
        }
    }

    private void tryDeduplicateUsers() {
        try {
            jdbcTemplate.update("DELETE u FROM users u "
                    + "JOIN (SELECT tenant_id, username, MAX(created_time) mx FROM users GROUP BY tenant_id, username) m "
                    + "ON u.tenant_id = m.tenant_id AND u.username = m.username " + "WHERE u.created_time < m.mx");
        } catch (Exception ignored) {
        }
    }

    private void tryMigrateUserUniqueConstraint() {
        try {
            var idxNames = jdbcTemplate.queryForList("SELECT DISTINCT INDEX_NAME FROM INFORMATION_SCHEMA.STATISTICS "
                    + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'users' "
                    + "AND COLUMN_NAME = 'username' AND NON_UNIQUE = 0", String.class);
            for (String idx : idxNames) {
                try {
                    jdbcTemplate.execute("ALTER TABLE users DROP INDEX `" + idx + "`");
                } catch (Exception ignored) {
                }
            }
            Integer existing = null;
            try {
                existing = jdbcTemplate
                        .queryForObject("SELECT COUNT(*) FROM (SELECT INDEX_NAME FROM INFORMATION_SCHEMA.STATISTICS "
                                + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='users' AND NON_UNIQUE=0 "
                                + "GROUP BY INDEX_NAME HAVING SUM(CASE WHEN COLUMN_NAME='tenant_id' THEN 1 ELSE 0 END)>0 "
                                + "AND SUM(CASE WHEN COLUMN_NAME='username' THEN 1 ELSE 0 END)>0) t", Integer.class);
            } catch (Exception ignored) {
            }
            if (existing == null || existing == 0) {
                try {
                    jdbcTemplate.execute(
                            "ALTER TABLE users ADD UNIQUE INDEX uk_users_tenant_username (tenant_id, username)");
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void tryNormalizeVersions() {
        String[] tables = new String[]{"menu_items", "orders", "users", "tenants", "tables"};
        for (String t : tables) {
            try {
                jdbcTemplate.update("UPDATE " + t + " SET version = 0 WHERE version IS NULL");
            } catch (Exception ignored) {
            }
        }
    }

    private void tryDropLegacyColumns() {
        try {
            jdbcTemplate.execute("ALTER TABLE menu_items DROP COLUMN primary_type");
        } catch (Exception ignored) {
        }
    }

    private void ensureUserPasswords() {
        try {
            var users = userRepository.findAll();
            if (users == null || users.isEmpty())
                return;
            BCryptPasswordEncoder pe = new BCryptPasswordEncoder();
            boolean anyChanged = false;
            for (UserEntity u : users) {
                if (u.getPasswordHash() == null || u.getPasswordHash().isBlank()) {
                    String pwd;
                    String uname = u.getUsername() != null ? u.getUsername() : "user";
                    switch (uname) {
                        case "admin" -> pwd = "admin123";
                        case "kitchen" -> pwd = "kitchen123";
                        case "bar" -> pwd = "bar123";
                        default -> pwd = "changeme123";
                    }
                    u.setPasswordHash(pe.encode(pwd));
                    anyChanged = true;
                }
            }
            if (anyChanged)
                userRepository.saveAll(users);
        } catch (Exception ignored) {
        }
    }
}

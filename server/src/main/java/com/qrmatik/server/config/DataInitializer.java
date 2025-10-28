package com.qrmatik.server.config;

import com.qrmatik.server.model.MenuItemEntity;
import com.qrmatik.server.model.TenantEntity;
import com.qrmatik.server.model.UserEntity;
import com.qrmatik.server.repository.MenuItemRepository;
import com.qrmatik.server.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MenuItemRepository menuRepository;
    private final com.qrmatik.server.repository.TableRepository tableRepository;
    private final com.qrmatik.server.repository.TenantRepository tenantRepository;
    private final JdbcTemplate jdbcTemplate;

    public DataInitializer(UserRepository userRepository, MenuItemRepository menuRepository, com.qrmatik.server.repository.TableRepository tableRepository, com.qrmatik.server.repository.TenantRepository tenantRepository, JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.menuRepository = menuRepository;
        this.tableRepository = tableRepository;
        this.tenantRepository = tenantRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        // On startup, normalize NULL version columns to 0 to avoid optimistic locking NPEs on legacy rows
        tryNormalizeVersions();
        // Drop legacy columns if exist (e.g., primary_type removed from entity)
        tryDropLegacyColumns();
        // Ensure existing users (from older seeds) have a password hash
        ensureUserPasswords();
        if (userRepository.count() == 0) {
            // create default tenant if missing and attach relations
            TenantEntity t = tenantRepository.findByCode("default").orElseGet(() ->
                    tenantRepository.save(TenantEntity.builder()
                            .code("default").name("Demo Tenant").logoUrl("")
                            .primaryColor("#0f172a").accentColor("#6366f1").build())
            );
            BCryptPasswordEncoder pe = new BCryptPasswordEncoder();
            userRepository.save(UserEntity.builder().username("admin").role("admin").tenant(t).passwordHash(pe.encode("admin123")).build());
            userRepository.save(UserEntity.builder().username("kitchen").role("kitchen").tenant(t).passwordHash(pe.encode("kitchen123")).build());
            userRepository.save(UserEntity.builder().username("bar").role("bar").tenant(t).passwordHash(pe.encode("bar123")).build());
        }

        if (menuRepository.count() == 0) {
            TenantEntity tenant = tenantRepository.findByCode("default").orElse(null);
            // Yeni model: category = food|drink, subcategory = pizza|salad|soda|wine|dessert
            menuRepository.save(MenuItemEntity.builder().name("Margherita").price(BigDecimal.valueOf(45.0)).category("food").subcategory("pizza").image("https://picsum.photos/seed/1/400/240").tenant(tenant).build());
            menuRepository.save(MenuItemEntity.builder().name("Pepperoni").price(BigDecimal.valueOf(55.0)).category("food").subcategory("pizza").image("https://picsum.photos/seed/2/400/240").tenant(tenant).build());
            menuRepository.save(MenuItemEntity.builder().name("Caesar Salad").price(BigDecimal.valueOf(30.0)).category("food").subcategory("salad").image("https://picsum.photos/seed/3/400/240").tenant(tenant).build());
            menuRepository.save(MenuItemEntity.builder().name("Coca-Cola").price(BigDecimal.valueOf(10.0)).category("drink").subcategory("soda").image("https://picsum.photos/seed/4/400/240").tenant(tenant).build());
            menuRepository.save(MenuItemEntity.builder().name("Red Wine").price(BigDecimal.valueOf(80.0)).category("drink").subcategory("wine").image("https://picsum.photos/seed/5/400/240").tenant(tenant).build());
            menuRepository.save(MenuItemEntity.builder().name("Tiramisu").price(BigDecimal.valueOf(35.0)).category("food").subcategory("dessert").image("https://picsum.photos/seed/6/400/240").tenant(tenant).build());
        }
        if (tableRepository.count() == 0) {
            TenantEntity tenant = tenantRepository.findByCode("default").orElse(null);
            tableRepository.save(com.qrmatik.server.model.TableEntity.builder().code("A1").description("Masa A1").tenant(tenant).status(com.qrmatik.server.model.TableStatus.AVAILABLE).build());
            tableRepository.save(com.qrmatik.server.model.TableEntity.builder().code("B3").description("Masa B3").tenant(tenant).status(com.qrmatik.server.model.TableStatus.AVAILABLE).build());
            tableRepository.save(com.qrmatik.server.model.TableEntity.builder().code("Bar-01").description("Bar 1").tenant(tenant).status(com.qrmatik.server.model.TableStatus.AVAILABLE).build());
            tableRepository.save(com.qrmatik.server.model.TableEntity.builder().code("C2").description("Masa C2").tenant(tenant).status(com.qrmatik.server.model.TableStatus.AVAILABLE).build());
            tableRepository.save(com.qrmatik.server.model.TableEntity.builder().code("quest").description("quest quest").tenant(tenant).status(com.qrmatik.server.model.TableStatus.AVAILABLE).build());
        }
        // seed some tables
        if (menuRepository.count() >= 0) {
            // Note: check table repo via constructor-supplied object
        }
        try {
            // use table repository if available via reflection to avoid strict ordering issues
            // but we can call directly (repositories are supplied in constructor)
        } catch (Exception ignored) {}
    }

    private void tryNormalizeVersions() {
        String[] tables = new String[] { "menu_items", "orders", "users", "tenants", "tables" };
        for (String t : tables) {
            try {
                jdbcTemplate.update("UPDATE " + t + " SET version = 0 WHERE version IS NULL");
            } catch (Exception ignored) { }
        }
    }

    // Eski kolonları kaldırmayı dene (ör. primary_type)
    private void tryDropLegacyColumns() {
        try {
            jdbcTemplate.execute("ALTER TABLE menu_items DROP COLUMN primary_type");
        } catch (Exception ignored) { }
    }

    private void ensureUserPasswords() {
        try {
            var users = userRepository.findAll();
            if (users == null || users.isEmpty()) return;
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
            if (anyChanged) userRepository.saveAll(users);
        } catch (Exception ignored) { }
    }
}

QrMatik - Server

This is a minimal Spring Boot backend skeleton for the QrMatik client demo.
It exposes a simple REST API for orders and uses Spring Data JPA with MySQL.

How to run

1. Install Java 17 (or later) and Maven.
2. Configure MySQL and update `src/main/resources/application.properties` with your DB credentials.
3. From the `server/` directory run:

    mvn spring-boot:run

Endpoints

- GET /api/orders - list orders
- GET /api/orders/{id} - get order
- POST /api/orders - create order
- PUT /api/orders/{id}/status - update status (body: {"status":"PREPARING"})

Plans and limits

- Tenants have a plan: FREE | STANDARD | PRO (field: `TenantEntity.plan`).
- Public signup defaults to FREE.
- Current enforced limits:
    - Menu items: FREE ≤ 50, STANDARD ≤ 500, PRO unlimited
    - Tables: FREE ≤ 10, STANDARD ≤ 50, PRO unlimited
- Enforcement occurs on create via `PlanGuard`; exceeding returns HTTP 402 with a clear message.
- Super admin can set/update plan via existing tenant admin endpoints:
    - POST /api/tenants (body can include `plan`)
    - PUT /api/tenants/{id} (body can include `plan`)

Inventory (Pro)

- Inventory management is available only on PRO plan.
- Entity fields:
    - `MenuItemEntity.stockEnabled` (Boolean)
    - `MenuItemEntity.stockQuantity` (Integer)
- Endpoints (tenant admin):
    - GET `/api/stock/items` → List menu items with stock fields (HTTP 402 if plan is not PRO)
    - PUT `/api/stock/items/{id}` (body: `{ stockEnabled: boolean, stockQuantity: number|null }`)
        → Updates only stock fields (HTTP 402 if plan is not PRO)
    - Public menu payloads also include these fields; client can choose to hide items with zero stock.

Custom domains (Pro)

- `TenantEntity.customDomain` alanı ile bir özel alan adı tanımlanabilir (yalnızca PRO plan).
- Aynı domain başka tenant'ta kullanılamaz.
- İsteklerde Host header'ı custom domain ile eşleşirse tenant otomatik çözülür (TenantFilter).
- DNS tarafında, custom domain'iniz ana domaininize CNAME ile yönlendirilmelidir.

Notes

- This skeleton uses Lombok; your IDE should have Lombok support enabled.
- For production you should harden CORS, security, and consider connection pooling and migrations (Flyway/Liquibase).
    - With `spring.jpa.hibernate.ddl-auto=update` (current default), schema changes are auto-applied in dev.
    - For managed DBs, add a migration for new columns: `menu_items.stock_enabled` (BOOLEAN) and `menu_items.stock_qty` (INT).

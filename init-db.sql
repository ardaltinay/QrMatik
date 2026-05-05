-- NOT: Docker Compose POSTGRES_DB ve POSTGRES_USER kullanıyorsanız veritabanı zaten otomatik oluşur.
-- Bu dosya manuel kurulumlar veya ek başlangıç ayarları için kullanılabilir.

-- Eğer veritabanı yoksa oluştur (Manuel kurulumlar için)
-- SELECT 'CREATE DATABASE feasymenu' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'feasymenu')\gexec

-- Şema bazlı veya extension bazlı ayarlar buraya eklenebilir
-- Örnek: UUID desteği için (Zaten Spring Boot Hibernate ile hallediyor ama performans için iyidir)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Üretim ortamında performans için bazı kritik index'ler (Hibernate bazen bunları atlayabilir)
-- Not: Tablolar oluştuktan sonra çalışması için uygulamayı bir kez çalıştırdıktan sonra burayı kullanmak daha iyidir.

-- Örnek: Tenant bazlı sorguları hızlandırmak için
-- CREATE INDEX IF NOT EXISTS idx_menu_items_tenant ON menu_items(tenant_id);
-- CREATE INDEX IF NOT EXISTS idx_orders_tenant ON orders(tenant_id);
-- CREATE INDEX IF NOT EXISTS idx_users_tenant ON users(tenant_id);

-- Başlangıç mesajı
DO $$
BEGIN
    RAISE NOTICE 'Feasymenu Veritabanı başlangıç ayarları tamamlandı.';
END $$;

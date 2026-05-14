-- FeasyMenu Database Schema
-- Generated for PostgreSQL with UUID support

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. Tenants Table
CREATE TABLE IF NOT EXISTS tenants (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    version BIGINT DEFAULT 0,
    created_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    code VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255),
    owner_name VARCHAR(255),
    owner_email VARCHAR(255),
    logo_url TEXT,
    primary_color VARCHAR(50),
    accent_color VARCHAR(50),
    welcome_message TEXT,
    font_family VARCHAR(255),
    plan VARCHAR(50) DEFAULT 'FREE',
    custom_domain VARCHAR(255),
    locale VARCHAR(20),
    time_zone VARCHAR(100),
    billing_period VARCHAR(20),
    plan_paid_until DATE,
    last_payment_id VARCHAR(255),
    last_payment_at TIMESTAMP WITH TIME ZONE,
    pending_plan VARCHAR(50),
    pending_billing_period VARCHAR(20),
    pending_effective_date DATE,
    active BOOLEAN DEFAULT TRUE
);

-- 2. Users Table
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    version BIGINT DEFAULT 0,
    created_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    username VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    tenant_id UUID REFERENCES tenants(id),
    password_hash VARCHAR(512),
    UNIQUE (tenant_id, username)
);

-- 3. Menu Items Table
CREATE TABLE IF NOT EXISTS menu_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    version BIGINT DEFAULT 0,
    created_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    name VARCHAR(255) NOT NULL,
    name_en VARCHAR(255),
    description TEXT,
    description_en TEXT,
    price DECIMAL(19, 2),
    price_usd DECIMAL(19, 2),
    category VARCHAR(255),
    sub VARCHAR(255),
    image TEXT,
    tenant_id UUID REFERENCES tenants(id),
    stock_enabled BOOLEAN DEFAULT FALSE,
    stock_qty INTEGER
);

-- 4. Tables (Restaurant Tables) Table
CREATE TABLE IF NOT EXISTS tables (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    version BIGINT DEFAULT 0,
    created_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    code VARCHAR(100) NOT NULL,
    description TEXT,
    capacity INTEGER,
    status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE',
    tenant_id UUID REFERENCES tenants(id),
    UNIQUE (tenant_id, code)
);

-- 5. Orders Table
CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    version BIGINT DEFAULT 0,
    created_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    session_id VARCHAR(255),
    customer_name VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    total DECIMAL(19, 2),
    session_expires_at TIMESTAMP WITH TIME ZONE,
    tenant_id UUID REFERENCES tenants(id),
    table_id UUID REFERENCES tables(id),
    inventory_applied BOOLEAN DEFAULT FALSE
);

-- 6. Order Items Table
CREATE TABLE IF NOT EXISTS order_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    version BIGINT DEFAULT 0,
    created_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    order_id UUID REFERENCES orders(id) ON DELETE CASCADE,
    menu_item_id UUID REFERENCES menu_items(id),
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(19, 2) NOT NULL,
    name_snapshot VARCHAR(255),
    image_snapshot TEXT,
    category_snapshot VARCHAR(255),
    subcategory_snapshot VARCHAR(255),
    note TEXT
);

-- 7. Refresh Tokens Table
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    version BIGINT DEFAULT 0,
    created_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    token VARCHAR(512) NOT NULL UNIQUE,
    username VARCHAR(255) NOT NULL,
    expiry_date TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked BOOLEAN DEFAULT FALSE
);

-- 8. Blog Posts Table
CREATE TABLE IF NOT EXISTS blog_posts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    version BIGINT DEFAULT 0,
    created_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    slug_tr VARCHAR(255) NOT NULL UNIQUE,
    slug_en VARCHAR(255) NOT NULL UNIQUE,
    title_tr VARCHAR(255) NOT NULL,
    title_en VARCHAR(255),
    excerpt_tr TEXT,
    excerpt_en TEXT,
    content_tr TEXT,
    content_en TEXT
);

-- Essential Performance Indexes
CREATE INDEX IF NOT EXISTS idx_users_tenant ON users(tenant_id);
CREATE INDEX IF NOT EXISTS idx_menu_items_tenant ON menu_items(tenant_id);
CREATE INDEX IF NOT EXISTS idx_tables_tenant ON tables(tenant_id);
CREATE INDEX IF NOT EXISTS idx_orders_tenant ON orders(tenant_id);
CREATE INDEX IF NOT EXISTS idx_order_items_order ON order_items(order_id);

-- Advanced Optimization Indexes
-- Orders: Frequent status filtering for kitchen/bar/cashier screens
CREATE INDEX IF NOT EXISTS idx_orders_status_tenant ON orders(tenant_id, status);
-- Orders: Sorting by creation time (Recent orders first)
CREATE INDEX IF NOT EXISTS idx_orders_created_at ON orders(created_time DESC);
-- Orders: Lookup by customer session
CREATE INDEX IF NOT EXISTS idx_orders_session_id ON orders(session_id);
-- Orders: Foreign key to table
CREATE INDEX IF NOT EXISTS idx_orders_table_id ON orders(table_id);

-- Menu Items: Category and Subcategory filtering for menu display
CREATE INDEX IF NOT EXISTS idx_menu_items_cat_sub_tenant ON menu_items(tenant_id, category, sub);

-- Refresh Tokens: Token cleanup and validation
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_username ON refresh_tokens(username);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_expiry ON refresh_tokens(expiry_date);

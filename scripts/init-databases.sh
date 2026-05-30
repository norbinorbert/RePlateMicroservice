#!/bin/bash

# Database initialization script
# This creates the three separate databases for each microservice

set -e

MYSQL_HOST="${MYSQL_HOST:-mysql-service}"
MYSQL_PORT="${MYSQL_PORT:-3306}"
MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD:-root}"
MYSQL_USER="${MYSQL_USER:-replate_user}"
MYSQL_PASSWORD="${MYSQL_PASSWORD:-replate_password}"

echo "🗄️  Initializing RePlate databases..."
echo "Host: $MYSQL_HOST"
echo "User: $MYSQL_USER"

# Create databases and grant permissions
mysql -h "$MYSQL_HOST" -u root -p "$MYSQL_ROOT_PASSWORD" <<EOF
-- Create auth database
CREATE DATABASE IF NOT EXISTS replate_auth CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create listing database
CREATE DATABASE IF NOT EXISTS replate_listings CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create Filter database
CREATE DATABASE IF NOT EXISTS replate_filters CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Grant permissions
GRANT ALL PRIVILEGES ON replate_auth.* TO '${MYSQL_USER}'@'%' IDENTIFIED BY '${MYSQL_PASSWORD}';
GRANT ALL PRIVILEGES ON replate_listings.* TO '${MYSQL_USER}'@'%' IDENTIFIED BY '${MYSQL_PASSWORD}';
GRANT ALL PRIVILEGES ON replate_filters.* TO '${MYSQL_USER}'@'%' IDENTIFIED BY '${MYSQL_PASSWORD}';

FLUSH PRIVILEGES;

-- Show created databases
SHOW DATABASES;
EOF

echo "✅ Databases initialized successfully!"


-- Insert default roles (only if not exists)
INSERT INTO roles (name, description)
VALUES ('ROLE_USER', 'Standard user with basic permissions')
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name, description)
VALUES ('ROLE_MODERATOR', 'Moderator with content management permissions')
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name, description)
VALUES ('ROLE_ADMIN', 'Administrator with full system access')
ON CONFLICT (name) DO NOTHING;

-- Insert test users (for development only!)
-- Password: password123 (BCrypt encoded)
INSERT INTO users (username, email, password, full_name, is_enabled)
VALUES ('admin', 'admin@example.com',
        '$2a$12$LJ3m4ys3Lk0TSwHCpNqrDOuOYU7oOq0qP5t1VJjOEZN8q0YI5sA5K',
        'Admin User', true)
ON CONFLICT (username) DO NOTHING;

INSERT INTO users (username, email, password, full_name, is_enabled)
VALUES ('user', 'user@example.com',
        '$2a$12$LJ3m4ys3Lk0TSwHCpNqrDOuOYU7oOq0qP5t1VJjOEZN8q0YI5sA5K',
        'Normal User', true)
ON CONFLICT (username) DO NOTHING;

-- Assign roles to users
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN'
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'user' AND r.name = 'ROLE_USER'
ON CONFLICT DO NOTHING;
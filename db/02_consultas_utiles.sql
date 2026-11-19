-- Consultas para mostrar en la presentación que los datos persisten en PostgreSQL.
-- Entrar desde la EC2:  psql -h ENDPOINT_DEL_RDS -U postgres -d sonido_vivo      (dentro de psql: \dt lista las tablas, \q sale)

-- Usuarios con su rol (la contraseña está guardada como hash BCrypt $2a$12$...)
SELECT u.id, u.nombre, u.correo, r.nombre AS rol, u.activo, LEFT(u.password_hash, 20) AS hash_inicio
FROM usuario u JOIN rol r ON r.id = u.rol_id
ORDER BY u.id;

-- Cantidad de usuarios por rol
SELECT r.nombre AS rol, COUNT(u.id) AS usuarios
FROM rol r LEFT JOIN usuario u ON u.rol_id = r.id
GROUP BY r.nombre
ORDER BY r.nombre;

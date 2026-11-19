Sonido Vivo — Backend (API REST)

API REST de la tienda de instrumentos Sonido Vivo: base de datos PostgreSQL en Amazon RDS, inicio de sesión con JWT y roles.

Asignatura: DSY1104 – Desarrollo Fullstack II (2026) · Evaluación Parcial 3 (avance)

Equipo 7: Sebastian Fica (seb.fica@duocuc.cl) · Alonso Valenzuela (alo.valenzuela@duocuc.cl)

Repositorio frontend: https://github.com/SAFD666/SonidoVivo.git

Swagger (EC2): http://IP_PUBLICA_EC2:8080/swagger-ui.html (reemplazar por la IP pública de la EC2)






Estado del avance
Avance mínimo (miércoles 18 de noviembre): tabla de usuarios, inicio de sesión y registro.

Tabla de usuarios en PostgreSQL (con hash BCrypt): Listo (tabla usuario)

Inicio de sesión con JWT: Listo (POST /api/v1/auth/login)

Registro de usuario: Listo (POST /api/v1/auth/register)

Modificar usuario: En avance (PUT /api/v1/usuarios/{id})

Listar usuarios: En avance (GET /api/v1/usuarios)

Eliminar (desactivar) usuario: En avance (DELETE /api/v1/usuarios/{id})

Estados: Listo = probado en Swagger/Postman y guardado en la base de datos · En avance = código y commits en el repositorio, falta terminar pruebas · Pendiente = sin iniciar.

Para el cierre de la semana 15 (domingo 22) se completa el CRUD de usuarios con roles y rutas protegidas, documentado en Swagger.






Tecnología
Java 21 · Spring Boot 3.5 · Spring Data JPA · PostgreSQL (RDS) · Spring Security + JWT (HS256, 60 min) · BCrypt (costo 12) · Swagger (springdoc). Se ejecuta en una EC2 Ubuntu 24.04, puerto 8080.




Usuarios
    Tabla usuario (y rol). Hibernate las crea al iniciar; SQL de referencia en db/01_modelo_referencia.sql.
 
    Roles: Administrador, Vendedor, Cliente. El registro público siempre crea Cliente; el rol lo asigna el servidor.

    Contraseñas: solo se guarda el hash BCrypt (usuario.password_hash).

    Eliminar un usuario lo desactiva (activo = false); ya no puede iniciar sesión.

    Login con credenciales incorrectas responde un error genérico (401).

Rutas y permisos (todas las rutas van bajo /api/v1):

    POST /auth/register, POST /auth/login: Público

    GET /auth/me: Autenticado

    GET /usuarios, POST /usuarios: Administrador

    GET /usuarios/{id}, PUT /usuarios/{id}: Administrador, o el propio usuario

    DELETE /usuarios/{id}, PATCH /usuarios/{id}/rol, PATCH /usuarios/{id}/reactivar: Administrador

Para probar en Swagger: POST /auth/login, copiar accessToken y usar el botón Authorize.

Códigos de error: 400 datos inválidos · 401 sin token o credenciales incorrectas · 403 sin permiso · 404 no existe · 409 correo duplicado · 429 demasiados intentos de login.


Cuentas de demostración
admin@sonidovivo.cl, vendedor@sonidovivo.cl y cliente@correo.cl, todas con la contraseña definida en DEMO_PASSWORD.


Cómo ejecutar

Bash

cp .env.example .env     # completar DB_URL, DB_PASSWORD, JWT_SECRET, DEMO_PASSWORD
set -a; source .env; set +a
./mvnw clean package -DskipTests
java -jar target/sonidovivo-backend-0.1.0.jar

Pruebas: importar postman/SonidoVivo.postman_collection.json, definir baseUrl y demoPassword, y ejecutar Run collection. Consultas para la presentación en db/02_consultas_utiles.sql.
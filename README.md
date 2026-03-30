# 🛒 Bazar API

> ⚠️ **Importante:** Antes de explorar el proyecto, por favor leer la documentación completa en `Assets/BazarAPI_Context.docx` y el diagrama `Assets/UML.png`, donde se detalla el contexto, reglas de negocio y modelado del sistema.

API REST para la gestión de un bazar (ventas, productos, clientes, etc.). Proyecto orientado a prácticas de backend con Java + Spring Boot, aplicando buenas prácticas, testing y CI.

---

## 🚀 Tecnologías

* Java 17
* Spring Boot
* Spring Data JPA (Hibernate)
* PostgreSQL (prod)
* H2 (tests)
* Flyway (migraciones)
* Maven
* GitHub Actions (CI)

---

## 📦 Estructura del proyecto

El código principal se encuentra en:

```
bazar-api/
```

* `src/main/java` → lógica de la aplicación
* `src/main/resources` → configuración (`application.yml`)
* `src/test` → tests unitarios e integración

---

## ⚙️ Configuración

### 🔹 Entorno productivo

Se utiliza PostgreSQL (ej: Supabase) y Flyway para versionado de base de datos.

Variables clave:

* `SPRING_DATASOURCE_URL`
* `SPRING_DATASOURCE_USERNAME`
* `SPRING_DATASOURCE_PASSWORD`

Además, se define explícitamente el dialecto de Hibernate para evitar dependencia del metadata JDBC.

### 🔹 Testing

Los tests corren con H2 en memoria automáticamente, sin necesidad de base externa.

---

## 🧪 Testing & CI

El proyecto incluye integración continua con GitHub Actions:

* Corre en cada push y pull request a `main`
* Ejecuta `./mvnw test`
* No requiere base de datos externa

Archivo:

```
.github/workflows/ci.yml
```

---

## ▶️ Cómo correr el proyecto

Desde la carpeta `bazar-api`:

```bash
./mvnw spring-boot:run
```

O en Windows:

```bash
mvnw.cmd spring-boot:run
```

---

## 📚 Documentación funcional

Para entender el dominio del problema, reglas de negocio y alcance de la aplicación:

👉 Revisar en la carpeta `Assets/`:

* `BazarAPI_Context.docx`
* `UML.png`

Estos archivos contienen el contexto completo del sistema y el modelado de entidades.

---

## 🧠 Objetivo del proyecto

Este proyecto está pensado como:

* Práctica de desarrollo backend realista
* Aplicación de arquitectura en capas
* Uso de migraciones (Flyway)
* Testing automatizado
* Integración continua (CI)



Si querés contribuir o tenés feedback, ¡bienvenido! 🚀

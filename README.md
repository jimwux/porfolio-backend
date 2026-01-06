# Portfolio Backend - Envío de emails con Resend

Backend en **Java + Spring Boot** pensado para un portfolio profesional. Expone un endpoint para recibir mensajes de contacto y enviarlos por email mediante la API de **Resend**, con validaciones y manejo de errores básico para requests inválidas.

## Tecnologías utilizadas

- Java 17
- Spring Boot 4 (WebMVC, WebFlux/WebClient, Validation, Actuator)
- Maven
- Resend API (envío de emails)

## Arquitectura del proyecto

Estructura principal por capas y paquetes:

- **`controller`**: `ContactController` expone el endpoint REST para recibir mensajes.
- **`service`**: `ContactService` construye el payload y ejecuta el envío de emails vía Resend.
- **`dto`**: `ContactRequest` define el payload de entrada con validaciones.
- **`config`**: `CorsConfig` configura CORS; `WebClientConfig` define el `WebClient.Builder`.
- **`common`**: `ApiExceptionHandler` maneja errores de validación (`400 Bad Request`).

## Flujo del envío de correos

1. El cliente realiza un `POST /api/contact` con nombre, email y mensaje.
2. `ContactController` valida el payload (`@Valid`) y delega a `ContactService`.
3. `ContactService` arma el contenido del email, agrega headers y envía la request a `https://api.resend.com/emails`.
4. Si Resend responde con error, se lanza una excepción con el cuerpo de la respuesta.

## Variables de entorno necesarias

Definidas en `src/main/resources/application.properties`:

- `RESEND_API_KEY`: API key para autenticar con Resend (**obligatoria**).
- `APP_MAIL_TO`: destinatario final del email (por defecto: `jimenagomezwusi@hotmail.com`).
- `CORS_ALLOWED_ORIGINS`: orígenes permitidos para CORS (por defecto: `http://localhost:4200`).
- `PORT`: puerto del servidor (por defecto: `8080`).

## Cómo ejecutar el proyecto localmente

1. Exportar variables de entorno:

   ```bash
   export RESEND_API_KEY="tu_api_key"
   export APP_MAIL_TO="tu_email@dominio.com"
   ```

2. Ejecutar el backend:

   ```bash
   ./mvnw spring-boot:run
   ```

3. Probar el endpoint:

   ```bash
   curl -X POST http://localhost:8080/api/contact \
     -H "Content-Type: application/json" \
     -d '{"name":"Juan","email":"juan@mail.com","message":"Hola desde el portfolio"}'
   ```

## Endpoints principales

- `POST /api/contact`
  - **Body**: `name`, `email`, `message`.
  - **Respuesta exitosa**: `{ "ok": true }`.

## Manejo de errores y validaciones

- Validaciones en `ContactRequest`:
  - `name`, `email`, `message` obligatorios (`@NotBlank`).
  - `email` con formato válido (`@Email`).
  - tamaños máximos definidos (`@Size`).
- `ApiExceptionHandler` devuelve `400` con un listado de errores por campo cuando la validación falla.

## Posibles mejoras futuras

- Persistir mensajes en base de datos para auditoría.
- Agregar rate limiting o captcha para evitar spam.
- Incorporar logging estructurado y métricas de envío.
- Añadir plantillas HTML y pruebas de integración con Resend.

## Autor / Portfolio

Proyecto desarrollado para el portfolio profesional de **Jimena**.

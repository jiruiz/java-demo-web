# Demo API de alumnos con Spring Boot y Swagger

Proyecto de práctica para crear una API REST de alumnos usando Spring Boot,
Spring Data JPA, MySQL y Swagger.

## Arquitectura actual

```text
Swagger / cliente HTTP
        |
        | GET /api/alumnos
        v
AlumnoController
        |
        v
AlumnoRepository
        |
        v
Base de datos MySQL (tabla alumnos)
```

- `AlumnoController`: recibe las peticiones HTTP y devuelve JSON.
- `AlumnoRepository`: realiza las operaciones sobre la base de datos.
- `Alumno`: representa la entidad y la tabla `alumnos`.
- `InicioController`: continúa atendiendo las páginas HTML con Thymeleaf.

## 1. Dependencias necesarias

En `pom.xml` se agregaron Spring Web y Springdoc:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>3.1.0</version>
</dependency>
```

Después de modificar el `pom.xml`, hay que guardar el archivo y recargar el
proyecto Maven en NetBeans. La primera descarga puede tardar varios minutos.

## 2. Controlador REST

Se creó el archivo:

```text
src/main/java/com/example/demo/controller/AlumnoController.java
```

Configuración principal:

```java
@RestController
@RequestMapping("/api/alumnos")
@Tag(name = "Alumnos", description = "API para administrar alumnos")
public class AlumnoController {

    private final AlumnoRepository alumnoRepository;

    public AlumnoController(AlumnoRepository alumnoRepository) {
        this.alumnoRepository = alumnoRepository;
    }
}
```

- `@RestController` indica que el controlador devuelve datos, normalmente JSON.
- `@RequestMapping` establece la dirección base de los endpoints.
- `@Tag` agrupa los endpoints dentro de Swagger.
- Spring proporciona automáticamente una instancia de `AlumnoRepository`.

## 3. Endpoint para listar alumnos

Dentro de `AlumnoController` se agregó:

```java
@Operation(summary = "Listar todos los alumnos")
@GetMapping
public List<Alumno> listar() {
    return alumnoRepository.findAll();
}
```

El endpoint resultante es:

```http
GET /api/alumnos
```

`findAll()` viene incluido en `JpaRepository`; no es necesario escribir el
`SELECT` manualmente.

## 4. Ejecutar el proyecto

Desde NetBeans:

1. Abrir `DemoApplication.java`.
2. Hacer clic derecho dentro del archivo.
3. Seleccionar **Run File**.
4. Esperar hasta ver `Started DemoApplication`.

También se puede ejecutar desde una terminal ubicada en el proyecto:

```powershell
.\mvnw.cmd spring-boot:run
```

La aplicación utiliza el puerto `8081`, configurado en
`application.properties`.

## 5. Probar con Swagger

Abrir en el navegador:

```text
http://localhost:8081/swagger-ui.html
```

Luego:

1. Abrir la sección **Alumnos**.
2. Seleccionar `GET /api/alumnos`.
3. Presionar **Try it out**.
4. Presionar **Execute**.

Si todo funciona, la respuesta tendrá estado HTTP `200` y una lista JSON.

## Error `${start-class}` en NetBeans

Si aparece:

```text
ClassNotFoundException: ${start-class}
```

el código puede haber compilado correctamente, pero NetBeans no encontró la
clase principal en su acción de ejecución. Se puede evitar ejecutando
directamente `DemoApplication.java` con **Run File** o usando:

```powershell
.\mvnw.cmd spring-boot:run
```

## Próximos pasos

Completar el ABM agregando al controlador:

- `GET /api/alumnos/{dni}` para buscar.
- `POST /api/alumnos` para crear.
- `PUT /api/alumnos/{dni}` para modificar.
- `DELETE /api/alumnos/{dni}` para eliminar.

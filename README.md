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

---

# Configuración de usuarios, roles y login

Esta sección documenta cómo se agregó autenticación con Spring Security y
contraseñas cifradas con BCrypt.

## Flujo del login

```text
Formulario /login
       |
       v
Spring Security
       |
       v
CustomUserDetailsService
       |
       v
UserRepository -> tabla usuarios
       |
       v
BCrypt compara la contraseña ingresada con el hash guardado
```

BCrypt es un algoritmo de hash. La contraseña original no se guarda ni se
puede recuperar. Durante el login se usa `matches()` para comprobarla.

## 1. Dependencia de Spring Security

Agregar en `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

`spring-boot-starter-security` ya incluye las clases necesarias para usar
BCrypt, por lo que no hace falta declarar `spring-security-crypto` por
separado.

## 2. Roles

Archivo:

```text
src/main/java/com/example/demo/model/Role.java
```

```java
package com.example.demo.model;

public enum Role {
    USER,
    ADMIN
}
```

`USER` representa un usuario común y `ADMIN` un administrador.

## 3. Entidad User

Archivo:

```text
src/main/java/com/example/demo/model/User.java
```

Campos principales:

```java
@Entity
@Table(name = "usuarios")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String usuario;

    @Column(nullable = false)
    private String clave;

    private String datosPersonales;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;
}
```

Con `spring.jpa.hibernate.ddl-auto=update`, Hibernate crea o actualiza la tabla
`usuarios`. `EnumType.STRING` guarda el rol como `USER` o `ADMIN` en lugar de
un número.

## 4. Repositorio de usuarios

Archivo:

```text
src/main/java/com/example/demo/repository/UserRepository.java
```

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsuario(String usuario);
}
```

`findByUsuario` se usa para comprobar si el usuario existe y para buscarlo
durante el login.

## 5. Cifrar contraseñas antes de guardar

El `PasswordEncoder` se declara en `SecurityConfig`. Después se inyecta en
`UserService`:

```java
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User guardar(User user) {
        user.setClave(passwordEncoder.encode(user.getClave()));
        return userRepository.save(user);
    }

    public boolean claveCorrecta(User user, String claveIngresada) {
        return passwordEncoder.matches(claveIngresada, user.getClave());
    }
}
```

Para crear usuarios se debe llamar a `userService.guardar(user)`. No se debe
usar directamente `userRepository.save(user)`, porque así la contraseña podría
guardarse sin cifrar.

## 6. Cargar el usuario desde Spring Security

`CustomUserDetailsService` implementa `UserDetailsService`, busca el usuario en
la base de datos y adapta nuestro modelo al formato esperado por Spring:

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    public CustomUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String usuario) {
        User user = repository.findByUsuario(usuario)
                .orElseThrow(() ->
                    new UsernameNotFoundException("Usuario no encontrado")
                );

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsuario())
                .password(user.getClave())
                .roles(user.getRole().name())
                .build();
    }
}
```

## 7. Configuración de seguridad

En `SecurityConfig` se configura BCrypt, el formulario de login y las rutas:

```java
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        return http
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/login", "/css/**", "/js/**").permitAll()
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
                )
                .formLogin(form -> form
                    .loginPage("/login")
                    .defaultSuccessUrl("/", true)
                    .permitAll()
                )
                .logout(logout -> logout
                    .logoutSuccessUrl("/login?logout")
                )
                .build();
    }
}
```

- `/login`, CSS y JavaScript son públicos.
- `/admin/**` requiere el rol `ADMIN`.
- Las demás rutas requieren haber iniciado sesión.

## 8. Controlador y formulario de login

El controlador devuelve la plantilla `login.html`:

```java
@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
```

Formulario ubicado en `src/main/resources/templates/login.html`:

```html
<p data-th-if="${param.error}">Usuario o contraseña incorrectos</p>
<p data-th-if="${param.logout}">Sesión cerrada correctamente</p>

<form data-th-action="@{/login}" method="post">
    <label>Usuario:</label>
    <input type="text" name="username" required>

    <label>Contraseña:</label>
    <input type="password" name="password" required>

    <button type="submit">Ingresar</button>
</form>
```

Los nombres `username` y `password` son los nombres que Spring Security espera
por defecto. Se usa `data-th-*` para que Thymeleaf funcione sin advertencias del
validador HTML de NetBeans.

## 9. Crear un administrador inicial

`DatosIniciales.java` está junto a `DemoApplication.java`:

```text
src/main/java/com/example/demo/DatosIniciales.java
```

El `CommandLineRunner` se ejecuta al iniciar la aplicación:

```java
@Bean
CommandLineRunner crearAdmin(UserRepository repository,
                             UserService userService) {
    return args -> {
        if (repository.findByUsuario("admin").isEmpty()) {
            User user = new User();
            user.setUsuario("admin");
            user.setClave("1234");
            user.setDatosPersonales("Administrador");
            user.setRole(Role.ADMIN);

            userService.guardar(user);
        }
    };
}
```

El `if` evita volver a crear el usuario o cifrar nuevamente su contraseña cada
vez que inicia la aplicación.

Credenciales de práctica:

```text
Usuario: admin
Contraseña: 1234
```

En una aplicación real, la contraseña inicial no debe quedar escrita en el
código fuente.

## 10. Error por `passwordEncoder` duplicado

Si aparece este error:

```text
A bean with that name has already been defined
```

significa que existe un método `passwordEncoder()` tanto en
`PasswordConfig.java` como en `SecurityConfig.java`. Debe existir un solo bean.
En este proyecto se conserva el de `SecurityConfig` y se elimina
`PasswordConfig.java`.

No es necesario habilitar:

```properties
spring.main.allow-bean-definition-overriding=true
```

## 11. Probar el login

1. Ejecutar la aplicación.
2. Abrir `http://localhost:8081/login`.
3. Ingresar con `admin` y `1234`.
4. Spring Security busca al usuario en MySQL.
5. BCrypt compara la clave ingresada con el hash guardado.
6. Si es correcta, redirige a `/`.

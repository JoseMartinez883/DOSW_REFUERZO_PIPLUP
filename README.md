# PIPLUP FASE 01— DOSW

## Datos personales:

- Nombre y Apellido: Jose Alejandro Martinez Arias
- Código de Estudiante: 1000104385
- Curso: DOSW (Desarrollo y operaciones sofware)

---

### Ejercicio 01 — Filtrar y ordenar la flota de drones acuáticos disponibles

Se requiere gestionar la flota de drones acuáticos a través del API de Streams de Java para:

1. Filtrar los drones disponibles con batería mayor o igual al 35% y ordenarlos descendentemente según su nivel de carga.
2. Obtener una lista con únicamente los identificadores (`id`) de los drones que se encuentren disponibles.
3. Verificar si existe al menos un dron disponible con batería suficiente (>= 35%).
4. Contabilizar la cantidad total de unidades disponibles.
5. Determinar cuál es el dron con mayor porcentaje de batería en la flota.

**Código implementado:**

`DroneAcuatico.java`

```java
package main.dosw.piplup;

public record DroneAcuatico(
        String  id,
        String  modelo,
        int     bateria,       // 0-100%
        boolean disponible,
        String  zona           // "Embalse Norte", "Canal Central", etc.
) {}
```

`ConsultorFlota.java`

```Java
package main.dosw.piplup;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ConsultorFlota {

    public List<DroneAcuatico> obtainDroneAvailableWithEnoughBattery(List<DroneAcuatico> drones){
        return drones.stream().filter(drone -> drone.bateria() >= 35)
                .filter(DroneAcuatico::disponible)
                .sorted(Comparator.comparing(DroneAcuatico::bateria).reversed()).toList();
    }

    public List<String> obtainIdsDronesAvailable(List<DroneAcuatico> drones){
        return drones.stream().filter(DroneAcuatico::disponible).map(DroneAcuatico::id).toList();
    }

    public boolean existsDroneAvailableWithEnoughBattery(List<DroneAcuatico> drones){
        return drones.stream().anyMatch(drone -> drone.disponible() && drone.bateria() >= 35);
    }

    public long amountDronesAvailable(List<DroneAcuatico> drones){
        return drones.stream().filter(DroneAcuatico::disponible).count();
    }

    public Optional<DroneAcuatico> obtainDroneWithLargestBattery(List<DroneAcuatico> drones){
        return drones.stream().max(Comparator.comparing(DroneAcuatico::bateria));
    }
}
```

`Ejercisio1.java`

```Java
package main.dosw.piplup;

import java.util.List;

public class Ejercisio1 {
    public static void main(String[] args){
        List<DroneAcuatico> flota = List.of(
            new DroneAcuatico("AR-01", "Aqua-Ranger 100", 92, true,  "Embalse Norte"),
            new DroneAcuatico("AR-02", "Aqua-Ranger 100", 45, true,  "Canal Central"),
            new DroneAcuatico("AR-03", "Aqua-Ranger 100", 18, false, "Laguna Sur"),
            new DroneAcuatico("AR-04", "Aqua-Ranger 100", 73, true,  "Punto Ribereño Este")
        );

        ConsultorFlota consultor = new ConsultorFlota();

        System.out.println("1. Disponibles con batería >= 35% ordenados:");
        System.out.println(consultor.obtainDroneAvailableWithEnoughBattery(flota));

        System.out.println("\n2. IDs de drones disponibles:");
        System.out.println(consultor.obtainIdsDronesAvailable(flota));

        System.out.println("\n3. ¿Existe algún disponible con batería >= 35%?");
        System.out.println(consultor.existsDroneAvailableWithEnoughBattery(flota));

        System.out.println("\n4. Total de drones disponibles:");
        System.out.println(consultor.amountDronesAvailable(flota));

        System.out.println("\n5. Drone con mayor batería:");
        System.out.println(consultor.obtainDroneWithLargestBattery(flota));
    }
}
```

**Captura de ejecución:**
![ExecutionExercise01PiplupPhase.png](docs/images/ExecutionExercise01PiplupPhase.png)

**Explicación:**Se modela la entidad con el record inmutable `DroneAcuatico` y se resuelven las consultas mediante la API de Streams:

- `filter()` para validar la disponibilidad y el umbral de carga.
- `sorted()` junto con `Comparator.comparing().reversed()` para ordenar de mayor a menor batería.
- `map()` con referencia a método (`DroneAcuatico::id`) para proyectar la lista de identificadores.
- `anyMatch()` para comprobar de forma eficiente si hay al menos una coincidencia.
- `count()` para el total de drones activos y `max()` para obtener el dron con mayor batería envuelto de forma segura en un `Optional`.

---

### Ejercicio 02 — Repositorio de AquaPort con estructura GitFlow básica

Configurar el repositorio base para AquaPort aplicando la metodología GitFlow. Se define un archivo `.gitignore` robusto para entornos Java/Maven e IDEs, asegurando que binarios y configuraciones locales no se versionen. Se implementa el flujo de trabajo creando la rama de integración `develop` y la rama de trabajo `feature/streams-consultor-flota`, gestionando la inclusión de cambios mediante commits atómicos y consolidando el trabajo en `develop` mediante un Pull Request.

**Código implementado:**

`.gitignore`

```gitignore
# Compiled class file
*.class

# Log file
*.log

# BlueJ files
*.ctxt

# Mobile Tools for Java (J2ME)
.mtj.tmp/

# Package Files #
*.jar
*.war
*.nar
*.ear
*.zip
*.tar.gz
*.rar

# virtual machine crash logs, see [http://www.java.com/en/download/help/error_hotspot.xml](http://www.java.com/en/download/help/error_hotspot.xml)
hs_err_pid*
replay_pid*

# Maven target directory
target/

# IntelliJ IDEA
.idea/
*.iml

# Eclipse
.project
.classpath
.settings/

# Visual Studio Code
.vscode/
```

`Flujo de comandos Git ejecutados:`

```bash
# Inicialización y configuración de ramas base
git init
git add .gitignore
git commit -m "chore: configurar .gitignore para Java y herramientas de desarrollo"
git branch -M main

# Creación de rama de integración develop
git checkout -b develop

# Creación de rama de característica para el reto 01
git checkout -b feature/streams-consultor-flota

# Commits atómicos de la funcionalidad
git add src/main/java/main/dosw/piplup/DroneAcuatico.java
git commit -m "feat: definir record DroneAcuatico para el modelo MVP"

git add src/main/java/main/dosw/piplup/ConsultorFlota.java
git commit -m "feat: implementar ConsultorFlota con 5 consultas Stream"

git add src/main/java/main/dosw/piplup/Ejercisio1.java
git commit -m "test: agregar clase principal para verificar consultas de flota"

# Publicación de la rama hacia el repositorio remoto
git push -u origin feature/streams-consultor-flota
```

**Pull Request:**

- **Enlace:** [Feature/piplup dosw ejercicio 01 by JoseMartinez883 · Pull Request #1 · JoseMartinez883/DOSW_REFUERZO_PIPLUP](https://github.com/JoseMartinez883/DOSW_REFUERZO_PIPLUP/pull/1)

**Captura de ejecución:**
![CommitsExercise02PhasePiplup.png](docs/images/CommitsExercise02PhasePiplup.png)

**Explicación:**
Se estableció la estrategia de ramas con GitFlow aislando el desarrollo de la funcionalidad en la rama `feature/piplup-dosw-ejercicio-01` a partir de `feature/piplup`. Se redactaron commits atómicos siguiendo el estándar semántico (especificando prefijos como `feat` y `chore`), evitando mezclar cambios de configuración con lógica de negocio. Asimismo, el `.gitignore` previene la contaminación del repositorio al ignorar artefactos de compilación (`target/`, `*.class`) y metadatos de entornos de desarrollo (`.idea/`, `.vscode/`). Finalmente, la integración hacia `feature/piplup` se gestionó formalmente a través del Pull Request #1. Tener en cuenta que el .gitignore, ya estaba adeacuado, ya que antes de comenzar con la realizacion del taller de refuerzo, primero se preparo la estructura de directorios requeridas y de .gitignore.

---

### Ejercicio 03 — Builder para construir misiones válidas en AquaPort

Implementar el patrón de diseño creacional Builder para la instanciación de objetos de tipo `Mision` en el sistema AquaPort. El patrón garantiza la inmutabilidad de la entidad y previene la creación de instancias con parámetros inconsistentes o nulos mediante encadenamiento de métodos y validaciones integradas en la operación terminal `build()`.

**Código implementado:**

`TipoCarga.java`

```java
package main.dosw.piplup;

public enum TipoCarga {
    MUESTRA_AGUA
}
```

`EstadoMision.java`

```java
package main.dosw.piplup;

public enum EstadoMision {
    PENDIENTE,PROCESO,FINALIZADA
}
```

`Mision.java`

```java
package main.dosw.piplup;

public class Mision {

    private final String id;
    private final DroneAcuatico drone;
    private final String puntoLlegada;
    private final String puntoPartida;
    private final TipoCarga tipoCarga;
    private final EstadoMision estadoMision;

    private Mision(String id, DroneAcuatico drone, String puntoLlegada, String puntoPartida, TipoCarga tipoCarga, EstadoMision estadoMision) {
        this.id = id;
        this.drone = drone;
        this.puntoLlegada = puntoLlegada;
        this.puntoPartida = puntoPartida;
        this.tipoCarga = tipoCarga;
        this.estadoMision = estadoMision;
    }

    public String getId() {
        return id;
    }

    public DroneAcuatico getDrone() {
        return drone;
    }

    public String getPuntoLlegada() {
        return puntoLlegada;
    }

    public String getPuntoPartida() {
        return puntoPartida;
    }

    public TipoCarga getTipoCarga() {
        return tipoCarga;
    }

    public EstadoMision getEstadoMision() {
        return estadoMision;
    }

    public static class Builder {
        private String id;
        private DroneAcuatico drone;
        private String puntoLlegada;
        private TipoCarga tipoCarga;
        private EstadoMision estadoMision = EstadoMision.PENDIENTE;
        private String puntoPartida;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder drone(DroneAcuatico drone) {
            this.drone = drone;
            return this;
        }

        public Builder puntoLlegada(String puntoLlegada) {
            this.puntoLlegada = puntoLlegada;
            return this;
        }

        public Builder tipoCarga(TipoCarga tipoCarga) {
            this.tipoCarga = tipoCarga;
            return this;
        }

        public Builder puntoPartida(String puntoPartida) {
            this.puntoPartida = puntoPartida;
            return this;
        }

        public Mision build() {
            if (id == null || drone == null || puntoLlegada == null || tipoCarga == null
                    || puntoPartida == null || id.isBlank() || puntoLlegada.isBlank() || puntoPartida.isBlank()) {
                throw new IllegalStateException("Falta campos por diligenciar sobre la mision o hay campos coN mensajes vacios");
            } else if (!drone.disponible()){
                throw new IllegalStateException("El drone asignado no se encuentra disponible");
            }

            return new Mision(id, drone, puntoLlegada, puntoPartida, tipoCarga, EstadoMision.PENDIENTE);
        }
    }
}
```

`Ejercicio03.java`

```java
package main.dosw.piplup;

public class Ejercicio03 {
    public static void main(String[] args) {
        DroneAcuatico droneValido = new DroneAcuatico("AR-01", "Aqua-Ranger 100", 85, true, "Embalse Norte");
        DroneAcuatico droneNoDisponible = new DroneAcuatico("AR-03", "Aqua-Ranger 100", 18, false, "Laguna Sur");

        // 1. Caso exitoso
        Mision misionValida = new Mision.Builder()
                .id("M-001")
                .drone(droneValido)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .build();

        System.out.println("Misión construida correctamente: " + misionValida.getId() + " - " + misionValida.getEstadoMision());

        // 2. Validación de disponibilidad del drone
        try {
            new Mision.Builder()
                    .id("M-002")
                    .drone(droneNoDisponible)
                    .puntoPartida("Laguna Sur")
                    .puntoLlegada("Laboratorio Hídrico")
                    .tipoCarga(TipoCarga.MUESTRA_AGUA)
                    .build();
        } catch (IllegalStateException e) {
            System.out.println("Excepción capturada esperada: " + e.getMessage());
        }

        // 3. Validación de campos obligatorios o vacíos
        try {
            new Mision.Builder()
                    .id("")
                    .drone(droneValido)
                    .puntoPartida("Embalse Norte")
                    .puntoLlegada("   ")
                    .build();
        } catch (IllegalStateException e) {
            System.out.println("Excepción capturada esperada: " + e.getMessage());
        }
    }
}
```

**Captura de ejecución:**
![ExecutionExercise03PiplupPhase.png](docs/images/ExecutionExercise03PiplupPhase.png)

**Explicación:**
Se encapsuló la construcción de la clase inmutable `Mision` mediante su clase anidada estática `Builder`. A través de una interfaz fluida se asignan los atributos requeridos, delegando al método `build()` la comprobación estricta de precondiciones: verifica que las cadenas no sean nulas ni estén compuestas únicamente por espacios en blanco (`isBlank()`), que las referencias a objetos/enums no sean nulas y que el `DroneAcuatico` asignado cumpla la regla de negocio de encontrarse en estado disponible. De no cumplirse alguna regla, interrumpe el flujo levantando una `IllegalStateException` descriptiva.

---

### Ejercicio 04 — SRP y DIP en el núcleo de AquaPort MVP

Diseño e implementación de la arquitectura del núcleo de AquaPort MVP aplicando los principios SOLID, específicamente el **Principio de Responsabilidad Única (SRP)** y el **Principio de Inversión de Dependencias (DIP)**. Se desacopla la persistencia mediante la abstracción `RepositorioMisiones`, se aísla la verificación de reglas de negocio en `ValidadorMision`, la emisión de alertas y mensajes en `NotificadorOperador`, y la orquestación del flujo de alta y consulta en `RegistradorMisiones` mediante inyección de dependencias por constructor.

**Código implementado:**

`RepositorioMisiones.java`

```java
package main.dosw.piplup;

import java.util.List;
import java.util.Optional;

public interface RepositorioMisiones {

    void guardar(Mision mision);

    Optional<Mision> buscarPorId(String id);

    List<Mision> obtenerTodas();

    boolean existePorId(String id);
}
```

`RepositorioMisionesMemoria.java`

```java
package main.dosw.piplup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class RepositorioMisionesMemoria implements RepositorioMisiones {

    private final Map<String, Mision> baseDatos = new LinkedHashMap<>();

    @Override
    public void guardar(Mision mision) {
        Objects.requireNonNull(mision, "La misión a guardar no puede ser nula");
        baseDatos.put(mision.getId(), mision);
    }

    @Override
    public Optional<Mision> buscarPorId(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(baseDatos.get(id));
    }

    @Override
    public List<Mision> obtenerTodas() {
        return Collections.unmodifiableList(new ArrayList<>(baseDatos.values()));
    }

    @Override
    public boolean existePorId(String id) {
        if (id == null || id.isBlank()) {
            return false;
        }
        return baseDatos.containsKey(id);
    }
}
```

`ValidadorMision.java`

```java
package main.dosw.piplup;

public class ValidadorMision {

    private static final int BATERIA_MINIMA = 35;

    public boolean tieneBateriaSuficiente(DroneAcuatico drone) {
        if (drone == null) {
            return false;
        }
        return drone.bateria() >= BATERIA_MINIMA;
    }

    public void validarPuntoLlegada(String puntoLlegada) {
        if (puntoLlegada == null || puntoLlegada.isBlank()) {
            throw new IllegalArgumentException("El punto de llegada no puede ser nulo ni estar vacío");
        }
    }

    public void validarPuntoPartida(String puntoPartida) {
        if (puntoPartida == null || puntoPartida.isBlank()) {
            throw new IllegalArgumentException("El punto de partida no puede ser nulo ni estar vacío");
        }
    }

    public void validarAsignacion(Mision mision) {
        if (mision == null) {
            throw new IllegalArgumentException("La misión a validar no puede ser nula");
        }
        DroneAcuatico drone = mision.getDrone();
        if (drone == null) {
            throw new IllegalStateException("La misión requiere un drone acuático asignado");
        }
        if (!drone.disponible()) {
            throw new IllegalStateException("El drone asignado " + drone.id() + " no está disponible para operar");
        }
        if (!tieneBateriaSuficiente(drone)) {
            throw new IllegalStateException("El drone " + drone.id() + " tiene batería insuficiente (" + drone.bateria() + "%). Mínimo requerido: " + BATERIA_MINIMA + "%");
        }
        validarPuntoPartida(mision.getPuntoPartida());
        validarPuntoLlegada(mision.getPuntoLlegada());
    }
}
```

`NotificadorOperador.java`

```java
package main.dosw.piplup;

public class NotificadorOperador {

    public void notificarRegistroExitoso(Mision mision) {
        if (mision != null) {
            System.out.println("[AquaPort - Notificación] Misión " + mision.getId()
                    + " registrada exitosamente con drone " + mision.getDrone().id()
                    + " de ruta: " + mision.getPuntoPartida() + " -> " + mision.getPuntoLlegada());
        }
    }

    public void notificarError(String mensaje) {
        System.err.println("[AquaPort - Alerta] Error en operación: " + mensaje);
    }

    public void notificarConsulta(String detalle) {
        System.out.println("[AquaPort - Consulta] " + detalle);
    }
}
```

`RegistradorMisiones.java`

```java
package main.dosw.piplup;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RegistradorMisiones {

    private final RepositorioMisiones repositorio;
    private final ValidadorMision validador;
    private final NotificadorOperador notificador;

    public RegistradorMisiones(RepositorioMisiones repositorio,
                               ValidadorMision validador,
                               NotificadorOperador notificador) {
        this.repositorio = Objects.requireNonNull(repositorio, "El repositorio no puede ser nulo");
        this.validador = Objects.requireNonNull(validador, "El validador no puede ser nulo");
        this.notificador = Objects.requireNonNull(notificador, "El notificador no puede ser nulo");
    }

    public void registrarMision(Mision mision) {
        Objects.requireNonNull(mision, "La misión a registrar no puede ser nula");
        if (repositorio.existePorId(mision.getId())) {
            throw new IllegalStateException("Ya existe una misión registrada con el identificador: " + mision.getId());
        }
        validador.validarAsignacion(mision);
        repositorio.guardar(mision);
        notificador.notificarRegistroExitoso(mision);
    }

    public Optional<Mision> consultarPorId(String id) {
        return repositorio.buscarPorId(id);
    }

    public List<Mision> consultarTodas() {
        return repositorio.obtenerTodas();
    }
}
```

`Ejercicio04.java`

```java
package main.dosw.piplup;

public class Ejercicio04 {

    public static void main(String[] args) {
        System.out.println("=== AquaPort MVP — Ejercicio 04: Principios SOLID (SRP y DIP) ===");

        // Inversión de Dependencias (DIP): Se inyecta la abstracción RepositorioMisiones
        RepositorioMisiones repositorio = new RepositorioMisionesMemoria();
        ValidadorMision validador = new ValidadorMision();
        NotificadorOperador notificador = new NotificadorOperador();

        // Responsabilidad Única (SRP): RegistradorMisiones solo coordina el registro
        RegistradorMisiones registrador = new RegistradorMisiones(repositorio, validador, notificador);

        // Flota de prueba del MVP
        DroneAcuatico droneOptimo = new DroneAcuatico("AR-01", "Aqua-Ranger 100", 92, true, "Embalse Norte");
        DroneAcuatico droneBateriaBaja = new DroneAcuatico("AR-03", "Aqua-Ranger 100", 18, false, "Laguna Sur");

        // 1. Caso exitoso: Misión válida
        System.out.println("\n1. Registrando misión válida:");
        Mision misionValida = new Mision.Builder()
                .id("MIS-101")
                .drone(droneOptimo)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .build();

        registrador.registrarMision(misionValida);

        // 2. Caso fallido: Violación de regla de negocio (batería insuficiente o no disponible)
        System.out.println("\n2. Intentando registrar misión con drone no apto:");
        try {
            Mision misionInvalida = new Mision.Builder()
                    .id("MIS-102")
                    .drone(droneBateriaBaja)
                    .puntoPartida("Laguna Sur")
                    .puntoLlegada("Laboratorio Hídrico")
                    .tipoCarga(TipoCarga.MUESTRA_AGUA)
                    .build();
            registrador.registrarMision(misionInvalida);
        } catch (IllegalStateException e) {
            notificador.notificarError("Validación rechazada: " + e.getMessage());
        }

        // 3. Caso de consulta a través del repositorio inyectado
        System.out.println("\n3. Consultando todas las misiones registradas:");
        registrador.consultarTodas().forEach(m ->
                notificador.notificarConsulta("ID: " + m.getId() + " | Drone: " + m.getDrone().id()
                        + " | Carga: " + m.getTipoCarga() + " | Estado: " + m.getEstadoMision())
        );

        System.out.println("\n4. Consultando misión por ID 'MIS-101':");
        registrador.consultarPorId("MIS-101").ifPresentOrElse(
                m -> notificador.notificarConsulta("Encontrada: " + m.getId() + " hacia " + m.getPuntoLlegada()),
                () -> notificador.notificarError("Misión no encontrada")
        );
    }
}
```

**Captura de ejecución:**
![ExecutionExercise04PiplupPhase.png](docs/images/ExecutionExercise04PiplupPhase.png)

**Explicación:**

1. **Principio de Responsabilidad Única (SRP):** En lugar de un objeto Dios (`GestorMision`) que guarde, valide, notifique e imprima simultáneamente, se modularizó el diseño en clases altamente cohesivas:
   - `RegistradorMisiones`: Exclusivamente orquesta el registro y delegación de consultas.
   - `ValidadorMision`: Contiene y ejecuta estrictamente las validaciones del dominio (batería ≥35%, disponibilidad y parámetros consistentes).
   - `NotificadorOperador`: Responsable de la salida y comunicación de alertas y confirmaciones hacia el operador.
   - `RepositorioMisionesMemoria`: Encargado únicamente de la persistencia de datos en memoria.
2. **Principio de Inversión de Dependencias (DIP):** El servicio de alto nivel `RegistradorMisiones` no se acopla a ninguna implementación concreta de almacenamiento (evitando `new BaseDatosLocal()`), sino que depende de la abstracción `RepositorioMisiones`, inyectada a través de su constructor (`Constructor Injection`). Esto permite sustituir o extender libremente la persistencia (por bases de datos o mocks en pruebas) sin alterar las reglas de negocio.

---

### Ejercicio 05 — Diagrama de Contexto C4 del Sistema AquaPort MVP

Elaboración del **Diagrama de Contexto del Sistema (C4 Nivel 1)** para el sistema AquaPort en su fase inicial (Piplup MVP). El diagrama abstrae los detalles internos del software (clases, bases de datos o algoritmos) presentándolo como una caja negra central, clarificando las fronteras del sistema, los roles de los usuarios que interactúan con él y los flujos de información que intercambian.

**Código implementado / Especificación de Arquitectura:**

#### 1. Actores y Sistemas:

- **Operador Hídrico (Persona):** Personal de operaciones encargado de supervisar la flota de 4 drones Aqua-Ranger 100, consultar su disponibilidad y nivel de batería, y asignar manualmente los drones a las misiones de transporte.
- **Solicitante (Persona):** Investigador, profesor o laboratorista del campus ECI que requiere transportar muestras de agua o sensores entre los puntos hídricos y el laboratorio.
- **Administrador ECI (Persona):** Responsable de la gestión institucional que supervisa el uso de la flota y consulta reportes y estadísticas operativas.
- **AquaPort MVP (Sistema Software):** Sistema central de gestión y monitoreo de drones acuáticos. Gestiona la flota, valida las reglas de asignación y mantiene el registro de las misiones.
- **Sistemas Externos:** Ninguno. El MVP opera de forma autónoma e independiente sin integraciones externas en esta fase.

#### 2. Código del Diagrama en PlantUML (`docs/c4-context-piplup.puml`):

El código fuente del diagrama se encuentra en `docs/c4-context-piplup.puml`.

**Diagrama de Contexto:**
![c4-contexto-piplup.png](docs/uml/c4-contexto-piplup.png)

**Explicación:**

1. **Enfoque de Caja Negra (Nivel 1 C4):** El diagrama no expone ningún detalle interno del software (como clases Java `DroneAcuatico`, interfaces o patrones de diseño). Se enfoca exclusivamente en la delimitación del sistema respecto al mundo exterior.
2. **Definición de Actores orientados a Roles:** Se definen los 3 roles humanos esenciales del MVP (`Operador Hídrico`, `Solicitante` y `Administrador ECI`), evitando nombres propios o cargos organizacionales rígidos.
3. **Flujos Bidireccionales Claramente Etiquetados:** Cada interacción entre los actores y AquaPort MVP explicita el dato o evento que transita, demostrando el ciclo completo de solicitud, asignación manual, retroalimentación y reportería del MVP.

---

### Ejercicio 06 — Análisis de Requisitos (RF, RNF, MoSCoW)

A continuación, se definen los requerimientos iniciales para la primera fase (Piplup - MVP) de AquaPort, aplicando criterios de calidad con métricas medibles para los atributos de calidad, y la clasificación MoSCoW para priorizar los esfuerzos del equipo de desarrollo.

#### Requerimientos Funcionales (RF) y No Funcionales (RNF)

**Requerimientos Funcionales (RF):**

- **RF-01 (Must Have):** El **Operador Hídrico** debe poder registrar una misión de transporte, asignando manualmente un drone disponible de la flota, para despachar entregas en el campus.
- **RF-02 (Must Have):** El **Solicitante** debe poder recibir un código único al solicitar una misión, para tener el rastreo visual del encargo a transportar (muestra de agua o sensor).
- **RF-03 (Should Have):** El **Administrador ECI** debe poder consultar el inventario completo y el estado actual (batería, zona) de toda la flota de drones para supervisar los activos operacionales.

**Requerimientos No Funcionales (RNF):**

- **RNF-01 (Must Have):** *[Rendimiento]* La consulta de drones disponibles para el operador debe procesar los filtros y retornar la lista en menos de **200 milisegundos**, asumiendo una flota máxima de 10 drones simultáneos, medible mediante pruebas de carga en JUnit 5 con `assertTimeout`.
- **RNF-02 (Must Have):** *[Usabilidad / Claridad]* Todos los mensajes de error arrojados por el sistema deben estar en idioma español, ser autoexplicativos y guiar al operador hídrico sobre cómo solucionar el fallo sin necesidad de consultar el manual externo.
- **RNF-03 (Could Have):** *[Disponibilidad]* La plataforma central MVP de AquaPort debe asegurar un uptime (tiempo de actividad) del **99.5%** durante el horario hábil de investigación del campus (Lunes a Viernes, 6:00 AM - 8:00 PM).

**Justificación MoSCoW para el MVP:**

- **Must Have:** El registro de misiones (RF-01) y la generación de comprobantes (RF-02) son el núcleo de AquaPort, al igual que un rendimiento básico (RNF-01) y mensajes claros en el idioma del operador para evitar confusiones operativas (RNF-02). Sin ellos, el MVP no cumple su propósito ni es funcional para los usuarios finales.
- **Should Have:** La consulta general del administrador (RF-03) agrega mucho valor administrativo, pero si no se entrega en el sprint inicial, el operador aún puede despachar drones.
- **Could Have:** Alta disponibilidad estricta (RNF-03) es deseable para evitar interrupciones, pero durante el MVP (prueba de concepto), caídas ocasionales son tolerables mientras se estabiliza la arquitectura.
- **Won't Have:** Asignación automática de drones a misiones y rutas multi-etapa complejas; estas funcionalidades quedan fuera del alcance del MVP y se planifican para la fase Prinplup.

---

### Ejercicio 07 — Plantilla DOSW para RF "Registrar misión de transporte"

Utilizando la plantilla oficial de la asignatura para el análisis de requerimientos (`Plantilla_Requerimientos_DOSW.docx`), se documenta formalmente el Requerimiento Funcional principal de esta fase.

#### FUNCIONALIDAD

| Campo                          | Detalle                                                                                                                                      |
| :----------------------------- | :------------------------------------------------------------------------------------------------------------------------------------------- |
| **Código:**             | RF-01                                                                                                                                        |
| **Nombre:**              | Registrar misión de transporte                                                                                                              |
| **Descripción:**        | Permite registrar formalmente en el sistema una solicitud de misión, verificando la disponibilidad del drone y acoplándolo a la locación. |
| **Cómo se ejecutará:** | A través de la consola/backend interactuando con la clase`RegistradorMisiones`.                                                           |
| **Actor principal:**     | Operador Hídrico                                                                                                                            |
| **Precondiciones:**      | El sistema debe estar inicializado y deben existir drones registrados en la flota.                                                           |

#### DATOS DE ENTRADA

| Nombre           | Descripción                        | Tipo de campo     | Reglas / Aplicación                                | Obligatorio |
| :--------------- | :---------------------------------- | :---------------- | :-------------------------------------------------- | :---------- |
| `idMision`     | Identificador único de la misión  | `String`        | No puede estar vacío ni contener solo espacios.    | Sí         |
| `drone`        | Drone seleccionado para la misión  | `DroneAcuatico` | Debe estar instanciado correctamente en el sistema. | Sí         |
| `puntoPartida` | Ubicación de inicio de la misión  | `String`        | No puede estar vacío ni contener solo espacios.    | Sí         |
| `puntoLlegada` | Ubicación de destino de la misión | `String`        | No puede estar vacío ni contener solo espacios.    | Sí         |
| `tipoCarga`    | Tipo de elemento a transportar      | `Enum`          | Debe ser un valor válido (ej.`MUESTRA_AGUA`).    | Sí         |

#### DATOS DE SALIDA

| Nombre               | Descripción                       | Tipo de campo | Reglas / Aplicación                                                      | Obligatorio |
| :------------------- | :--------------------------------- | :------------ | :------------------------------------------------------------------------ | :---------- |
| `misionRegistrada` | El objeto de la misión confirmada | `Mision`    | Se crea inmutablemente a través del patrón*Builder*.                  | Sí         |
| `mensajeExito`     | Confirmación en consola           | `String`    | Mensaje descriptivo con prefijo`[AquaPort - Notificación]`.            | Sí         |
| `mensajeError`     | Excepción en caso de fallo        | `Exception` | Excepciones como`IllegalArgumentException` o `IllegalStateException`. | (En fallos) |

#### FLUJO BÁSICO

| Paso | Actor             | Descripción                                                                                                                                                 | Excepciones                                                           |
| :--- | :---------------- | :----------------------------------------------------------------------------------------------------------------------------------------------------------- | :-------------------------------------------------------------------- |
| 1    | Operador Hídrico | Ingresa los datos de la nueva misión (ID, locaciones) y selecciona un drone de la flota. El sistema verifica internamente que los campos no estén vacíos. | `IllegalArgumentException` si hay datos inválidos.                 |
| 2    | Operador Hídrico | Confirma el registro. El sistema verifica que el drone esté "DISPONIBLE" y con batería`>= 35%`.                                                          | `IllegalStateException` si no cumple las reglas de estado/batería. |
| 3    | Operador Hídrico | Recibe la confirmación luego de que el sistema construye la`Mision` inmutable y la almacena exitosamente en memoria.                                      | `IllegalArgumentException` si el ID de la misión ya existe.        |

#### FLUJO ALTERNO

| Paso | Actor             | Descripción                                                                                                                             | Excepciones                                                |
| :--- | :---------------- | :--------------------------------------------------------------------------------------------------------------------------------------- | :--------------------------------------------------------- |
| 2a   | Operador Hídrico | Intenta confirmar el registro con un drone ocupado o sin batería. El sistema aborta la operación y le notifica el rechazo en pantalla. | `IllegalStateException` arrojada y mostrada al operador. |
| 3a   | Operador Hídrico | Intenta registrar la misión con un identificador (`idMision`) previamente usado. El sistema aborta el registro.                       | `IllegalArgumentException` (Identificador duplicado).    |

#### ANEXOS / PROTOTIPOS

> **Notas y comentarios:** Al tratarse del MVP en su fase inicial (Piplup), el prototipo de interfaz es estrictamente a nivel de terminal/consola. Las validaciones de los flujos alternos se comprueban mediante pruebas unitarias o métodos `main` de demostración (ver captura de Ejercicio 04).

#### REGLAS DE NEGOCIO

| No.   | Descripción                                                                                                     |
| :---- | :--------------------------------------------------------------------------------------------------------------- |
| RN-01 | Todo drone asignado a una misión debe tener un nivel de batería estrictamente mayor o igual al**35%**.   |
| RN-02 | Un drone solo puede ser asignado a una misión si su estado actual es**DISPONIBLE**.                       |
| RN-03 | Las instancias de`Mision` deben ser inmutables para evitar modificaciones inconsistentes luego de su registro. |

#### ABREVIATURAS

| Abreviatura   | Significado                                      |
| :------------ | :----------------------------------------------- |
| **MVP** | Minimum Viable Product (Producto Mínimo Viable) |
| **RF**  | Requerimiento Funcional                          |
| **ID**  | Identificador Único                             |

#### HISTORIAL DE REVISIÓN

| Elaborado por | Aprobado por  | Fecha      | Descripción y Justificación de Cambios             |
| :------------ | :------------ | :--------- | :--------------------------------------------------- |
| Jose Martinez | Jose Martinez | 10/10/2026 | Versión inicial del documento para el MVP (Piplup). |

---

### Ejercicio 08 — Manual de Identidad y UX/UI (AquaPort MVP)

En concordancia con las buenas prácticas de diseño de producto y las directrices de la fase Piplup, la interfaz de monitoreo de AquaPort se construyó bajo la premisa de **identidad corporativa primero, diseño de interfaz después**. Esto garantiza coherencia visual, accesibilidad y claridad cognitiva para el Operador Hídrico frente a la pantalla de control.

> 🌐 **Enlace directo al Manual de Identidad interactivo:**  
> 👉 [**Abrir Manual de Identidad de AquaPort (`AquaPort_Manual_de_Identidad.html`)**](docs/mockups/AquaPort_Manual_de_Identidad.html)

#### 1. Manual de Identidad Corporativa

El manual de identidad formal de AquaPort ha sido desarrollado como artefacto web interactivo navegable y autocontenido. Puedes consultarlo directamente en el enlace superior o accediendo al archivo [`docs/mockups/AquaPort_Manual_de_Identidad.html`](docs/mockups/AquaPort_Manual_de_Identidad.html).

##### A. Paleta de Colores Institucional
Diseñada específicamente para entornos operativos de monitoreo continuo (modo oscuro de baja fatiga visual), inspirada en ambientes hídricos y con altos contrastes WCAG:

| Nombre | Código Hex | RGB | Uso y Aplicación en Interfaz |
| :--- | :--- | :--- | :--- |
| **Abisal** | `#0A1C26` | `(10, 28, 38)` | Fondo principal del panel y superficie base. |
| **Casco** | `#12303D` | `(18, 48, 61)` | Tarjetas de drones, barras de navegación y contenedores. |
| **Marea** | `#1B7F8C` | `(27, 127, 140)` | Color primario de acción (botones principales, logo claro). |
| **Sonar** | `#7FD8C9` | `(127, 216, 201)` | Acento visual, enlaces, estados de foco y barras de batería. |
| **Sedimento** | `#8CA3A8` | `(140, 163, 168)` | Texto secundario, unidades de medida y etiquetas complementarias. |
| **Bruma** | `#E4EDEC` | `(228, 237, 236)` | Texto principal y encabezados de alta legibilidad (contraste 14.6:1). |

##### B. Colores Semánticos de Estado (Flota de Drones)
Cada estado combina de forma obligatoria **color, forma e icono y texto explicativo** para prevenir ambigüedad en operadores con daltonismo:

| Estado | Código Hex | Ratio Contraste | Icono / Forma | Criterio de Uso Operativo |
| :--- | :--- | :--- | :--- | :--- |
| **Disponible** | `#3FBF8A` | 7.5:1 | Círculo lleno (`●`) | Drone con batería `>= 35%` listo para asignación inmediata. |
| **En misión** | `#4C9BE8` | 5.9:1 | Flecha de avance (`▶`) | Drone ejecutando un despacho o transporte en tránsito. |
| **Recargando** | `#E5B33A` | 9.0:1 | Rayo (`⚡`) | Drone en estación de acople cargando energía. |
| **Mantenimiento** | `#8A979C` | 5.8:1 | Rombo (`◇`) | Drone fuera de servicio por revisión programada. |
| **Fallo** | `#E5604F` | 5.1:1 | Triángulo de alerta (`▲`) | Alerta técnica; requiere atención inmediata del operador. |

##### C. Tipografía
- **IBM Plex Sans (400, 500, 600):** Tipografía principal para títulos, etiquetas de navegación, mensajes y botones.
- **IBM Plex Mono (400, 500):** Tipografía monoespaciada reservada exclusivamente para telemetría y datos que requieren comparación o copiado rápido: identificadores de drone (`AR-01`), códigos de misión (`M-0412`), porcentajes de batería (`82%`) y timestamps (`10:47:12`).

##### D. Tono de Voz y Microcopia
Técnico, conciso, preventivo y en voz activa en español. Se eliminan los mensajes genéricos o decorativos en favor de diagnósticos claros con cifras y pautas de acción concretas (ej. *"El drone AR-03 tiene batería insuficiente (18%). Mínimo requerido: 35%."* en lugar de *"Error de asignación"*).

---

#### 2. Mockup del Panel de Monitoreo de la Flota Acuática

A partir del manual de identidad establecido, se diseñó la interfaz de monitoreo de la flota mostrando los cuatro (4) drones del MVP de AquaPort en operación simultánea:

![Panel de Monitoreo de la Flota Acuática - AquaPort](docs/images/AquaPort_Panel_Flota.png)

##### Aspectos Destacados del Mockup:
1. **Resumen Cuantitativo Superior:** Contadores en tiempo real agrupados por estado (`1 disponible`, `1 en misión`, `1 recargando`, `1 fallo`) que permiten evaluar la capacidad de la flota sin navegar entre pestañas.
2. **Banner de Notificación Crítica:** Aviso destacado en color de fallo sobre el drone `AR-04`, especificando la hora de pérdida de enlace y el punto geográfico donde intervenir (`Punto Ribereño Este`).
3. **Control y Deshabilitación Preventiva (RN-01):**
   - El drone `AR-01` (`82%`) cuenta con el botón primario activo **Asignar misión**.
   - El drone `AR-03` (`18%`) se muestra visualmente deshabilitado (opacidad al 55% y borde discontinuo) con el botón bloqueado y el motivo explícito: *"Batería insuficiente (18%). Mínimo requerido: 35%."*, previniendo errores antes de que ocurran.
4. **Radar y Zonificación Espacial del Campus:** Representación topográfica y batimétrica con las 5 zonas operacionales (`Embalse Norte`, `Canal Central`, `Laguna Sur`, `Punto Ribereño Este`, `Laboratorio Hídrico`), ubicando espacialmente a cada unidad.

---

#### 3. Verificación con las 10 Heurísticas de Usabilidad de Jakob Nielsen

Se evaluó exhaustivamente la interfaz del panel de monitoreo frente a los 10 principios de usabilidad de Jakob Nielsen, verificando el cumplimiento de **7 de las 10 heurísticas** en esta pantalla y justificando las 3 restantes:

| N.° | Heurística de Nielsen | Evaluación | Aplicación y Evidencia Concreta en el Panel de AquaPort |
| :---: | :--- | :---: | :--- |
| **#1** | **Visibilidad del estado del sistema** | **Cumple** | Los indicadores superiores totalizan los estados de la flota en tiempo real. Cada fila de drone y cada nodo del mapa batimétrico muestran su estado con color, icono y texto. El pie de página indica el timestamp exacto de la última sincronización (`Actualizado 10:47:12`). |
| **#2** | **Concordancia entre el sistema y el mundo real** | **Cumple** | Utiliza la terminología natural del operador hídrico (drones, misiones, batería, telemetría). Las zonas geográficas corresponden a los lugares reales del campus de la Escuela Colombiana de Ingeniería (`Embalse Norte`, `Canal Central`, `Laguna Sur`, `Punto Ribereño Este`, `Laboratorio Hídrico`). |
| **#3** | **Control y libertad del usuario** | *No aplica aquí* | La cancelación de misiones pendientes o edición de rutas se gestiona en la vista de detalle de misión; el panel de flota actúa como centro de visualización y despacho rápido. |
| **#4** | **Consistencia y estándares** | **Cumple** | Todas las filas mantienen una jerarquía visual idéntica: ID, estado estandarizado, nivel de batería, zona actual, misión y botón de acción. Los datos numéricos y códigos se expresan uniformemente en tipografía monoespaciada (`IBM Plex Mono`). |
| **#5** | **Prevención de errores** | **Cumple** | El drone `AR-03` con batería al 18% se presenta deshabilitado y su botón de asignación queda inactivo con el mensaje de restricción (`< 35%`). El drone `AR-04` en fallo no ofrece botón de asignación, impidiendo que el operador intente despachar una unidad averiada. |
| **#6** | **Reconocimiento antes que recuerdo** | **Cumple** | Cada fila consolida toda la información que el operador necesita para decidir (ID, porcentaje y barra de batería, ubicación actual y misión en curso). El usuario no necesita recordar datos de pantallas previas para tomar decisiones de asignación. |
| **#7** | **Flexibilidad y eficiencia de uso** | *No aplica aquí* | En la escala del MVP (4 drones), la lista completa cabe en un solo vistazo sin necesidad de filtros ni atajos complejos. Se proyectan aceleradores y filtros avanzados para etapas posteriores con flotas superiores a 50 drones. |
| **#8** | **Diseño estético y minimalista** | **Cumple** | La interfaz evita elementos ornamentales distractores. El mapa topográfico utiliza líneas sutiles sin saturar, y las tarjetas presentan una distribución espaciada en múltiplos de 4 px, priorizando la lectura rápida de los indicadores operativos. |
| **#9** | **Ayudar a reconocer, diagnosticar y recuperarse de errores** | **Cumple** | En lugar de alertas genéricas, los mensajes son precisos y orientados a la solución: para `AR-03` se indica el porcentaje actual y la cota mínima requerida; para `AR-04` el banner diagnostica la hora de desconexión y orienta al operador sobre el punto exacto donde inspeccionar el dispositivo. |
| **#10** | **Ayuda y documentación** | *No aplica aquí* | El panel de control está diseñado para ser autoexplicativo para personal técnico sin manuales contextuales intrusivos. La documentación de diseño y lineamientos se centralizan en el [Manual de Identidad HTML](docs/mockups/AquaPort_Manual_de_Identidad.html). |

---

### Ejercicio 09 — Agilismo y Jira (AquaPort MVP)

En concordancia con los estándares de gestión ágil de la asignatura (**`sprint2-dosw-proyectos.html`** y **`DOSW_Equipo_Piplup.html`**), el desarrollo del MVP de AquaPort se organizó formalmente en Jira aplicando la jerarquía ágil completa:  
**`Épica` ➔ `Features` (incluyendo contrato Swagger/OpenAPI) ➔ `Historias de Usuario (HU)` ➔ `Subtareas Técnicas por capas de software`**, aplicando estimación con Planning Poker (Fibonacci) y la regla estricta de **Definition of Done (DoD) con evidencia**.

> 🌐 **Enlace directo al Proyecto y Backlog en Jira:**  
> 👉 [**Abrir Proyecto AquaPort en Atlassian Jira (Tablero AQUA)**](https://josealejandromartinezarias9.atlassian.net/jira/software/projects/AQUA/summary?atlOrigin=eyJpIjoiN2JlNGY5ZTE1OWIxNGMwNjljOGQ1M2VkN2I1OGVjN2QiLCJwIjoiaiJ9)

---

#### 1. Vista General del Backlog en Jira

El backlog de AquaPort consolida la Épica, las Features funcionales y de documentación, junto con las 3 Historias de Usuario priorizadas, sumando un total estimado de **23 Story Points**:

![Backlog General en Jira](docs/images/JiraGeneral.png)

| Clave | Tipo | Resumen / Ítem | Épica / Padre | Estimación | Estado |
| :---: | :---: | :--- | :---: | :---: | :---: |
| **AQUA-1** | `Epic` | Digitalizar el monitoreo y reparto hídrico de la ECI... | — | — | Por hacer |
| **AQUA-2** | `Feature` | FEAT-01: Gestión de la flota de drones acuáticos del campus | AQUA-1 | — | Por hacer |
| **AQUA-3** | `Feature` | FEAT-02: Documentación y contratos de API con Swagger / OpenAPI | AQUA-1 | — | Por hacer |
| **AQUA-4** | `Story` | HU-01: Consulta de estado y telemetría de la flota acuática | FEAT-01 | **5 pts** | Por hacer |
| **AQUA-5** | `Story` | HU-02: Asignación manual de drone a misión de transporte | FEAT-01 | **13 pts** | Por hacer |
| **AQUA-6** | `Story` | HU-03: Cancelación de misión en estado pendiente | FEAT-01 | **5 pts** | Por hacer |

---

#### 2. Épica del MVP (AQUA-1)

* **Resumen:** `Digitalizar el monitoreo y reparto hídrico de la ECI mediante una flota de drones acuáticos supervisada`
* **Descripción:** Desarrollar la plataforma central MVP (AquaPort - Fase Piplup) que permita la coordinación, despacho, monitoreo de telemetría y supervisión de una flota de drones acuáticos encargados de transportar muestras hídricas y sensores entre los diferentes cuerpos de agua del campus de la Escuela Colombiana de Ingeniería Julio Garavito (Embalse Norte, Canal Central, Laguna Sur, Punto Ribereño Este y Laboratorio Hídrico).

![Épica en Jira](docs/images/JiraEpica.png)

---

#### 3. Features del Sistema

##### A. FEAT-01: Gestión de la flota de drones acuáticos del campus (AQUA-2)
Módulo funcional de supervisión y control operativo que permite consultar el inventario y estado de carga de la flota, asignar unidades a misiones de transporte hídrico y gestionar el ciclo de despacho básico para el Operador Hídrico.

![Feature 01 en Jira](docs/images/JiraFeat01.png)

##### B. FEAT-02: Documentación y contratos de API con Swagger / OpenAPI (AQUA-3)
Definición y publicación interactiva de los contratos de API REST mediante interfaces OpenAPI (SpringDoc) separadas por controlador, garantizando el contrato de integración formal entre frontend y backend conforme a las directrices de la guía de proyectos.

![Feature 02 en Jira](docs/images/JiraFeat02.png)

---

#### 4. Historias de Usuario (User Stories)

> **Criterio de Calidad DOSW:** Todas las historias se encuentran redactadas bajo la plantilla formal *`Como [rol], quiero [qué], para [beneficio]`*, utilizando exclusivamente roles humanos del dominio (`Operador Hídrico`) y evitando designar al "Sistema" como actor.

##### 📋 HU-01: Consulta de estado y telemetría de la flota acuática (AQUA-4 · 5 pts)
* **Narrativa:**  
  *Como* Operador Hídrico,  
  *quiero* visualizar la lista de drones de la flota con su identificador, nivel de batería, estado actual y zona asignada,  
  *para* evaluar la disponibilidad operativa de las unidades antes de despachar una nueva misión de transporte.
* **Criterios de Aceptación:**
  1. El sistema clasifica visualmente el estado de cada unidad entre DISPONIBLE, EN_MISION, RECARGANDO, MANTENIMIENTO y FALLO.
  2. Los drones con batería < 35% se presentan claramente como no aptos para despacho.
  3. La consulta se procesa en menos de 200 ms para la flota activa (RNF-01).

![HU 01 en Jira](docs/images/JiraHU01.png)

---

##### 📋 HU-02: Asignación manual de drone a misión de transporte (AQUA-5 · 13 pts)
* **Narrativa:**  
  *Como* Operador Hídrico,  
  *quiero* asignar manualmente un drone disponible a una solicitud de misión especificando puntos de partida, llegada y tipo de carga,  
  *para* despachar oportunamente el transporte de muestras de agua o sensores en los cuerpos hídricos del campus.
* **Criterios de Aceptación (Formato Gherkin / BDD):**
  - **Escenario 1 (Asignación válida):** Dado que el Operador Hídrico selecciona un drone registrado y en estado "DISPONIBLE" con batería $\ge 35\%$, cuando confirma el registro de la misión con datos válidos, entonces el sistema crea la instancia inmutable de la Misión con un ID alfanumérico único y actualiza el drone a "EN_MISION".
  - **Escenario 2 (Rechazo por RN-01 / RN-02):** Dado que el Operador Hídrico intenta asignar un drone con batería < 35% o en estado diferente a "DISPONIBLE", cuando solicita el registro, entonces el sistema rechaza la operación notificando el motivo en pantalla con la batería actual y la cota mínima requerida (35%).

![HU 02 en Jira](docs/images/JiraHU02.png)

###### Descomposición en Subtareas Técnicas por Capas (AQUA-7 a AQUA-12):
Siguiendo el template arquitectónico oficial (`Entidades` ➔ `DTOs` ➔ `Validator` ➔ `Service` ➔ `Controller` ➔ `Pruebas`), la HU-02 desglosa sus tareas técnicas con su respectiva Definition of Done (DoD) y evidencia requerida para su cierre:

![Subtareas Técnicas en Jira](docs/images/JiraSubTaks.png)

| Clave | Subtarea Técnica | Capa Arquitectónica | Estimación | Criterio de Aceptación / Done (DoD) |
| :---: | :--- | :---: | :---: | :--- |
| **AQUA-7** | `SUB-01: Entidades y records de dominio` | Dominio Puro | 2 pts | Clases inmutables `Mision` (con Builder) y `DroneAcuatico` en `model.domain`, compilando sin anotaciones de persistencia ni DTOs. |
| **AQUA-8** | `SUB-02: Request y Response DTOs con Bean Validation` | DTOs | 1 pt | `MisionRequestDTO` con validaciones de campos no nulos/no vacíos y `MisionResponseDTO` para la respuesta al cliente. |
| **AQUA-9** | `SUB-03: Implementar ValidadorMision con reglas de negocio` | Validación | 2 pts | `ValidadorMision` verificando batería $\ge 35\%$, estado `DISPONIBLE` y locaciones no vacías, arrojando excepciones tipadas. |
| **AQUA-10** | `SUB-04: Service de asignación con Streams y RegistradorMisiones` | Servicio / Negocio | 3 pts | Lógica desacoplada en `IMisionService` orquestando validador y persistencia en memoria, sin retornar DTOs directamente. |
| **AQUA-11** | `SUB-05: Controller REST y documentación Swagger OpenAPI` | Presentación / Docs | 2 pts | Interfaz `MisionApi` con anotaciones OpenAPI y `MisionController` con endpoint `POST /api/v1/misiones` retornando HTTP 201. |
| **AQUA-12** | `SUB-06: Pruebas unitarias con JUnit 5 y patrón AAA` | Pruebas Unitarias | 3 pts | Suite `ValidadorMisionTest` y `MisionServiceImplTest` con `@DisplayName` cubriendo casos positivos y negativos con JaCoCo $\ge 80\%$. |

---

##### 📋 HU-03: Cancelación de misión en estado pendiente (AQUA-6 · 5 pts)
* **Narrativa:**  
  *Como* Operador Hídrico,  
  *quiero* cancelar una misión de transporte que se encuentre en estado pendiente antes del inicio de navegación,  
  *para* liberar el drone asignado en caso de imprevistos ambientales o errores en la solicitud.
* **Criterios de Aceptación:**
  1. Solo se permite cancelar misiones en estado `PENDIENTE`. Si el drone ya está navegando (`EN_MISION`), el sistema rechaza la cancelación con código HTTP 422.
  2. Al confirmarse la cancelación, el drone retorna su estado inmediatamente a `DISPONIBLE`.

![HU 03 en Jira](docs/images/JiraHU03.png)

---

### Ejercicio 10 — Diagramas de Casos de Uso (AquaPort MVP)

En cumplimiento con los lineamientos formales de modelado de software de DOSW (**`DOSW_Equipo_Piplup.html`**), se diseñó el **Diagrama de Casos de Uso UML** para el requerimiento funcional principal de la fase Piplup (**`RF-01: Registrar misión de transporte`**), modelando la interacción con el sistema central `AquaPort` y formalizando las relaciones de inclusión obligatoria (`<<include>>`) y extensión condicional (`<<extend>>`).

#### 1. Diagrama de Casos de Uso del MVP

![Diagrama de Casos de Uso de AquaPort](docs/uml/DiagramCaseUse.png)

---

#### 2. Elementos del Diagrama

##### A. Límite del Sistema
* **Contenedor:** `AquaPort — Sistema Central MVP`. Delimita con precisión la frontera del software; todos los casos de uso (elipses) residen en su interior, mientras que los actores interactúan desde el exterior.

##### B. Actores (Roles Humanos del Dominio)
Conforme a las reglas de calidad de la asignatura, se modelan roles humanos funcionales y **nunca se utiliza "Sistema" como actor**:
1. **Operador Hídrico (Actor Principal):** Usuario técnico que opera la pantalla de monitoreo, consulta la telemetría en tiempo real, asigna drones a misiones de despacho y gestiona cancelaciones.
2. **Solicitante (Actor Secundario):** Investigador, docente o laboratorista del campus que genera solicitudes de transporte para muestras hídricas o sensores (`RF-02`).
3. **Administrador ECI (Actor de Supervisión):** Personal institucional que audita el estado general de los activos, revisa métricas operativas y supervisa el inventario de la flota (`RF-03`).

---

#### 3. Casos de Uso y Especificación de Relaciones

| Código | Caso de Uso | Actor Primario | Tipo / Relación | Regla / Condición |
| :---: | :--- | :---: | :---: | :--- |
| **CU-01** | **Registrar misión de transporte (RF-01)** | Operador Hídrico | Caso Base Principal | Permite formalizar un despacho fluvial acoplando drone, origen, destino y carga. |
| **CU-02** | **Consultar estado y telemetría de flota** | Operador Hídrico / Admin | Caso de Consulta | Lectura en tiempo real de batería, zona y estado de los drones. |
| **CU-03** | **Cancelar misión pendiente** | Operador Hídrico | Gestión de Ciclo | Aborta misiones que aún no han iniciado su navegación activa. |
| **CU-04** | **Solicitar transporte de muestra/sensor (RF-02)** | Solicitante | Caso de Entrada | Ingreso del requerimiento de transporte por parte del solicitante. |
| **CU-05** | **Supervisar inventario de flota y auditoría (RF-03)** | Administrador ECI | Caso de Control | Monitoreo administrativo de disponibilidad y registro de activos. |
| **CU-06** | **Validar disponibilidad y batería del drone** | Sistema Interno | **`<<include>>`** | **Obligatorio:** Se ejecuta SIEMPRE que se invoca `CU-01`. Sin verificar que el drone esté `DISPONIBLE` y con batería $\ge 35\%$, no se puede registrar la misión. |
| **CU-07** | **Alertar batería crítica** | Sistema Interno | **`<<extend>>`** | **Condicional:** `[si batería del drone < 40%]`. Advierte preventivamente al operador antes del despacho. |
| **CU-08** | **Notificar rechazo de asignación** | Sistema Interno | **`<<extend>>`** | **Condicional:** `[si batería < 35% o drone ocupado]`. Interrumpe la operación informando el motivo técnico exacto (RN-01 / RN-02). |

---

#### 4. Justificación de Buenas Prácticas UML Aplicadas

1. **Diferenciación estricta entre `<<include>>` y `<<extend>>`:**
   - **`<<include>>` (Sin condición):** La validación de disponibilidad y batería (`CU-06`) es una precondición ineludible. El caso base `CU-01` no puede completarse con éxito sin la ejecución de `CU-06`.
   - **`<<extend>>` (Condicional con regla explícita):** Tanto la alerta de batería crítica (`CU-07`) como la notificación de rechazo (`CU-08`) solo ocurren si se cumplen condiciones operativas específicas (`batería < 40%` o `batería < 35% / no disponible`). Si la condición no se cumple, el caso base procede normalmente sin ejecutar la extensión.
2. **Dirección formal de las relaciones:** En UML estándar, las flechas de `<<include>>` nacen en el caso base hacia el caso incluido, mientras que las de `<<extend>>` nacen en el caso de extensión y apuntan hacia el caso base que modifican o enriquecen.

---

### Ejercicio 11 — Mocks con IA: Los 3 Estados del Panel (AquaPort MVP)

En cumplimiento con el proceso metodológico de diseño de interfaces guiado por IA (**Referencias ➔ Identidad de Marca ➔ Datos del RF ➔ Prompt Estructurado**), se generaron las pantallas para los **tres estados operacionales fundamentales** del centro de monitoreo de AquaPort, asegurando coherencia visual absoluta con la paleta de identidad corporativa (`Abisal #0A1C26`, `Casco #12303D`, `Marea #1B7F8C`, `Sonar #7FD8C9`) y verificando las heurísticas de usabilidad de Jakob Nielsen.

#### 1. Prompt Estructurado Utilizado para la Generación con IA

Siguiendo la plantilla formal del reto, se utilizó el siguiente prompt base parametrizado para cada estado:

```text
Actúa como diseñador UX/UI senior de sistemas de monitoreo ambiental y marítimo.
SISTEMA: AquaPort — Panel de control de flota de drones acuáticos ECI (Escuela Colombiana de Ingeniería).
PANTALLA: Panel de monitoreo de la flota (vista principal del Operador Hídrico).
IDENTIDAD VISUAL: Paleta institucional modo oscuro: Fondo Abisal (#0A1C26), tarjetas Casco (#12303D), 
                  primario Marea (#1B7F8C), acento Sonar (#7FD8C9), texto Bruma (#E4EDEC). 
                  Tipografía IBM Plex Sans para interfaz y IBM Plex Mono para telemetría.
ACTOR: Operador Hídrico — requiere evaluar la capacidad operativa de un vistazo para despachar misiones.
DATOS POR DRONE: ID (AR-XX), nivel de batería (barra + %), estado (DISPONIBLE / EN_MISION / RECARGANDO / FALLO),
                 zona actual del campus (Embalse Norte, Canal Central, Laguna Sur, Punto Ribereño Este, Laboratorio Hídrico).
ACCIONES: Botón 'Asignar misión' (cyan primario), 'Ver misión' (secundario outline), 'Ver detalle' (en fallo).
COMPONENTES ADICIONALES: Radar topográfico/batimétrico circular del campus en panel derecho, barra de resumen superior con contadores y pie con timestamp de actualización.
ESTADO A GENERAR: [Normal | Alerta | Vacío]
HEURÍSTICAS NIELSEN: Visibilidad del estado (#1), Prevención de errores (#5), Diseño minimalista (#8), Ayuda ante errores (#9).
```

---

#### 2. Pantallas de los 3 Estados Operacionales

##### A. Estado Normal (Operación Estable de la Flota)
* **Escenario:** Flota con disponibilidad regular, sin fallos técnicos activos ni alertas rojas bloqueantes. Permite el despacho ágil e inmediato de misiones con drones en óptimo nivel de carga.
* **Métricas en Pantalla:** `2 disponible`, `1 en misión`, `1 recargando`, `0 fallo`.

![Estado Normal - Panel de Flota AquaPort](docs/images/AquaPort_Panel_Normal.png)

* **Heurísticas de Nielsen Aplicadas:**
  - **#1 Visibilidad del estado del sistema:** El operador constata inmediatamente qué unidades están listas (`AR-01` al 88% y `AR-03` al 92%), qué unidad está en tránsito (`AR-02`) y cuál está recargando en estación (`AR-04`).
  - **#8 Diseño estético y minimalista:** Sin banners de advertencia innecesarios; la jerarquía prioriza los datos de telemetría y la acción primaria destacada en color Marea/Sonar.

---

##### B. Estado Alerta (Incidencia Crítica y Diagnóstico de Fallo)
* **Escenario:** Anomalía de telemetría detectada en una unidad (`AR-04` en estado `FALLO`), coexistiendo con una unidad en recarga crítica (`AR-03` al 18%).
* **Métricas en Pantalla:** `1 disponible`, `1 en misión`, `1 recargando`, `1 fallo`.

![Estado Alerta - Panel de Flota AquaPort](docs/images/AquaPort_Panel_Alerta.png)

* **Heurísticas de Nielsen Aplicadas:**
  - **#1 Visibilidad del estado:** Banner rojo coral superior con mensaje de pérdida de señal desde las `10:42` y punto geográfico de revisión (`Punto Ribereño Este`).
  - **#5 Prevención de errores:** `AR-03` (18% batería) y `AR-04` (en fallo) tienen bloqueada la acción de asignación, imposibilitando el despacho accidental de unidades no aptas.
  - **#9 Ayudar a reconocer, diagnosticar y recuperarse de errores:** El banner diagnostica la hora exacta del fallo y la ubicación de inspección, mientras que la tarjeta de `AR-03` informa explícitamente: *"Batería insuficiente (18%). Mínimo requerido: 35%"*.

---

##### C. Estado Vacío / Empty State (Sin Unidades Disponibles para Asignación)
* **Escenario:** Todos los drones se encuentran ocupados en navegación activa o en ciclo de carga (`0 disponibles`). El operador no puede despachar misiones de inmediato pero debe conocer cuándo se liberará el próximo recurso.
* **Métricas en Pantalla:** `0 disponible`, `3 en misión`, `1 recargando`, `0 fallo`.

![Estado Vacío - Panel de Flota AquaPort](docs/images/AquaPort_Panel_Vacio.png)

* **Heurísticas de Nielsen Aplicadas:**
  - **#1 Visibilidad del estado:** Notificación horizontal en Sonar/Ámbar indicando con transparencia que no hay unidades libres pero informando la solución: *"Próxima unidad estimada: AR-04 (disponible en 12 min en Laboratorio Hídrico)"*.
  - **#5 Prevención de errores:** Todos los botones de despacho se encuentran deshabilitados ("En tránsito" / "Sin acción"), evitando clics inútiles o envíos frustrados.
  - **#6 Reconocimiento antes que recuerdo:** El operador ve en el radar las trayectorias activas y los tiempos estimados de cada misión sin tener que memorizar las horas de despacho previas.

---

#### 3. Matriz Comparativa de Heurísticas de Nielsen por Estado

| Heurística de Nielsen | Estado Normal | Estado Alerta | Estado Vacío (Empty State) |
| :--- | :---: | :---: | :---: |
| **#1 Visibilidad del estado** | Contadores verdes y radar despejado | Banner superior rojo con hora y zona de corte | Banner informativo con conteo `0 disponible` |
| **#2 Concordancia con el mundo real** | Terminología fluvial y nombres de campus | Nombres de zonas de inspección técnica | Tiempos de espera en minutos comprensibles |
| **#5 Prevención de errores** | Botón activo solo en drones $\ge 35\%$ | Deshabilitación visual al 55% de unidades no aptas | Todos los botones de asignación desactivados |
| **#8 Diseño minimalista** | Máximo contraste, cero ornamentos superfluos | El color rojo se reserva estrictamente al fallo | Espacios vacíos limpios con guía técnica clara |
| **#9 Recuperación de errores** | No aplica (no hay fallos activos) | Diagnóstico con sugerencia de intervención | Indicación de próximo drone disponible con hora |





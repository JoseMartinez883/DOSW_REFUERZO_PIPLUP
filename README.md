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

**Explicación:**  
Se modela la entidad con el record inmutable `DroneAcuatico` y se resuelven las consultas mediante la API de Streams:
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





# Changelog — AquaPort

Todas las modificaciones notables de este proyecto se documentan en este archivo.
El formato se basa en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/)
y el proyecto se adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [3.0.0-enterprise] - 2026-10-11
### Fase Empoleon — AquaPort Enterprise: Red Multi-Embalse

#### Added
- **Escalabilidad de Flota a 60 Drones (`GeneradorFlotaEmpoleon`):** Flota de 60 drones acuáticos distribuidos en 4 zonas operativas (15 Buceadores, 30 Superficiales, 15 Semisumergidos).
- **Red de 4 Zonas Hídricas Interconectadas (`ZonaHidrica`):** Embalse de Investigación (Norte), Red de Canales (Campus), Laguna de Reserva (Sur) y Laboratorio Hídrico Central.
- **Rutas Multi-Etapa con Waypoints (`Waypoint`, `TramoRuta`, `MisionEnterprise`):** Planificación de misiones complejas con puntos de control secuenciales y asignación de tramos.
- **Coordinación con Streams y Lambdas (`CoordinadorRutasEmpoleon`):**
  - Cálculo de ratios de eficiencia operativa por zona (`misiones completadas / totales`).
  - Detección autónoma del dron con mejor historial de entregas exitosas vía `reduce`.
  - Planificación y emparejamiento óptimo de drones disponibles por tramo correlativo.
  - Procesamiento paralelo de salud de flota (`parallelStream`) evaluando telemetría multivariable.
- **Custom Collector en Una Sola Pasada (`BateriaPromedioPorZonaCollector`):** Implementación de la interfaz `Collector` de Java para agrupar y promediar carga residual por zona en un único recorrido sin sobrecarga en memoria.
- **Gobernanza de Git y Hooks de Repositorio:**
  - Script `.githooks/commit-msg` para forzar formato estricto de Conventional Commits (`feat:`, `fix:`, `docs:`, etc.).
  - Script `.githooks/pre-commit` para impedir commits directos accidentales sobre ramas protegidas (`main`).
  - Scripts de instalación automatizada para entornos Windows PowerShell (`setup-hooks.ps1`) y Unix/macOS (`setup-hooks.sh`).

#### Changed
- **Arquitectura Limpia por Capas:** Estructuración del paquete `main.dosw.empoleon` en capas aisladas:
  - `dominio/`: Entidades, records y collectors sin dependencias externas (Java puro).
  - `aplicacion/`: Casos de uso y servicios de coordinación de rutas.
  - `infraestructura/`: Generadores de flota y adaptadores periféricos.

---

## [2.0.1] - 2026-10-11
### Fase Prinplup — Hotfix de Producción y Calidad SonarQube

#### Fixed
- **Hotfix de Batería Crítica (`hotfix/fix-bateria-critica`):** Implementación de salvaguarda operacional `UMBRAL_BATERIA_MINIMA = 20%` para mitigar riesgo de varamientos en misiones de transporte.
- **Remediación Integral de SonarQube:**
  - Reducción de Complejidad Cognitiva de 23 a 5 en `AsignadorAutomatico.java`.
  - Supresión de 4 chequeos redundantes de `null` sobre `Optional`.
  - Sustitución de `System.out.println` por `java.util.logging.Logger` en observers y suites de demostración.
  - Extracción de literales de cadena repetidos a constantes estáticas finales.
  - Eliminación de 47 issues totales alcanzando 0 Bugs, 0 Vulnerabilidades y 0 minutos de deuda técnica.

---

## [2.0.0] - 2026-10-11
### Fase Prinplup — AquaPort v2: Flota Autónoma con Alertas y Patrones

#### Added
- **Flota Especializada de 15 Drones (`DroneAcuatico`, `TipoDrone`):** Categorización técnica en `SUPERFICIAL`, `SEMISUMERGIDO` y `BUCEADOR`.
- **Integración de Patrones GoF:**
  - **Factory Method (`FabricaDrones`):** Instanciación polimórfica desacoplada de la jerarquía de drones.
  - **Strategy (`EstrategiaSeleccion`):** Políticas dinámicas de selección algorítmica (`MayorBateriaStrategy`, `ZonaCercanaStrategy`, `PrioridadCriticaStrategy`).
  - **Observer (`NotificadorAlertas`, `ObservadorMision`):** Emisión reactiva de alertas ante incidentes en tiempo real a `CentroControlObserver` y `TecnicoMantenimientoObserver`.
- **Análisis Estadístico con Streams (`ConsultorFlotaPrinplup`):** Agrupamientos por tipo, particionamiento binario de misiones críticas y reducción de promedios energéticos.
- **TDD y Cobertura Exhaustiva:** Quality Gate JaCoCo configurado al 80% mínimo en Maven, alcanzando 98% en clases de dominio.

---

## [1.0.0] - 2026-10-11
### Fase Piplup — AquaPort MVP

#### Added
- **MVP Inicial:** Flota homogénea de 4 drones acuáticos (`Aqua-Ranger 100`).
- **Gestión Básica:** Registro inmutable de misiones (`Mision`) y validación de reglas de negocio (`ValidadorMision`).
- **Pruebas Unitarias:** Suite JUnit 5 para validación de cargas, umbral de batería y disponibilidad.

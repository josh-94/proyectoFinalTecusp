# Diagramas C4

Diagramas de arquitectura del sistema siguiendo el **Modelo C4** (Simon Brown).
Estan escritos como **diagramas-como-codigo** en PlantUML puro, sin dependencias
externas ni necesidad de internet.

## Archivos

| Archivo | Nivel C4 | Que muestra |
|---------|----------|-------------|
| `c4_nivel1_contexto.puml` | Nivel 1 — Contexto | Actores del sistema y el sistema como caja negra. |
| `c4_nivel2_contenedores.puml` | Nivel 2 — Contenedores | App Flutter, gateway, microservicios, bases de datos y Kafka. |
| `c4_nivel3_componentes_orders.puml` | Nivel 3 — Componentes | Interior de `orders-service` con la arquitectura hexagonal. |

Cada `.puml` tiene su `.png` ya renderizado al lado.

## Como renderizar / editar

### Opcion A — VS Code (recomendada)
1. Instala la extension **PlantUML** (autor: jebbs).
2. Abre cualquier `.puml`.
3. `Alt + D` para previsualizar en vivo.
4. Para exportar: paleta de comandos -> `PlantUML: Export Current Diagram`.

> La extension necesita Java instalado. Si no quieres instalar Java, en la
> configuracion de la extension cambia `plantuml.render` a `PlantUMLServer`
> y usa `https://www.plantuml.com/plantuml` (requiere internet).

### Opcion B — Linea de comandos
```bash
java -jar plantuml.jar -tpng c4_nivel1_contexto.puml
java -jar plantuml.jar -tsvg c4_nivel2_contenedores.puml
```

## Convencion de colores

- Azul oscuro: personas / usuarios.
- Azul: aplicaciones y microservicios.
- Amarillo: capa de dominio (Nivel 3).
- Gris: sistemas o contenedores externos al elemento en foco.

## Por que C4

El modelo C4 describe la arquitectura en niveles de zoom progresivo
(Contexto -> Contenedor -> Componente -> Codigo). Cada nivel tiene una
audiencia distinta y evita mezclar detalles de implementacion con la vision
general. Para este proyecto se documentan los tres primeros niveles; el
Nivel 4 (Codigo) se considera cubierto por el propio repositorio.

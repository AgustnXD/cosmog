# The Cosmog — Mod de Minecraft Forge 1.20.1

Dimensión espacial **tranquila** con música relajante, un jefe, amuletos Curios y equipo astral.

## Cómo compilarlo
1. Requisitos: **JDK 17** y conexión a internet (Gradle descarga Forge y Curios).
2. Este proyecto no trae el *gradle wrapper*. Copia `gradlew`, `gradlew.bat` y la carpeta `gradle/` de un
   [Forge MDK 1.20.1-47.3.0](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.20.1.html)
   (o ejecuta `gradle wrapper --gradle-version 8.1.1`), o simplemente abre la carpeta con **IntelliJ IDEA** (importa Gradle).
3. `./gradlew build` → el `.jar` queda en `build/libs/`. Para probar: `./gradlew runClient`.
4. En el servidor/cliente final instala también **Curios API 5.x para 1.20.1** (dependencia obligatoria).
   Si Gradle no encuentra la versión de Curios, cambia `curios_version` en `gradle.properties`
   (versiones en https://maven.theillusivec4.top/).

## Cómo jugarlo
- **Portal cósmico** (bloque): colócalo donde quieras y haz clic derecho para viajar a The Cosmog. Receta:
  `O A O / A E A / O A O` con O = obsidiana, A = fragmento de amatista, E = ojo de ender.
  Al llegar aparece otro portal junto al punto de llegada para volver.
- **Llave astral** (portátil): `_ A _ / A E A / _ A _` (amatista + ojo de ender). Clic derecho viaja a The Cosmog y, estando allí, te devuelve a tu cama/spawn.
  (Ambas recetas usan solo materiales del mundo normal, para poder entrar en supervivencia.)
- La dimensión usa la isla flotante principal estilo End. El **santuario del Regulador de Cosmos** está en el centro (chunk 0,0).
  Si quieres comprobarlo: `/locate structure cosmog:regulator_shrine`.
- **Mena de polvo estelar** → fragmentos. 4 fragmentos + 1 lingote de oro → **Lingote astral**.
- El jefe suelta **Núcleo cósmico** (3-5), fragmentos y lingotes astrales.
- Comandos útiles: `/execute in cosmog:the_cosmog run tp @s 0 100 0`, `/give @s cosmog:astral_sword`.

## Contenido
| Objeto | Efecto / stats |
|---|---|
| Star Heart (nivel 1) | Regeneración I + Visión nocturna |
| Nova Heart (nivel 2) | Lo anterior + Velocidad + Resistencia al fuego |
| Cosmic Heart (nivel 3) | Absorción III, Velocidad III, Regeneración V, Resistencia II (+ visión nocturna y res. al fuego) |
| Astral Heart (nivel 4, extra) | Cosmic Heart + Fuerza II + Prisa II  *(la propuse yo; cámbiala en `ModItems.java`)* |
| Armadura astral | 3× netherite: defensa 9/24/18/9, dureza 9, durabilidad ×111, resist. empuje 0.3 |
| Espada astral | 40 de daño (Núcleo cósmico + 4 lingotes astrales + palo), 6093 usos, a prueba de fuego |

Los amuletos van en el slot **charm** de Curios. Recetas de amuletos: Star (diamante + 4 fragmentos),
Nova (Star + 4 lingotes + 4 fragmentos), Cosmic (Nova + 2 lingotes + 2 núcleos), Astral (Cosmic + 4 lingotes + 4 núcleos).

## Reglas de mobs
`EntityJoinLevelEvent` (ModEvents.java) cancela en The Cosmog cualquier mob hostil (`Enemy`) y cualquier mob de otros mods.
Solo el Regulador de Cosmos es la excepción. El bioma tampoco tiene spawns.

## Regenerar assets
`tools/` contiene los scripts Python que generan texturas, música (.ogg sintetizada), la estructura (.nbt) y los JSON.
Puedes reemplazar cualquier archivo en `src/main/resources` por tu propio arte/música
(la música es `assets/cosmog/sounds/the_cosmog.ogg`).


## v2 (rediseño)
- **Terreno propio** (`worldgen/noise_settings/cosmic_islands.json` + `density_function/`): islas flotantes de pasto oscuro,
  separadas por vacío, con forma de cono invertido. Se extienden sin límite en todas direcciones.
- **Cielo galáctico propio** (`client/GalaxyEffects.java` + `textures/environment/galaxy_sky.png`).
- **Isla del jefe**: el Regulador vive sobre su propia isla (y=128) con un santuario. La llave/portal te dejan en ella, a 20 bloques.
  Si caes al vacío (y<8) te devuelve a esa isla.
- **Jefe**: traje negro, corbata morada y cabeza de agujero negro con disco de acreción (`AccretionDiskLayer`).
- **Discos de música**: Cosmog, Starfall, Nebula, Event Horizon (los suelta el jefe, uno al azar).
- La música de la dimensión ahora **reemplaza** la que esté sonando al entrar.

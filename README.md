# Koro Tetris (Android 15+)

Tetris para Android 15 o superior (minSdk 35) con:
- **29 niveles** con la velocidad de caída del Tetris de NES (fotogramas por fila de la NES; el nivel 29 usa la velocidad de 1 fotograma, el famoso "kill screen").
- Subida de nivel cada 10 líneas (con la regla de la NES si empiezas en un nivel alto) y puntuación NES: 40 / 100 / 300 / 1200 × nivel.
- **Efecto especial en cada línea completada**: destello, explosión de partículas, sacudida y texto (¡LÍNEA!, ¡DOBLE!, ¡TRIPLE!, ¡TETRIS! en arcoíris).
- **Felicitación en cada nivel**: banner "¡FELICIDADES! NIVEL N", fuegos artificiales, confeti, fanfarria y **vibración**. Celebraciones especiales en los niveles 10, 13, 16, 19, 25 y 29.
- **Música: Korobéiniki** (canción popular rusa de 1861, dominio público), sintetizada en tiempo real; el tempo sube un poco con cada nivel.
- Controles táctiles con auto-repetición, gestos sobre el tablero, pieza siguiente, sombra opcional y récord guardado.

## Compilar el APK

### Opción A: GitHub (sin instalar nada)
1. Crea un repositorio nuevo en github.com (puede ser privado).
2. Sube todo el contenido de esta carpeta (incluida la carpeta oculta `.github`). Desde la web: **Add file → Upload files** y arrastra los archivos y carpetas.
3. Ve a la pestaña **Actions**. El flujo "Compilar APK" se ejecuta solo (unos 3–5 min). Si no arranca, entra en él y pulsa **Run workflow**.
4. Al terminar, abre la ejecución y descarga **KoroTetris-APK** en "Artifacts". Es un .zip que contiene `KoroTetris.apk`.

### Opción B: Android Studio
1. Instala Android Studio (versión Ladybug o más reciente).
2. **File → Open** y elige esta carpeta. Espera a que termine la sincronización de Gradle.
3. **Build → Build App Bundle(s) / APK(s) → Build APK(s)**.
4. El APK queda en `app/build/outputs/apk/debug/app-debug.apk`.

Desde la terminal: `./gradlew assembleRelease` → `app/build/outputs/apk/release/app-release.apk`.

## Controles
| Acción | Botón | Gesto en el tablero |
|---|---|---|
| Mover | ◀ ▶ (mantén pulsado para repetir) | Deslizar a izquierda o derecha |
| Rotar | ⟳ / ⟲ | Tocar |
| Bajar rápido | ▼ BAJAR | Arrastrar hacia abajo despacio |
| Caída instantánea | ⤓ CAÍDA | Deslizar rápido hacia abajo |
| Pausa | ❚❚ o gesto "atrás" | |

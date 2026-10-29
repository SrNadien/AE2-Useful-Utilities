# Changelog

## 1.0.3 — Assembler Matrix, selector mejorado y existencias en JEI

### Selector del Pattern Uploader

- Botón ★ en cada fila para fijar un proveedor arriba (también con clic derecho en la fila). Los fijados se
  guardan en `config/ae2usefulutilities/pinned_providers.json`.
- Botones ◀ ▶ de página con indicador `Página X/Y`, y número de filas adaptado al alto de la pantalla.
- Botón ✕ junto al buscador para limpiarlo (también con clic derecho en el buscador).
- Orden natural: `Horno 2` va antes que `Horno 10`.

### Existencias de la red en JEI

- Nuevo botón junto al de historial de JEI que muestra sobre cada objeto de la lista de JEI cuántos tiene tu red
  ME, o `Crear` si hay patrón pero no existencias. Funciona con una terminal ME abierta o con cualquier terminal
  inalámbrico enlazado y con carga que lleves en el inventario o en Curios, en cualquier pantalla con JEI.
- Se sincroniza solo mientras el botón está activo. Configurable en `networkStock` (activado y ticks entre
  actualizaciones) y en el cliente en `jei.showNetworkStock`.

### Craftear desde JEI y desde el mundo

- `Ctrl` + `Shift` + rueda del ratón (o clic izquierdo o clic de rueda) sobre un objeto de la lista de JEI, de los marcadores,
  de una terminal ME o de cualquier ranura abre la pantalla de petición de fabricación de AE2 para ese objeto.
- Apuntando a un bloque colocado, `Ctrl` + `Shift` + clic de la rueda del ratón hace lo mismo con el objeto del
  bloque.
- Usa la terminal abierta o cualquier terminal inalámbrico con carga (inventario, mano secundaria o Curios).

### Compatibilidad con JEI propia

- El addon vuelve a incluir la compatibilidad de AE2 con JEI que se quitó en 1.21.1, así que ya no hace falta
  AE2 JEI Integration para esto: botón `+` en la Pattern Encoding Terminal y en las terminales de crafteo
  (incluido `Ctrl` + clic para pedir a la red lo que falte), arrastrar ingredientes de JEI a las ranuras
  fantasma de AE2, zonas de exclusión de las pantallas de AE2 y ver recetas de lo que hay bajo el ratón en las
  terminales ME.
- El `+` funciona también en las terminales inalámbricas de AE2 y en las de ae2wtlib (crafteo y codificación de
  patrones), y el buscador de JEI se sincroniza con el de las terminales de AE2.
- Si AE2 JEI Integration está instalado, el addon deja que sea ese mod quien registre todo esto.
- También vuelven las categorías de recetas de AE2 en JEI (Inscriber, Charger, Condenser, Attunement, Certus
  Growth, Entropy Manipulator y Transform), con sus catalizadores, la zona clicable de la pantalla del Inscriber,
  las recetas de facetas, los subtipos de objetos con energía, los textos de ayuda de las prensas y la Crank, y
  sin las herramientas de depuración en la lista.

### Soporte para ExtendedAE Assembler Matrix

- El Pattern Uploader ahora lista y rellena cualquier contenedor de patrones de la red, no solo Pattern
  Providers. Incluye los Assembler Matrix Pattern de [ExtendedAE](https://github.com/GlodBlock/ExtendedAE), que
  aparecen en el selector con sus 36 huecos y se rellenan en banco como el resto de proveedores del mismo nombre.
- La matriz solo acepta patrones de crafteo, herrería y cortapiedras; el resto se queda en el inventario.

## 1.0.2 — Pattern Uploader y Pattern Restocker

Dos funciones nuevas para la Pattern Encoding Terminal, inspiradas en
[Pattern-Uploader](https://github.com/link-fgfgui/Pattern-Uploader),
[ae2-pattern-restocker](https://github.com/sawces/ae2-pattern-restocker) y
[ExtendedAE_Plus](https://github.com/GaLicn/ExtendedAE_Plus).

### Nuevo

**Pattern Uploader**

- Botón de flecha ↑ junto al botón Encode de la Pattern Encoding Terminal. También funciona con `ALT` + Encode.
- Abre un selector con todos los Pattern Providers de la red. Cada fila muestra el nombre que usa la Pattern
  Access Terminal, los huecos libres y cuántos proveedores agrupa: `Molecular Assembler (24) x8`.
- Los proveedores idénticos se colapsan en una sola fila. Si le pones nombre propio a uno con un yunque, se
  separa del grupo y puedes dirigirle patrones concretos.
- Sube el patrón que esté en la ranura de salida de la terminal **y** todos los patrones codificados que lleves
  en el inventario.
- Si el proveedor elegido se llena, el resto sigue entrando en los demás proveedores con el mismo nombre, así
  que elegir uno de un banco reparte entre todo el banco.
- Buscador con filtrado en vivo, scroll con la rueda, botones de página y `Enter` para subir directamente
  cuando el filtro deja un único resultado.
- El buscador viene prellenado con la máquina del primer patrón que llevas, si JEI puede resolverla.

**Pattern Restocker**

- Al codificar con la ranura de patrones en blanco vacía, saca automáticamente hasta 64 patrones en blanco del
  almacenamiento ME.
- Botón ✕ junto a la ranura del patrón codificado que lo devuelve a blanco. El patrón en blanco va primero a su
  ranura, luego al ME y, si no cabe, a tu inventario.

**Integración con JEI**

- Al transferir una receta desde JEI a la terminal, el patrón guarda la categoría de receta de la que salió.
- Los patrones codificados muestran en el tooltip la máquina que los fabrica: `Se fabrica en Horno de arco`.
- Requiere [AE2 JEI Integration](https://www.curseforge.com/minecraft/mc-mods/ae2-jei-integration), que es quien
  aporta el handler de transferencia de AE2 en 1.21.1. Sin él, el subidor y el selector siguen funcionando; solo
  se pierden la etiqueta de categoría, el tooltip y el prellenado del buscador.

**Configuración**

- Nueva sección `pattern.uploader`: `enabled`, `blacklistedCategories`.
- Nueva sección `pattern.restocker`: `autoRestock`, `returnButton`.

**Traducciones**

- Textos nuevos en inglés, español de España y español de México.

### Corregido

- El selector de proveedores se abría vacío: solo se veían el fondo y el título. `EditBox.setValue` dispara el
  responder al instante, y este se llamaba antes de que existieran los botones de fila, así que `refreshRows()`
  lanzaba `IndexOutOfBoundsException` y `init()` moría sin añadir ningún widget.
- El subidor ignoraba el patrón que estaba en la ranura de salida de la terminal y solo miraba el inventario, de
  modo que justo después de codificar no encontraba nada que subir.
- Un Pattern Provider de otro mod que fallara al dar su nombre o su inventario tumbaba la lista entera. Ahora
  cada proveedor se enumera en su propio bloque protegido y los demás siguen apareciendo.
- Errores heredados del código original portado: un `Item.byId(BuiltInRegistries.ITEM.getId(...))` que no
  identificaba el patrón de procesado, una comprobación de inserción por identidad de `ItemStack` en vez de por
  contenido, mapas estáticos de proveedores compartidos entre jugadores, y una caché de máquinas por categoría
  que nunca se refrescaba por usar `computeIfAbsent`.

### Modificado

- El enrutado automático por categoría de receta y por bloque adyacente se sustituyó por la selección explícita
  de proveedor. El automático fallaba en cuanto el patrón no traía etiqueta de categoría o el proveedor no
  tocaba una máquina; el selector funciona siempre.
- `build.gradle`: el jar completo de JEI pasa a `compileOnly`, porque `mezz.jei.common.transfer.RecipeTransferUtil`
  no está en el artefacto `-api`. Añadida la dependencia de AE2 JEI Integration.
- El mixin de JEI comprueba la presencia del mod con `FMLLoader.getLoadingModList()` en vez de con
  `Class.forName` sobre la clase objetivo, que podía cargarla antes de que Mixin llegara a transformarla.

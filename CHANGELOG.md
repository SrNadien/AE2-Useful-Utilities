# Changelog

## 4.0.0 — Assembler Matrix, selector mejorado y existencias en JEI

Port a 26.1.2 de las funciones añadidas en la rama 1.21.1.

Port a 26.3.

### Selector del Pattern Uploader

- Botón ★ en cada fila para fijar un proveedor arriba (también con clic derecho en la fila). Los fijados se
  guardan en `config/ae2usefulutilities/pinned_providers.json`.
- Botones ◀ ▶ de página con indicador `Página X/Y`, y número de filas adaptado al alto de la pantalla.
- Botón ✕ junto al buscador para limpiarlo (también con clic derecho en el buscador).
- Orden natural: `Horno 2` va antes que `Horno 10`.
- El buscador viene prellenado con la máquina del primer patrón que llevas, si JEI puede resolverla.

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

### Integración con JEI

- Al transferir una receta desde JEI a la Pattern Encoding Terminal, el patrón guarda la categoría de receta de
  la que salió.
- Los patrones codificados muestran en el tooltip la máquina que los fabrica: `Se fabrica en Horno de arco`.
- Todo esto solo se activa si JEI está instalado. La compatibilidad de AE2 con JEI (categorías, botón `+`,
  ingredientes fantasma) la aporta el propio AE2.

### Soporte para ExtendedAE Assembler Matrix

- El Pattern Uploader ahora lista y rellena cualquier contenedor de patrones de la red, no solo Pattern
  Providers. Incluye los Assembler Matrix Pattern de [ExtendedAE](https://github.com/GlodBlock/ExtendedAE), que
  aparecen en el selector con sus 36 huecos y se rellenan en banco como el resto de proveedores del mismo nombre.
- La matriz solo acepta patrones de crafteo, herrería y cortapiedras; el resto se queda en el inventario.

### Notas del port a la API de 26.1

- `Screen.mouseClicked(double, double, int)` pasa a `mouseClicked(MouseButtonEvent, boolean)`, con el botón en
  `event.button()` y la posición en `event.x()` / `event.y()`.
- `Screen.hasControlDown()` / `hasShiftDown()` desaparecen; ahora son `Minecraft.getInstance().hasControlDown()` /
  `hasShiftDown()`.
- `Item.appendHoverText` pasa a recibir `TooltipDisplay` y un `Consumer<Component>` en vez de la lista de líneas.
- `CompoundTag.getString(key)` devuelve `Optional`; se usa `getStringOr(key, "")`.
- `ServerPlayer.displayClientMessage(msg, true)` pasa a `sendOverlayMessage(msg)`.
- `Block.getCloneItemStack` pasa a `(LevelReader, BlockPos, BlockState, boolean, Player)`.
- `FMLLoader.getLoadingModList()` pasa a `FMLLoader.getCurrent().getLoadingModList()`.
- JEI 26.1.2: `IngredientListRenderer` vive en `mezz.jei.gui.overlay.ingredients`; `BookmarkOverlay.updateBounds`
  recibe también el conjunto de zonas de exclusión y los botones se dibujan en `drawForeground`; el dibujado de
  botones usa `Textures.getButton*()` en vez de `getButtonForState`; `RecipeTransferUtil.transferRecipe` pasó a
  `RecipeTransferService.transferRecipe(AbstractContainerScreen, ...)`; `RecipeType` pasa a `IRecipeType` y
  `createRecipeCatalystLookup` a `createCraftingStationLookup`.

## 2.1.0 — Pattern Uploader y Pattern Restocker

Port a 26.1.2 de las funciones añadidas en la rama 1.21.1. Inspiradas en
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
- Si el proveedor elegido se llena, el resto sigue entrando en los demás proveedores con el mismo nombre.
- Buscador con filtrado en vivo, scroll con la rueda, botones de página y `Enter` para subir directamente
  cuando el filtro deja un único resultado.

**Pattern Restocker**

- Al codificar con la ranura de patrones en blanco vacía, saca automáticamente hasta 64 patrones en blanco del
  almacenamiento ME.
- Botón ✕ junto a la ranura del patrón codificado que lo devuelve a blanco. El patrón en blanco va primero a su
  ranura, luego al ME y, si no cabe, a tu inventario.

**Configuración**

- Nueva sección `pattern.uploader`: `enabled`.
- Nueva sección `pattern.restocker`: `autoRestock`, `returnButton`.

**Traducciones**

- Textos nuevos en inglés, español de España y español de México.

### Diferencias respecto a la versión de 1.21.1

- No se portó la integración con JEI (etiqueta de categoría de receta, tooltip "Se fabrica en X" y prellenado
  del buscador con el nombre de la máquina). Este proyecto no declara JEI ni AE2 JEI Integration como
  dependencias, y el selector de proveedores no las necesita para funcionar.

### Notas del port a la API de 26.1

Minecraft y AE2 movieron bastantes cosas entre 1.21.1 y 26.1.2. Lo que hubo que adaptar:

- `ResourceLocation` pasa a ser `net.minecraft.resources.Identifier`, y `FriendlyByteBuf.writeResourceLocation` /
  `readResourceLocation` a `writeIdentifier` / `readIdentifier`.
- El pipeline de GUI cambió: `Screen.render(GuiGraphics, ...)` pasa a
  `Screen.extractRenderState(GuiGraphicsExtractor, ...)`, y `drawCenteredString` desaparece en favor de
  `guiGraphics.text(font, texto, x, y, argb, sombra)` con centrado manual. Los colores ahora son ARGB.
- `Screen.keyPressed(int, int, int)` pasa a `keyPressed(KeyEvent)`, con la tecla en `event.key()`.
- `Screen.hasAltDown()` desaparece; ahora es `Minecraft.getInstance().hasAltDown()`.
- `PacketDistributor.sendToServer` pasa a `ClientPacketDistributor.sendToServer`, en
  `net.neoforged.neoforge.client.network`.
- `FMLEnvironment.dist` pasa a `FMLEnvironment.getDist()`.
- `Level.dimension().location()` pasa a `dimension().identifier()`.
- `Inventory.items` pasa a `Inventory.getNonEquipmentItems()`.
- En AE2, `registerClientAction` / `sendClientAction` ya no reciben un `String` sino un
  `appeng.menu.guisync.ClientActionKey<T>`, y el enum `Icon` se movió de `appeng.client.gui` a `appeng.util`.

`AbstractContainerScreen.getGuiLeft()` y `getGuiTop()` siguen funcionando pero están marcados como obsoletos y
pendientes de eliminación; habrá que cambiarlos por accesores a `leftPos` / `topPos` en una versión futura.

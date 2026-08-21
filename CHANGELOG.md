# Changelog

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

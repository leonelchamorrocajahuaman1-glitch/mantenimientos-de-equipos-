# MobileStore

Catálogo responsive de celulares con carrito, clientes, ventas e inventario. El proyecto está preparado para ejecutarse desde Apache NetBeans.

## Ejecutar en Apache NetBeans

1. Abre la carpeta `mavenproject1` como proyecto Maven.
2. Ejecuta el proyecto con **Run Project (F6)**.
3. La clase `Mavenproject1` inicia un servidor local y abre `http://localhost:8080`.
4. Para detenerlo, pulsa **Stop** en NetBeans.

El servidor Java solo sirve los archivos estáticos de la raíz; la lógica de la tienda continúa implementada en `script.js`.

## Módulo 07: Control de mantenimiento

El paquete `com.mycompany.mavenproject1.mantenimiento` implementa `Equipo`, `OrdenMantenimiento`, `Tecnico`, `Historial` mediante consultas del servicio, y `MantenimientoService`. Incluye órdenes preventivas/correctivas, cierre con fecha y descripción obligatorias, rechazo de preventivas abiertas duplicadas por equipo y fecha, y consulta de próximos vencimientos.

Para ejecutar la prueba de aceptación en NetBeans: clic derecho en `MantenimientoDemo.java` y selecciona **Run File**.

## Funcionalidades

- Búsqueda y filtros por Apple, Samsung y Xiaomi.
- Carrito con cantidades y validación de stock.
- Registro de clientes e historial de ventas.
- Ventas con varios productos y precio unitario histórico en cada detalle.
- Descuento de 5% desde 2 unidades del mismo producto, aplicado antes del IGV (18%).
- Validación completa antes de descontar inventario: no hay ventas parciales.
- Importes calculados en centavos enteros y redondeados a dos decimales.
- Persistencia de productos, stock, clientes, carrito y ventas en `localStorage`.

El iPhone 15 inicia con stock 3. Vender 2 deja 1; intentar vender 2 nuevamente falla sin descontar inventario ni crear una venta.

## Publicación

El workflow `.github/workflows/pages.yml` publica los archivos estáticos en GitHub Pages al hacer push a `main`.

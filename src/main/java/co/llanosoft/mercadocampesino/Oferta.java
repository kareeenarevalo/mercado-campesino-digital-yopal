package co.llanosoft.mercadocampesino;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Oferta de un producto para una jornada concreta del mercado.
 * RF-06 y RF-07: protege inventario y no acepta reservas al llegar al cierre.
 * Demostración en memoria; en producción el descuento debe ser transaccional en la BD.
 */
public final class Oferta {
    private final String id;
    private final String producto;
    private final BigDecimal precioUnitario;
    private final LocalDateTime cierreReserva;
    private int cantidadDisponible;

    public Oferta(String id, String producto, BigDecimal precioUnitario,
                  int cantidadDisponible, LocalDateTime cierreReserva) {
        this.id = Objects.requireNonNull(id, "id es obligatorio");
        this.producto = Objects.requireNonNull(producto, "producto es obligatorio");
        this.precioUnitario = Objects.requireNonNull(precioUnitario, "precio es obligatorio");
        this.cierreReserva = Objects.requireNonNull(cierreReserva, "cierre es obligatorio");
        if (precioUnitario.signum() < 0) throw new IllegalArgumentException("El precio no puede ser negativo");
        if (cantidadDisponible < 0) throw new IllegalArgumentException("El inventario inicial no puede ser negativo");
        this.cantidadDisponible = cantidadDisponible;
    }

    /** RF-06/RF-07: valida el cierre y descuenta una cantidad atómicamente en esta instancia. */
    public synchronized void reservar(int cantidad, LocalDateTime ahora) {
        Objects.requireNonNull(ahora, "La fecha actual es obligatoria");
        if (!ahora.isBefore(cierreReserva)) {
            throw new IllegalStateException("Las reservas para esta jornada están cerradas");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
        if (cantidad > cantidadDisponible) {
            throw new IllegalStateException("Cantidad insuficiente; disponibilidad actual: " + cantidadDisponible);
        }
        cantidadDisponible -= cantidad;
    }

    public String getId() { return id; }
    public String getProducto() { return producto; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public LocalDateTime getCierreReserva() { return cierreReserva; }
    public synchronized int getCantidadDisponible() { return cantidadDisponible; }
}

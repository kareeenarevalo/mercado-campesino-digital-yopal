package co.llanosoft.mercadocampesino;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

/** Servicio pequeño para hacer reproducibles las pruebas de fecha. */
public final class ServicioReservas {
    private static final ZoneId ZONA_COLOMBIA = ZoneId.of("America/Bogota");
    private final Clock reloj;

    public ServicioReservas(Clock reloj) {
        this.reloj = Objects.requireNonNull(reloj, "El reloj es obligatorio").withZone(ZONA_COLOMBIA);
    }

    /** RF-07: la regla usa hora de Colombia; a las 18:00:00 del viernes ya no se acepta. */
    public void reservar(Oferta oferta, int cantidad) {
        Objects.requireNonNull(oferta, "La oferta es obligatoria");
        LocalDateTime ahoraColombia = LocalDateTime.now(reloj);
        oferta.reservar(cantidad, ahoraColombia);
    }
}

package co.llanosoft.mercadocampesino;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

class OfertaTest {
    private static final ZoneId BOGOTA = ZoneId.of("America/Bogota");
    private static final LocalDateTime CIERRE = LocalDateTime.of(2026, 10, 30, 18, 0);
    private Oferta oferta(int unidades) {
        return new Oferta("OF-1", "Queso llanero", new BigDecimal("18000"), unidades, CIERRE);
    }
    private ServicioReservas servicio(String instanteUtc) {
        return new ServicioReservas(Clock.fixed(Instant.parse(instanteUtc), BOGOTA));
    }

    @Test void reservaValidaDescuentaInventario() {
        Oferta o = oferta(5);
        servicio("2026-10-30T22:00:00Z").reservar(o, 2); // 17:00 en Colombia
        assertEquals(3, o.getCantidadDisponible());
    }

    @Test void cantidadCeroEsRechazadaSinCambiarInventario() {
        Oferta o = oferta(5);
        assertThrows(IllegalArgumentException.class,
                () -> servicio("2026-10-30T22:00:00Z").reservar(o, 0));
        assertEquals(5, o.getCantidadDisponible());
    }

    @Test void cantidadMayorQueDisponibilidadEsRechazada() {
        Oferta o = oferta(2);
        assertThrows(IllegalStateException.class,
                () -> servicio("2026-10-30T22:00:00Z").reservar(o, 3));
        assertEquals(2, o.getCantidadDisponible());
    }

    @Test void exactamenteALasSeisDeLaTardeYaEstaCerrada() {
        Oferta o = oferta(5);
        assertThrows(IllegalStateException.class,
                () -> servicio("2026-10-30T23:00:00Z").reservar(o, 1));
        assertEquals(5, o.getCantidadDisponible());
    }

    @Test void dosClientesNoPuedenTomarLaUltimaUnidad() throws InterruptedException {
        Oferta o = oferta(1);
        ServicioReservas s = servicio("2026-10-30T22:00:00Z");
        CountDownLatch inicio = new CountDownLatch(1);
        AtomicInteger exitos = new AtomicInteger();
        Runnable intento = () -> {
            try { inicio.await(); s.reservar(o, 1); exitos.incrementAndGet(); }
            catch (IllegalStateException esperado) { /* el segundo debe quedar rechazado */ }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        };
        Thread a = new Thread(intento); Thread b = new Thread(intento);
        a.start(); b.start(); inicio.countDown(); a.join(); b.join();
        assertEquals(1, exitos.get());
        assertEquals(0, o.getCantidadDisponible());
    }
}

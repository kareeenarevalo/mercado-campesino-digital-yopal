import co.llanosoft.mercadocampesino.Oferta;
import co.llanosoft.mercadocampesino.ServicioReservas;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class VerificacionLocal {
    private static final ZoneId BOGOTA = ZoneId.of("America/Bogota");
    private static final LocalDateTime CIERRE = LocalDateTime.of(2026,10,30,18,0);
    private static Oferta nueva(int n) { return new Oferta("OF-1","Queso llanero",new BigDecimal("18000"),n,CIERRE); }
    private static ServicioReservas servicio(String utc) { return new ServicioReservas(Clock.fixed(Instant.parse(utc),BOGOTA)); }
    private static void comprobar(boolean condicion, String nombre) {
        if (!condicion) throw new AssertionError(nombre);
        System.out.println("PASS " + nombre);
    }
    public static void main(String[] args) throws Exception {
        Oferta o1=nueva(5); servicio("2026-10-30T22:00:00Z").reservar(o1,2);
        comprobar(o1.getCantidadDisponible()==3,"CP-01 reserva valida descuenta stock");
        Oferta o2=nueva(5); boolean cero=false;
        try { servicio("2026-10-30T22:00:00Z").reservar(o2,0); } catch (IllegalArgumentException e) { cero=true; }
        comprobar(cero && o2.getCantidadDisponible()==5,"CP-02 cantidad cero rechazada sin descuento");
        Oferta o3=nueva(2); boolean exceso=false;
        try { servicio("2026-10-30T22:00:00Z").reservar(o3,3); } catch (IllegalStateException e) { exceso=true; }
        comprobar(exceso && o3.getCantidadDisponible()==2,"CP-04 no permite sobre-reserva");
        Oferta o4=nueva(5); boolean cierre=false;
        try { servicio("2026-10-30T23:00:00Z").reservar(o4,1); } catch (IllegalStateException e) { cierre=true; }
        comprobar(cierre && o4.getCantidadDisponible()==5,"CP-05 cierre exacto 18:00 Colombia");
        Oferta o5=nueva(1); ServicioReservas s=servicio("2026-10-30T22:00:00Z");
        CountDownLatch inicio=new CountDownLatch(1); AtomicInteger exitos=new AtomicInteger();
        Runnable r=()->{ try { inicio.await(); s.reservar(o5,1); exitos.incrementAndGet(); } catch(IllegalStateException esperado) {} catch(InterruptedException e){Thread.currentThread().interrupt();} };
        Thread a=new Thread(r); Thread b=new Thread(r); a.start(); b.start(); inicio.countDown(); a.join(); b.join();
        comprobar(exitos.get()==1 && o5.getCantidadDisponible()==0,"CP-07 solicitudes simultaneas para ultima unidad");
        System.out.println("RESULTADO: 5/5 verificaciones locales superadas.");
    }
}

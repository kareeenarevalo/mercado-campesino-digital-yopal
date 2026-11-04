# Mercado Campesino Digital de Yopal (MCDY) — proyecto académico

Este repositorio es una base de trabajo para el taller de SDLC. El caso del mercado es ficticio según el enunciado de clase; las cantidades, métricas objetivo y arquitectura que no están en el enunciado son propuestas académicas, no mediciones reales de la Secretaría.

## Requisitos
- JDK 21
- Maven 3.9 o una versión compatible
- Conexión a Internet la primera vez para descargar dependencias de Maven

## Ejecutar pruebas JUnit
```bash
mvn --batch-mode verify
```

## Verificación local sin dependencias externas
El entorno de preparación no incluye Maven y no pudo acceder a Maven Central. La regla del dominio se compiló y ejecutó con un verificador JDK sin dependencias:
```bash
mkdir -p target/manual
javac -d target/manual src/main/java/co/llanosoft/mercadocampesino/*.java tools/VerificacionLocal.java
java -cp target/manual VerificacionLocal
```
La salida reproducida está en `evidencia/prueba_local.txt`. Esta salida no se presenta como ejecución de JUnit; las pruebas JUnit están en `src/test/java` y se ejecutan con Maven o con GitHub Actions una vez publicado el repositorio.

## Estructura
- `src/main/java/...`: regla de negocio de reserva de ejemplo.
- `src/test/java/...`: cinco pruebas JUnit.
- `docs/`: diagramas y prototipos del informe.
- `tools/VerificacionLocal.java`: verificador local sin dependencias.
- `.github/workflows/tests.yml`: ejecución automática de pruebas en GitHub Actions.
- `pom.xml`: configuración Maven.

## Regla de inventario en producción
El método `synchronized` solo protege esta instancia en un proceso. Para el sistema real, el descuento debe implementarse con transacción y actualización condicional de inventario en la base de datos (por ejemplo, `UPDATE ... WHERE cantidad_disponible >= :cantidad`) para evitar sobreventa si existen varias instancias del servicio.

## Estrategia Git
- `main`: versiones aprobadas.
- `develop`: integración del sprint.
- `feature/rf-xx-descripcion`: una rama por funcionalidad.
- Cada integrante debe hacer al menos un commit con su propia cuenta, como exige el taller.
- No cargar datos personales reales ni credenciales; usar datos sintéticos para pruebas.

## Publicación del repositorio
Esta carpeta está lista para subir, pero **no se ha creado ni publicado un repositorio remoto desde aquí**. Crear uno con nombre `mercado-campesino-digital-yopal`, agregar el remoto que GitHub indique, y subir las ramas conforme a las instrucciones del informe.

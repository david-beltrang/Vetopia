package com.vetopia.e2e;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import io.github.bonigarcia.wdm.WebDriverManager;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class NuevoMedicamentoTest {

    private static final String BASE_URL = "http://localhost:4200";
    private static final long VETERINARIO_ID = 2L;
    private static final String NOMBRE_MASCOTA = "Rex";
    private static final String RAZA_MASCOTA = "Bulldog Francés";
    private static final String TITULO_TRATAMIENTO = "tratamiento test con ACOLAN";
    private static final String DESCRIPCION_TRATAMIENTO = "descripcion test con ACOLAN";

    // Valores iniciales del perfil de prueba y del inventario de drogas.
    private static final long GANANCIAS_INICIALES = 3_649_820L;
    private static final long PRECIO_VENTA_ACOLAN = 151_300L;
    private static final long COSTO_ACOLAN = 60_520L;
    private static final long TRATAMIENTOS_INICIALES = 80L;

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void init() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--disable-notifications");
        chromeOptions.addArguments("--disable-extensions");
        // chromeOptions.addArguments("--headless");

        driver = new ChromeDriver(chromeOptions);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testAgregarNuevoMedicamentoExitoso() {
        buscarMascotaAux(NOMBRE_MASCOTA, RAZA_MASCOTA);

        // Deja la mascota activa para poder asignarle el tratamiento.
        WebElement estado = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("estadoPacienteTest")));
        if ("Inactivo".equalsIgnoreCase(estado.getText().trim())) {
            driver.findElement(By.className("activoTest")).click();
            wait.until(d -> "Activo".equalsIgnoreCase(
                    d.findElement(By.id("estadoPacienteTest")).getText().trim()));
        }
        Assertions.assertTrue("Activo".equalsIgnoreCase(
                driver.findElement(By.id("estadoPacienteTest")).getText().trim()));

        // Abrir el formulario y comprobar la fecha inicial del input.
        WebElement botonAsignarTratamiento = wait.until(
                ExpectedConditions.elementToBeClickable(By.className("AsignarTratamientoTest")));
        botonAsignarTratamiento.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("formularioTratamientoTest")));

        WebElement fechaInput = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("fecha")));
        Assertions.assertEquals(LocalDate.now().toString(), fechaInput.getAttribute("value"));

        // Seleccionar ACOLAN y comprobar que quedó seleccionada.
        Select droga = new Select(driver.findElement(By.id("droga")));
        boolean acolanEncontrado = false;
        for (WebElement opcion : droga.getOptions()) {
            if (opcion.getText().contains("ACOLAN")) {
                Assertions.assertTrue(opcion.isEnabled(), "ACOLAN debe tener stock disponible");
                droga.selectByValue(opcion.getAttribute("value"));
                acolanEncontrado = true;
                break;
            }
        }
        Assertions.assertTrue(acolanEncontrado, "No se encontró ACOLAN en el selector");
        Assertions.assertTrue(droga.getFirstSelectedOption().getText().contains("ACOLAN"));

        driver.findElement(By.id("titulo")).sendKeys(TITULO_TRATAMIENTO);
        driver.findElement(By.id("descripcion")).sendKeys(DESCRIPCION_TRATAMIENTO);

        wait.until(ExpectedConditions.elementToBeClickable(By.id("aplicarTratamientoTest"))).click();
        WebElement confirmacion = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("confirmacionTest")));
        Assertions.assertEquals("Tratamiento Aplicado", confirmacion.getText().trim());

        // Volver al listado y comprobar que el tratamiento quedó en la ficha.
        wait.until(ExpectedConditions.elementToBeClickable(By.id("listadoPacientesTest"))).click();
        buscarMascotaAux(NOMBRE_MASCOTA, RAZA_MASCOTA);

        wait.until(this::tratamientoNuevoVisible);
        Assertions.assertTrue(tratamientoNuevoVisible(driver),
                "El historial no muestra el tratamiento nuevo con ACOLAN");

        // Ingresar como administrador.
        wait.until(ExpectedConditions.elementToBeClickable(By.id("logoutTest"))).click();
        WebElement correoInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("correo")));
        correoInput.sendKeys("admin@vetopia.com");
        WebElement contrasenaInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("contrasena")));
        contrasenaInput.sendKeys("admin123");
        wait.until(ExpectedConditions.elementToBeClickable(By.id("loginSubmitTest"))).click();
        wait.until(ExpectedConditions.urlContains("/admin/dashboard"));

        long gananciasEsperadas = GANANCIAS_INICIALES
                + (PRECIO_VENTA_ACOLAN - COSTO_ACOLAN);
        wait.until(d -> leerValorKpi(d, "gananciasTotalesTest") == gananciasEsperadas);
        Assertions.assertEquals(gananciasEsperadas,
                leerValorKpi(driver, "gananciasTotalesTest"));

        long tratamientosEsperados = TRATAMIENTOS_INICIALES + 1;
        wait.until(d -> leerValorKpi(d, "totalTratamientosAplicadosTest") == tratamientosEsperados);
        Assertions.assertEquals(tratamientosEsperados,
                leerValorKpi(driver, "totalTratamientosAplicadosTest"));
    }

    private void buscarMascotaAux(String nombreMascota, String razaMascota) {
        driver.get(BASE_URL + "/veterinario/mascotas?veterinarioId=" + VETERINARIO_ID);

        WebElement searchInput = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("busquedaMascotaTest")));
        searchInput.sendKeys(nombreMascota);

        wait.until(d -> d.findElements(By.className("fila-mascota")).size() == 2);
        List<WebElement> mascotas = driver.findElements(By.className("fila-mascota"));
        Assertions.assertEquals(2, mascotas.size());

        WebElement mascotaCorrecta = null;
        for (WebElement mascota : mascotas) {
            List<WebElement> columnas = mascota.findElements(By.tagName("td"));
            String nombre = columnas.get(1).findElements(By.tagName("p")).get(0).getText().trim();
            String raza = columnas.get(3).getText().trim();

            if (nombre.equals(nombreMascota) && raza.equals(razaMascota)) {
                mascotaCorrecta = mascota;
                break;
            }
        }

        Assertions.assertTrue(mascotaCorrecta != null,
                "No se encontró " + nombreMascota + " de raza " + razaMascota);
        mascotaCorrecta.findElement(By.className("verFichaTest")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("estadoPacienteTest")));
    }

    private boolean tratamientoNuevoVisible(WebDriver currentDriver) {
        List<WebElement> tratamientos = currentDriver.findElements(By.className("tratamientoItemTest"));
        for (WebElement tratamiento : tratamientos) {
            String droga = tratamiento.findElement(By.className("drogaTest")).getText();
            String titulo = tratamiento.findElement(By.className("tituloTratamientoTest")).getText();
            if (droga.contains("ACOLAN") && titulo.contains(TITULO_TRATAMIENTO)) {
                return true;
            }
        }
        return false;
    }

    private long leerValorKpi(WebDriver currentDriver, String idKpi) {
        String texto = currentDriver.findElement(By.id(idKpi))
                .findElement(By.tagName("h3")).getText();
        String digitos = texto.replaceAll("[^0-9]", "");
        return digitos.isEmpty() ? -1L : Long.parseLong(digitos);
    }
}

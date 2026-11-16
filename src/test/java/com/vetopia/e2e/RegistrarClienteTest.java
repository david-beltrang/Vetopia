package com.vetopia.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;


public class RegistrarClienteTest {

    private static final String BASE_URL = "http://localhost:4200";
    private static final String CORREO_VETERINARIO = "veterinario@vetopia.com";
    private static final String CONTRASENA_VETERINARIO = "vet123";

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void init() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--disable-notifications");
        chromeOptions.addArguments("--disable-extensions");
        chromeOptions.addArguments("--disable-search-engine-choice-screen");

        this.driver = new ChromeDriver(chromeOptions);
        this.driver.manage().window().maximize();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void SystemTest_veterinarioRegistraClienteYMascota_clienteConsulta() {
        // Datos unicos para que la prueba se pueda repetir contra la misma base.
        long marcaTiempo = System.currentTimeMillis();
        String cedula = String.valueOf(marcaTiempo).substring(3);
        String celular = "3" + String.valueOf(marcaTiempo).substring(4);
        String correo = "e2e." + marcaTiempo + "@vetopia.com";
        String contrasena = "dueno123";
        String nombreCliente = "Cliente prueba e2e " + marcaTiempo;
        String nombreMascota = "Mascota prueba e2e " + marcaTiempo;
        String raza = "Criollo";
        String edad = "2";
        String peso = "8.5";
        String color = "Café";
        String especie = "Perro";
        String sexo = "Macho";

        // 1. Al llegar al landing, entrar al login
        driver.get(BASE_URL + "/");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("btnLoginLanding"))).click();

        // 2. El veterinario ingresa sus credenciales, pero pone la contrasena incorrecta
        WebElement inputCorreoVeterinario = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("correo")));
        inputCorreoVeterinario.sendKeys(CORREO_VETERINARIO);

        WebElement inputContrasenaVeterinario = driver.findElement(By.id("contrasena"));
        inputContrasenaVeterinario.sendKeys("contrasena-incorrecta");
        driver.findElement(By.id("btnIngresar")).click();

        // Espera a que aparezca el mensaje de error de credenciales
        WebElement mensajeErrorLogin = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("mensajeErrorLogin")));
        assertThat(mensajeErrorLogin.getText())
                .as("El login debe mostrar el error de credenciales")
                .contains("Credenciales incorrectas");

        // 3. Arregla la contrasena y luego ingresa
        inputContrasenaVeterinario = driver.findElement(By.id("contrasena"));
        inputContrasenaVeterinario.clear();
        inputContrasenaVeterinario.sendKeys(CONTRASENA_VETERINARIO);
        driver.findElement(By.id("btnIngresar")).click();

        wait.until(ExpectedConditions.urlContains("/veterinario/mascotas"));
        // 4. Va a la seccion donde se listan los clientes
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("lnkClientesVeterinario"))).click();

        // 5. Clickea en el boton para registrar un nuevo cliente
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("btnNuevoCliente"))).click();

        // 6. Llena el formulario, pero primero se equivoca en el correo
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("cedula"))).sendKeys(cedula);
        driver.findElement(By.id("nombre")).sendKeys(nombreCliente);

        
        WebElement inputCorreoCliente = driver.findElement(By.id("correo"));
        inputCorreoCliente.sendKeys("correo-invalido");
        inputCorreoCliente.sendKeys(Keys.TAB);

        WebElement mensajeErrorCorreo = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("mensajeErrorCorreoCliente")));
        assertThat(mensajeErrorCorreo.getText().trim())
                .as("El formulario debe avisar que el correo no es valido")
                .isEqualTo("Ingresa un correo válido.");

        // 7. Corrige el correo, completa los datos y registra el cliente
        inputCorreoCliente.clear();
        inputCorreoCliente.sendKeys(correo);
        driver.findElement(By.id("celular")).sendKeys(celular);
        driver.findElement(By.id("contrasena")).sendKeys(contrasena);

        wait.until(ExpectedConditions.elementToBeClickable(By.id("btnRegistrarCliente"))).click();

        // 8. El registro del cliente encadena al registro de la mascota
        wait.until(ExpectedConditions.urlContains("/registrar-mascota"));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("nombre"))).sendKeys(nombreMascota);
        driver.findElement(By.id("raza")).sendKeys(raza);
        driver.findElement(By.id("edad")).sendKeys(edad);
        driver.findElement(By.id("pesoKg")).sendKeys(peso);
        new Select(driver.findElement(By.id("especie"))).selectByValue(especie);
        new Select(driver.findElement(By.id("sexo"))).selectByValue(sexo);
        driver.findElement(By.id("color")).sendKeys(color);

        // 9. Se envia la fecha de ingreso
        WebElement fechaIngreso = driver.findElement(By.id("fechaIngreso"));
        fechaIngreso.sendKeys("12122026");
        assertThat(fechaIngreso.getAttribute("value"))
                .as("La fecha de ingreso debe quedar diligenciada sin JavaScript")
                .isNotBlank();

        wait.until(ExpectedConditions.elementToBeClickable(By.id("btnRegistrarMascota"))).click();

        // 10. La mascota queda registrada y aparece en el listado del veterinario
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("tablaMascotasVeterinario")));
        wait.until(d -> !d.findElements(By.className("filaMascota")).isEmpty());

        List<WebElement> filasVeterinario = driver.findElements(By.className("filaMascota"));
        assertThat(filasVeterinario)
                .as("El listado del veterinario debe contener la mascota registrada")
                .isNotEmpty();
        assertThat(contieneTexto(filasVeterinario, nombreMascota)).isTrue();
        assertThat(contieneTexto(filasVeterinario, nombreCliente)).isTrue();

        // 11. El veterinario cierra sesion y el cliente entra con sus credenciales
        driver.findElement(By.id("lnkLogoutVeterinario")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("correo")));

        driver.findElement(By.id("correo")).sendKeys(correo);
        driver.findElement(By.id("contrasena")).sendKeys(contrasena);
        driver.findElement(By.id("btnIngresar")).click();

        // 12. El cliente ve su mascota en el portal
        wait.until(ExpectedConditions.urlContains("/cliente/mascotas"));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("tablaMascotasCliente")));
        wait.until(d -> d.findElements(By.className("filaMascota")).size() == 1);

        List<WebElement> filasCliente = driver.findElements(By.className("filaMascota"));
        assertThat(filasCliente.size())
                .as("El cliente debe ver exactamente su mascota registrada")
                .isEqualTo(1);

        // 13. Comparar los datos de la mascota en la fila con los datos que se registraron
        WebElement filaCliente = filasCliente.get(0);
        assertThat(filaCliente.findElement(By.className("nombreMascota")).getText()).isEqualTo(nombreMascota);
        assertThat(filaCliente.findElement(By.className("especieMascota")).getText()).isEqualTo(especie);
        assertThat(filaCliente.findElement(By.className("razaMascota")).getText()).isEqualTo(raza);
        assertThat(filaCliente.findElement(By.className("edadMascota")).getText()).isEqualTo(edad + " años");
        assertThat(filaCliente.findElement(By.className("sexoMascota")).getText()).isEqualToIgnoringCase(sexo);

        // 14. Entra al detalle y verifica los datos de la mascota.
        filaCliente.findElement(By.className("btnVerDetalles")).click();
        wait.until(ExpectedConditions.urlContains("/cliente/mascotas/detalle"));

        // 15. Verifica los datos de la mascota en el detalle
        assertThat(wait.until(ExpectedConditions.presenceOfElementLocated(By.id("detalleNombreMascota"))).getText())
                .isEqualTo(nombreMascota);
        assertThat(driver.findElement(By.id("detalleEspecieMascota")).getText()).isEqualTo(especie);
        assertThat(driver.findElement(By.id("detalleRazaMascota")).getText()).isEqualTo(raza);
        assertThat(driver.findElement(By.id("detalleEdadMascota")).getText()).isEqualTo(edad + " años");
    }

    private boolean contieneTexto(List<WebElement> elementos, String texto) {
        for (WebElement elemento : elementos) {
            if (elemento.getText().contains(texto)) {
                return true;
            }
        }
        return false;
    }
}

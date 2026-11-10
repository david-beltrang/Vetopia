package com.vetopia.e2e;

import java.time.Duration;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.vetopia.entities.Dueno;
import com.vetopia.repository.DuenoRepository;
import com.vetopia.repository.MascotaRepository;

import io.github.bonigarcia.wdm.WebDriverManager;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = "openrouter.api-key=")
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CitasClienteE2ETest {

    private static final String BASE_URL = "http://localhost:4200";

    @Autowired
    private DuenoRepository duenoRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-extensions");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }

    @Test
    void clientePuedeAgendarCitaAgrupadaYVerLaConfirmacion() {
        Dueno dueno = duenoRepository.findAll().stream()
                .filter(cliente -> !mascotaRepository.findByDueno_Id(cliente.getId()).isEmpty())
                .findFirst()
                .orElseThrow();
        driver.get(BASE_URL + "/login");

        wait.until(ExpectedConditions.elementToBeClickable(By.id("correo"))).sendKeys(dueno.getCorreo());
        driver.findElement(By.id("contrasena")).sendKeys("dueno123");
        driver.findElement(By.id("loginSubmitTest")).click();
        wait.until(ExpectedConditions.urlContains("/cliente/mascotas"));

        wait.until(ExpectedConditions.elementToBeClickable(By.id("citasClienteTest"))).click();
        wait.until(ExpectedConditions.urlContains("/cliente/citas"));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("motivoCita")));

        WebElement mascota = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("input[type='checkbox']")));
        mascota.click();
        driver.findElement(By.id("motivoCita")).sendKeys("Consulta preventiva de rutina");
        driver.findElement(By.id("analizarMotivoTest")).click();

        // Se fuerza el fallback manual para que el E2E no dependa de OpenRouter ni de una clave real.
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("especialidadCita")));
        new Select(driver.findElement(By.id("especialidadCita"))).selectByVisibleText("Medicina General");

        WebElement horario = wait.until(
                ExpectedConditions.elementToBeClickable(By.className("horarioDisponibleTest")));
        horario.click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("confirmarCitaTest"))).click();

        WebElement confirmacion = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("confirmacionCitaTest")));
        Assertions.assertTrue(confirmacion.getText().toLowerCase().contains("cita confirmada"));
        Assertions.assertTrue(driver.findElement(By.id("comoLlegarTest")).getAttribute("href")
                .contains("google.com/maps/dir"));
    }
}

package com.vetopia.service.cita;

import java.time.Duration;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import com.fasterxml.jackson.databind.JsonNode;

/** Clasifica el motivo usando solo especialidades válidas de la aplicación. */
@Service
public class ClasificadorMotivoCita {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClasificadorMotivoCita.class);

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public ClasificadorMotivoCita(RestClient.Builder restClientBuilder,
            @Value("${openrouter.api-key:}") String apiKey,
            @Value("${openrouter.model:nvidia/nemotron-3-ultra-550b-a55b:free}") String model) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(4));
        requestFactory.setReadTimeout(Duration.ofSeconds(12));

        this.restClient = restClientBuilder
                .baseUrl("https://openrouter.ai/api/v1")
                .requestFactory(requestFactory)
                .build();
        this.apiKey = apiKey;
        this.model = model;
    }

    public Optional<String> clasificar(String motivo, List<String> especialidadesPermitidas) {
        if (apiKey == null || apiKey.isBlank()) {
            LOGGER.warn("OpenRouter no se llamó: OPENROUTER_API_KEY no está configurada. Se usará selección manual.");
            return Optional.empty();
        }
        if (especialidadesPermitidas == null || especialidadesPermitidas.isEmpty()) {
            LOGGER.warn("OpenRouter no se llamó: no hay especialidades disponibles para clasificar el motivo.");
            return Optional.empty();
        }

        String listaEspecialidades = String.join("\n- ", especialidadesPermitidas);
        String prompt = "Clasifica el motivo veterinario en la especialidad más adecuada. "
                + "Devuelve únicamente una especialidad exactamente como aparece en la lista, sin explicación. "
                + "Si no hay una coincidencia clara, devuelve NO_CLASIFICABLE.\n\n"
                + "Especialidades permitidas:\n- " + listaEspecialidades
                + "\n\nMotivo: " + motivo;

        long inicioSolicitud = System.nanoTime();
        LOGGER.info("Enviando motivo a OpenRouter. Modelo={}, especialidades disponibles={}",
                model, especialidadesPermitidas.size());
        try {
            JsonNode respuesta = restClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("HTTP-Referer", "http://localhost:4200")
                    .header("X-Title", "Vetopia")
                    .body(Map.of(
                            "model", model,
                            "temperature", 0,
                            // Nemotron es un modelo de razonamiento: 40 tokens podían
                            // agotarse antes de que llegara el texto final de la respuesta.
                            "max_tokens", 256,
                            "reasoning", Map.of("effort", "minimal"),
                            "messages", List.of(
                                    Map.of("role", "system", "content",
                                            "Eres un clasificador de motivos veterinarios. Respeta estrictamente la lista permitida."),
                                    Map.of("role", "user", "content", prompt))))
                    .retrieve()
                    .body(JsonNode.class);

            JsonNode choice = respuesta == null ? null : respuesta.path("choices").path(0);
            JsonNode mensaje = choice == null ? null : choice.path("message");
            JsonNode contenido = mensaje == null ? null : mensaje.path("content");
            if (contenido == null || contenido.isMissingNode() || contenido.isNull()) {
                String motivoFin = choice == null || choice.path("finish_reason").isMissingNode()
                        ? "desconocido"
                        : choice.path("finish_reason").asText();
                boolean razonamientoPresente = mensaje != null
                        && (mensaje.hasNonNull("reasoning") || mensaje.hasNonNull("reasoning_details"));
                int tokensRespuesta = respuesta == null ? -1 : respuesta.path("usage")
                        .path("completion_tokens").asInt(-1);
                LOGGER.warn("OpenRouter respondió sin contenido final. Modelo={}, finish_reason={}, "
                                + "razonamiento_presente={}, tokens_respuesta={}, duración={} ms",
                        model, motivoFin, razonamientoPresente, tokensRespuesta, duracionMs(inicioSolicitud));
                return Optional.empty();
            }

            String salida = contenido.asText().trim();
            Optional<String> especialidadEncontrada = especialidadesPermitidas.stream()
                    .filter(especialidad -> normalizar(salida).equals(normalizar(especialidad)))
                    .findFirst();
            if (especialidadEncontrada.isPresent()) {
                LOGGER.info("OpenRouter clasificó el motivo como '{}'. Duración={} ms",
                        especialidadEncontrada.get(), duracionMs(inicioSolicitud));
            } else {
                // No se registra el texto devuelto: el modelo podría repetir detalles clínicos del motivo.
                LOGGER.warn("OpenRouter respondió, pero el texto no coincide con una especialidad permitida. "
                        + "Modelo={}, especialidades disponibles={}, duración={} ms",
                        model, especialidadesPermitidas.size(), duracionMs(inicioSolicitud));
            }
            return especialidadEncontrada;
        } catch (RestClientResponseException ex) {
            LOGGER.warn("OpenRouter respondió con HTTP {}. Modelo={}, duración={} ms; se usará selección manual.",
                    ex.getStatusCode().value(), model, duracionMs(inicioSolicitud));
            return Optional.empty();
        } catch (RestClientException ex) {
            // Una caída o límite del proveedor no debe impedir solicitar la cita:
            // el controlador ofrecerá la selección manual de especialidad.
            LOGGER.warn("Falló la conexión o lectura de OpenRouter. Modelo={}, tipo de error={}, duración={} ms; "
                    + "se usará selección manual.",
                    model, ex.getClass().getSimpleName(), duracionMs(inicioSolicitud));
            return Optional.empty();
        }
    }

    private long duracionMs(long inicioSolicitud) {
        return (System.nanoTime() - inicioSolicitud) / 1_000_000;
    }

    private String normalizar(String valor) {
        return Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-zA-Z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}

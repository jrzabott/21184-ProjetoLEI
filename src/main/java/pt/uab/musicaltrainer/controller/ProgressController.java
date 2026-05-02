package pt.uab.musicaltrainer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.uab.musicaltrainer.api.ProgressResponse;
import pt.uab.musicaltrainer.service.ProgressService;

/**
 * REST controller para dados de progresso e métricas.
 */
@Tag(name = "Progresso", description = "Métricas e histórico de progresso do utilizador")
@RestController
@RequestMapping("/api")
public class ProgressController {

    private static final Logger logger = LoggerFactory.getLogger(ProgressController.class);
    private final ProgressService service;

    public ProgressController(ProgressService service) {
        this.service = service;
    }

    @Operation(summary = "Obter progresso", description = "Devolve métricas globais: sessões, exercícios, taxa de acerto por tipo")
    @GetMapping("/progress")
    public ResponseEntity<ProgressResponse> getProgress() throws Exception {
        logger.debug("GET /api/progress");
        ProgressResponse progress = service.buildProgress();
        return ResponseEntity.ok(progress);
    }
}

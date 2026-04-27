package pt.uab.musicaltrainer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.uab.musicaltrainer.service.ProgressService;

/**
 * REST controller para dados de progresso e métricas.
 */
@RestController
@RequestMapping("/api")
public class ProgressController {

    private static final Logger logger = LoggerFactory.getLogger(ProgressController.class);
    private final ProgressService service;

    public ProgressController(ProgressService service) {
        this.service = service;
    }

    @GetMapping("/progress")
    public ResponseEntity<?> getProgress() {
        logger.debug("GET /api/progress");
        try {
            return ResponseEntity.ok(service.buildProgress());
        } catch (Exception e) {
            logger.error("Erro ao obter progresso", e);
            return ResponseEntity.internalServerError().body("Erro ao obter progresso");
        }
    }
}

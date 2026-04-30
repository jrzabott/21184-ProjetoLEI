package pt.uab.musicaltrainer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.uab.musicaltrainer.api.*;
import pt.uab.musicaltrainer.dto.SessionRecord;
import pt.uab.musicaltrainer.service.SessionService;

/**
 * REST controller para gestão de sessões de treino.
 */
@Tag(name = "Sessões", description = "Gestão de sessões de treino")
@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private static final Logger logger = LoggerFactory.getLogger(SessionController.class);
    private final SessionService service;

    public SessionController(SessionService service) {
        this.service = service;
        logger.info("SessionController inicializado");
    }

    @Operation(summary = "Iniciar sessão")
    @PostMapping("/start")
    public ResponseEntity<?> start(@RequestBody SessionStartRequest request) {
        logger.debug("POST /api/sessions/start: exerciseType={}", request.exerciseType());
        try {
            SessionRecord session = service.startSession();
            return ResponseEntity.status(201).body(
                new SessionStartResponse(session.id(), session.startTime())
            );
        } catch (Exception e) {
            logger.error("Erro ao iniciar sessão", e);
            return ResponseEntity.internalServerError().body("Erro ao iniciar sessão");
        }
    }

    @Operation(summary = "Terminar sessão")
    @PostMapping("/{sessionId}/end")
    public ResponseEntity<?> end(@PathVariable Long sessionId) {
        logger.debug("POST /api/sessions/{}/end", sessionId);
        try {
            SessionRecord ended = service.endSession(sessionId);
            double accuracy = service.calculateAccuracy(ended);
            long duration = service.calculateDurationSeconds(ended);

            return ResponseEntity.ok(new SessionEndResponse(
                ended.id(), ended.totalExercises(), ended.correctAnswers(), accuracy, duration
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Erro ao terminar sessão: id={}", sessionId, e);
            return ResponseEntity.internalServerError().body("Erro ao terminar sessão");
        }
    }
}

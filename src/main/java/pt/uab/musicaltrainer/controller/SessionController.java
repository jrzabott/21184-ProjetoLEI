package pt.uab.musicaltrainer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.uab.musicaltrainer.api.SessionResponse;
import pt.uab.musicaltrainer.dto.SessionRecord;
import pt.uab.musicaltrainer.service.SessionService;

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

    @Operation(summary = "Iniciar sessão",
               description = "Cria uma nova sessão de treino. Corpo do pedido opcional.")
    @PostMapping("/start")
    public ResponseEntity<SessionResponse> start() throws Exception {
        logger.debug("POST /api/sessions/start");
        SessionRecord session = service.startSession();
        SessionResponse response = new SessionResponse(
            session.id(), session.startTime(), null,
            session.totalExercises(), session.correctAnswers(), session.incorrectAnswers(),
            null, null
        );
        logger.info("Sessão iniciada: id={}", session.id());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Terminar sessão",
               description = "Marca a sessão como terminada. Idempotente — chamar duas vezes devolve o mesmo estado.")
    @PostMapping("/{sessionId}/end")
    public ResponseEntity<SessionResponse> end(@PathVariable Long sessionId) throws Exception {
        logger.debug("POST /api/sessions/{}/end", sessionId);
        SessionRecord ended  = service.endSession(sessionId);
        double accuracy      = service.calculateAccuracy(ended);
        long duration        = service.calculateDurationSeconds(ended);

        SessionResponse response = new SessionResponse(
            ended.id(), ended.startTime(), ended.endTime(),
            ended.totalExercises(), ended.correctAnswers(), ended.incorrectAnswers(),
            accuracy, duration
        );
        logger.info("Sessão terminada: id={}", sessionId);
        return ResponseEntity.ok(response);
    }
}

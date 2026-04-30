package pt.uab.musicaltrainer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.uab.musicaltrainer.api.*;
import pt.uab.musicaltrainer.dto.ExerciseRecord;
import pt.uab.musicaltrainer.generator.GeneratedExercise;
import pt.uab.musicaltrainer.service.ExerciseService;

/**
 * REST controller para operações de exercícios.
 * O utilizador responde tocando notas MIDI — sem múltipla escolha (ADR-014).
 */
@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    private static final Logger logger = LoggerFactory.getLogger(ExerciseController.class);

    private final ExerciseService service;

    public ExerciseController(ExerciseService service) {
        this.service = service;
        logger.info("ExerciseController inicializado");
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody GenerateRequest request) {
        logger.debug("POST /api/exercises/generate: type={}, difficulty={}",
            request.type(), request.difficulty());
        try {
            ExerciseRecord saved = service.generateAndSave(request.type(), request.difficulty());
            GeneratedExercise display = service.getDisplayData(saved);

            GenerateResponse response = new GenerateResponse(
                saved.id(), saved.type(), saved.difficulty(),
                display.notesToPlay(), display.description(), display.options()
            );
            logger.info("Exercício gerado: id={}, type={}", saved.id(), saved.type());
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.warn("Tipo inválido: {}", request.type());
            return ResponseEntity.badRequest().body("Tipo de exercício inválido: " + request.type());
        } catch (Exception e) {
            logger.error("Erro ao gerar exercício", e);
            return ResponseEntity.internalServerError().body("Erro interno");
        }
    }

    @PostMapping("/answer")
    public ResponseEntity<?> answer(@RequestBody AnswerRequest request) {
        logger.debug("POST /api/exercises/answer: exerciseId={}, notes={}",
            request.exerciseId(), request.notes().length);
        try {
            boolean correct = service.evaluateAnswer(request.exerciseId(), request.notes());
            int[] expectedNotes = service.getExpectedNotes(request.exerciseId());
            String explanation = service.buildExplanation(request.exerciseId(), request.notes(), correct);

            AnswerResponse response = new AnswerResponse(
                correct,
                java.util.Arrays.toString(expectedNotes),
                java.util.Arrays.toString(request.notes()),
                explanation
            );
            logger.info("Resposta avaliada: exerciseId={}, correct={}", request.exerciseId(), correct);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Erro ao avaliar resposta: exerciseId={}", request.exerciseId(), e);
            return ResponseEntity.internalServerError().body("Erro ao avaliar resposta");
        }
    }
}

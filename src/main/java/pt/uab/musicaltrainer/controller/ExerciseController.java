package pt.uab.musicaltrainer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.uab.musicaltrainer.api.*;
import pt.uab.musicaltrainer.dto.ExerciseRecord;
import pt.uab.musicaltrainer.generator.GeneratedExercise;
import pt.uab.musicaltrainer.service.ExerciseService;

@Tag(name = "Exercícios", description = "Geração e avaliação de exercícios de teoria musical")
@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    private static final Logger logger = LoggerFactory.getLogger(ExerciseController.class);

    private final ExerciseService service;
    private final ObjectMapper objectMapper;

    public ExerciseController(ExerciseService service, ObjectMapper objectMapper) {
        this.service = service;
        this.objectMapper = objectMapper;
        logger.info("ExerciseController inicializado");
    }

    @Operation(summary = "Gerar exercício", description = "Gera um exercício aleatório do tipo e dificuldade indicados")
    @PostMapping("/generate")
    public ResponseEntity<GenerateResponse> generate(@RequestBody GenerateRequest request) throws Exception {
        logger.debug("POST /api/exercises/generate: type={}, difficulty={}", request.type(), request.difficulty());

        ExerciseRecord saved      = service.generateAndSave(request.type(), request.difficulty(), request.sessionId());
        int suggested             = service.getSuggestedDifficulty(saved.type(), saved.difficulty());
        GeneratedExercise display = service.getDisplayData(saved);

        GenerateResponse response = new GenerateResponse(
            saved.id(), saved.type(), saved.difficulty(),
            suggested,
            display.notesToPlay(), display.description(), display.hint(), display.options()
        );
        logger.info("Exercício gerado: id={}, type={}", saved.id(), saved.type());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Avaliar resposta", description = "Recebe notas MIDI tocadas e avalia se correspondem ao exercício")
    @PostMapping("/answer")
    public ResponseEntity<AnswerResponse> answer(@Valid @RequestBody AnswerRequest request) throws Exception {
        logger.debug("POST /api/exercises/answer: exerciseId={}, sessionId={}", request.exerciseId(), request.sessionId());

        boolean correct     = service.evaluateAnswer(request.exerciseId(), request.sessionId(), request.notes());
        int[] expectedNotes = service.getExpectedNotes(request.exerciseId());
        String explanation  = service.buildExplanation(request.exerciseId(), request.notes(), correct);

        String correctAnswerJson = objectMapper.writeValueAsString(expectedNotes);
        String userAnswerJson    = objectMapper.writeValueAsString(request.notes());

        AnswerResponse response = new AnswerResponse(correct, correctAnswerJson, userAnswerJson, explanation);
        logger.info("Resposta avaliada: exerciseId={}, correct={}", request.exerciseId(), correct);
        return ResponseEntity.ok(response);
    }
}

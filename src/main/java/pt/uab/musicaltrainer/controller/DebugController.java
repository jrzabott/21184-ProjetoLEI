package pt.uab.musicaltrainer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.uab.musicaltrainer.api.GenerateAllRequest;
import pt.uab.musicaltrainer.generator.ExerciseType;
import pt.uab.musicaltrainer.service.DifficultyService;
import pt.uab.musicaltrainer.service.ExerciseService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Debug - apenas testes",
     description = "Diagnóstico e geração em massa para testes manuais via Swagger. Não usar em produção.")
@RestController
@RequestMapping("/api/debug")
public class DebugController {

    private static final Logger logger = LoggerFactory.getLogger(DebugController.class);

    private final ExerciseService  exerciseService;
    private final DifficultyService difficultyService;

    public DebugController(ExerciseService exerciseService, DifficultyService difficultyService) {
        this.exerciseService   = exerciseService;
        this.difficultyService = difficultyService;
        logger.info("DebugController inicializado");
    }

    @Operation(summary = "Tipos suportados e dificuldade actual",
               description = "Lista todos os ExerciseType e a dificuldade sugerida para cada um.")
    @GetMapping("/exercises/types")
    public ResponseEntity<?> getTypes() throws Exception {
        List<String> types = Arrays.stream(ExerciseType.values()).map(Enum::name).toList();
        Map<String, Integer> difficulties = new HashMap<>();
        for (ExerciseType type : ExerciseType.values()) {
            difficulties.put(type.name(), difficultyService.suggestDifficulty(type.name(), 5));
        }
        return ResponseEntity.ok(Map.of("types", types, "suggestedDifficulties", difficulties));
    }

    @Operation(summary = "Dificuldade sugerida para um tipo",
               description = "Calcula a dificuldade baseada nos últimos 100 exercícios do tipo.")
    @GetMapping("/exercises/difficulty/{type}")
    public ResponseEntity<?> getDifficulty(@PathVariable String type,
                                           @RequestParam(defaultValue = "5") int current) throws Exception {
        // valida o tipo antes de prosseguir - tipo inválido = 400 via GlobalExceptionHandler
        // usar validType.name() para garantir capitalização correcta no query à BD
        ExerciseType validType = ExerciseType.valueOf(type.toUpperCase());
        int suggested = difficultyService.suggestDifficulty(validType.name(), current);
        return ResponseEntity.ok(Map.of(
            "type", validType.name(),
            "currentDifficulty", current,
            "suggestedDifficulty", suggested
        ));
    }

    @Operation(summary = "Gerar exercícios de todos os tipos para teste",
               description = "Gera exercícios dos tipos indicados no corpo. types vazio = todos.")
    @PostMapping("/exercises/generate-all")
    public ResponseEntity<?> generateAll(@RequestBody(required = false) GenerateAllRequest request) throws Exception {
        int minDiff = (request == null || request.minDifficulty() == 0) ? 1  : request.minDifficulty();
        int maxDiff = (request == null || request.maxDifficulty() == 0) ? 10 : request.maxDifficulty();

        List<ExerciseType> targetTypes;
        if (request == null || request.types() == null || request.types().isEmpty()) {
            targetTypes = Arrays.asList(ExerciseType.values());
        } else {
            targetTypes = request.types().stream()
                .map(t -> ExerciseType.valueOf(t.toUpperCase()))
                .toList();
        }

        List<Map<String, Object>> results = new ArrayList<>();
        for (ExerciseType type : targetTypes) {
            for (int diff = minDiff; diff <= maxDiff; diff += 3) {
                var saved   = exerciseService.generateAndSave(type.name(), diff);
                var display = exerciseService.getDisplayData(saved);
                results.add(Map.of(
                    "exerciseId",  saved.id(),
                    "type",        type.name(),
                    "difficulty",  diff,
                    "notes",       display.notesToPlay(),
                    "description", display.description()
                ));
            }
        }
        logger.info("Debug generate-all: {} exercícios gerados", results.size());
        return ResponseEntity.ok(results);
    }
}

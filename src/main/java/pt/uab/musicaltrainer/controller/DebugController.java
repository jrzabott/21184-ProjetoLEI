package pt.uab.musicaltrainer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.uab.musicaltrainer.generator.ExerciseType;
import pt.uab.musicaltrainer.service.DifficultyService;
import pt.uab.musicaltrainer.service.ExerciseService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Endpoints de diagnóstico para teste manual via Swagger.
 * NÃO usar em produção — apenas para verificação e demonstração.
 */
@Tag(name = "Debug — apenas testes",
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
               description = "Lista todos os ExerciseType e a dificuldade sugerida para cada um com base no histórico.")
    @GetMapping("/exercises/types")
    public ResponseEntity<?> getTypes() {
        try {
            List<String> types = Arrays.stream(ExerciseType.values()).map(Enum::name).toList();
            Map<String, Integer> difficulties = new HashMap<>();
            for (ExerciseType type : ExerciseType.values()) {
                difficulties.put(type.name(), difficultyService.suggestDifficulty(type.name(), 5));
            }
            return ResponseEntity.ok(Map.of("types", types, "suggestedDifficulties", difficulties));
        } catch (Exception e) {
            logger.error("Erro em debug/types", e);
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }

    @Operation(summary = "Dificuldade sugerida para um tipo",
               description = "Calcula a dificuldade baseada nos últimos 100 exercícios do tipo.")
    @GetMapping("/exercises/difficulty/{type}")
    public ResponseEntity<?> getDifficulty(@PathVariable String type,
                                           @RequestParam(defaultValue = "5") int current) {
        try {
            int suggested = difficultyService.suggestDifficulty(type, current);
            return ResponseEntity.ok(Map.of(
                "type", type,
                "currentDifficulty", current,
                "suggestedDifficulty", suggested
            ));
        } catch (Exception e) {
            logger.error("Erro em debug/difficulty/{}", type, e);
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }

    @Operation(summary = "Gerar exercícios de todos os tipos para teste",
               description = "Gera exercícios de cada tipo nas dificuldades indicadas (passo 3). Útil para verificar taxonomia de dificuldade.")
    @PostMapping("/exercises/generate-all")
    public ResponseEntity<?> generateAll(
            @RequestParam(defaultValue = "1") int minDifficulty,
            @RequestParam(defaultValue = "10") int maxDifficulty) {
        try {
            List<Map<String, Object>> results = new ArrayList<>();
            for (ExerciseType type : ExerciseType.values()) {
                for (int diff = minDifficulty; diff <= maxDifficulty; diff += 3) {
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
        } catch (Exception e) {
            logger.error("Erro em debug/generate-all", e);
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }
}

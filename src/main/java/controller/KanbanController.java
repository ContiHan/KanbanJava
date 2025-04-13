package controller;

import domain.Kanban;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.KanbanService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/kanbans")
@RequiredArgsConstructor
@Tag(name = "Kanban Board Management", description = "APIs for managing Kanban boards")
public class KanbanController {

    private final KanbanService kanbanService;

    @Operation(summary = "Get all Kanban boards")
    @GetMapping
    public ResponseEntity<List<Kanban>> getAllKanbans() {
        List<Kanban> kanbans = kanbanService.getAllKanbans();
        return ResponseEntity.ok(kanbans);
    }

    @Operation(summary = "Get Kanban board by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Kanban> getKanbanById(@PathVariable Long id) {
        Kanban kanban = kanbanService.getKanbanById(id);
        return ResponseEntity.ok(kanban);
        // Zde by bylo vhodné použít DTO, které obsahuje i tasky daného kanbanu
        // Např. načíst kanban a pak zavolat taskService.getTasksByKanbanId(id)
    }

    @Operation(summary = "Create a new Kanban board")
    @PostMapping
    public ResponseEntity<Kanban> createKanban(@RequestBody Kanban kanban) {
        // V reálu DTO a validace
        Kanban createdKanban = kanbanService.createKanban(kanban);
        return new ResponseEntity<>(createdKanban, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing Kanban board")
    @PutMapping("/{id}")
    public ResponseEntity<Kanban> updateKanban(@PathVariable Long id, @RequestBody Kanban kanbanDetails) {
        // V reálu DTO a validace
        Kanban updatedKanban = kanbanService.updateKanban(id, kanbanDetails);
        return ResponseEntity.ok(updatedKanban);
    }

    @Operation(summary = "Delete a Kanban board by ID (including its tasks)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKanban(@PathVariable Long id) {
        kanbanService.deleteKanban(id);
        return ResponseEntity.noContent().build();
    }

    // --- Endpointy pro Tasky v kontextu Kanbanu ---
    // Alternativně mohou být task endpointy vnořené pod kanban, např. /api/v1/kanbans/{kanbanId}/tasks
    // To jsme ale vyřešili parametrem ?kanbanId= v TaskControlleru.
    // Příklad, jak by to mohlo vypadat zde:
    /*
    @Operation(summary = "Get all tasks for a specific Kanban board")
    @GetMapping("/{kanbanId}/tasks")
    public ResponseEntity<List<Task>> getTasksForKanban(@PathVariable Long kanbanId) {
        // Zde by se volala metoda TaskService, např. taskService.getTasksByKanbanId(kanbanId);
        // Je potřeba mít TaskService injektovanou i zde, nebo mít metodu v KanbanService, která to deleguje
        List<Task> tasks = taskService.getTasksByKanbanId(kanbanId); // Předpokládá injektovanou TaskService
        return ResponseEntity.ok(tasks);
    }
    */
}

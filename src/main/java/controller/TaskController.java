package controller;

import domain.Task;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.TaskService;

import java.util.List;
import java.util.Set;

// DTO pro vytvoření/aktualizaci Tasku, abychom mohli poslat i ID userů
// V reálné aplikaci by měly být samostatné DTO pro request a response.
@lombok.Data // Pro jednoduchost použijeme Lombok i zde
class TaskRequest {
    private String title;
    private String description;
    private String status;
    private Set<Long> assignedUserIds;
    // Pro update by se mohlo hodit i Long kanbanId, pokud chceme umožnit přesun
}


@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Task Management", description = "APIs for managing tasks")
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Get all tasks")
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(
            @Parameter(description = "Optional filter by Kanban ID")
            @RequestParam(required = false) Long kanbanId)
    {
        List<Task> tasks;
        if (kanbanId != null) {
            tasks = taskService.getTasksByKanbanId(kanbanId);
        } else {
            tasks = taskService.getAllTasks();
        }
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Get task by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    // Endpoint pro vytvoření tasku v rámci kanbanu
    @Operation(summary = "Create a new task within a specific Kanban board")
    @PostMapping("/kanban/{kanbanId}")
    public ResponseEntity<Task> createTask(
            @PathVariable Long kanbanId,
            @RequestBody TaskRequest taskRequest) {

        // Mapování z DTO na Task (pro data mimo ID a vztahy)
        Task taskDetails = new Task();
        taskDetails.setTitle(taskRequest.getTitle());
        taskDetails.setDescription(taskRequest.getDescription());
        taskDetails.setStatus(taskRequest.getStatus());

        Task createdTask = taskService.createTask(kanbanId, taskDetails, taskRequest.getAssignedUserIds());
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }


    @Operation(summary = "Update an existing task")
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long id,
            @RequestBody TaskRequest taskRequest) {

        // Mapování z DTO na Task
        Task taskDetails = new Task();
        taskDetails.setTitle(taskRequest.getTitle());
        taskDetails.setDescription(taskRequest.getDescription());
        taskDetails.setStatus(taskRequest.getStatus());
        // Zde by se případně předávalo i ID kanbanu pro přesun

        Task updatedTask = taskService.updateTask(id, taskDetails, taskRequest.getAssignedUserIds());
        return ResponseEntity.ok(updatedTask);
    }

    @Operation(summary = "Delete a task by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
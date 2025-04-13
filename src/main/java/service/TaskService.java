package service;

import domain.Kanban;
import domain.Task;
import domain.User;
import exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.KanbanRepository;
import repository.TaskRepository;
import repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final KanbanRepository kanbanRepository;
    private final UserRepository userRepository; // Pro přiřazování userů

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<Task> getTasksByKanbanId(Long kanbanId) {
        if (!kanbanRepository.existsById(kanbanId)) {
            throw new ResourceNotFoundException("Kanban", "id", kanbanId);
        }
        return taskRepository.findByKanbanId(kanbanId);
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
    }

    // Vytvoření tasku v rámci konkrétního kanbanu
    public Task createTask(Long kanbanId, Task taskDetails, Set<Long> assignedUserIds) {
        Kanban kanban = kanbanRepository.findById(kanbanId)
                .orElseThrow(() -> new ResourceNotFoundException("Kanban", "id", kanbanId));

        Task newTask = new Task();
        newTask.setTitle(taskDetails.getTitle());
        newTask.setDescription(taskDetails.getDescription());
        newTask.setStatus(taskDetails.getStatus() != null ? taskDetails.getStatus() : "TODO"); // Default status
        newTask.setKanban(kanban); // Přiřazení ke kanbanu

        // Načtení a přiřazení uživatelů
        if (assignedUserIds != null && !assignedUserIds.isEmpty()) {
            Set<User> users = new HashSet<>(userRepository.findAllById(assignedUserIds));
            if (users.size() != assignedUserIds.size()) {
                // Některá ID nebyla nalezena - můžete chtít vyhodit chybu nebo jen varovat
                throw new ResourceNotFoundException("One or more assigned Users not found");
            }
            newTask.setAssignedUsers(users);
            // Pro obousměrný vztah (pokud máte helper metody v User)
            // users.forEach(user -> user.getTasks().add(newTask));
        }

        return taskRepository.save(newTask);
    }


    public Task updateTask(Long id, Task taskDetails, Set<Long> assignedUserIds) {
        Task existingTask = getTaskById(id); // Zajistí existenci

        existingTask.setTitle(taskDetails.getTitle());
        existingTask.setDescription(taskDetails.getDescription());
        existingTask.setStatus(taskDetails.getStatus());

        // Aktualizace přiřazených uživatelů
        updateAssignedUsers(existingTask, assignedUserIds);

        // Změna Kanban boardu (pokud je potřeba) - vyžadovalo by kanbanId v requestu
        // if (taskDetails.getKanban() != null && taskDetails.getKanban().getId() != null) {
        //     if (!taskDetails.getKanban().getId().equals(existingTask.getKanban().getId())) {
        //         Kanban newKanban = kanbanRepository.findById(taskDetails.getKanban().getId())
        //             .orElseThrow(() -> new ResourceNotFoundException("Kanban", "id", taskDetails.getKanban().getId()));
        //         existingTask.setKanban(newKanban);
        //     }
        // }

        return taskRepository.save(existingTask);
    }

    private void updateAssignedUsers(Task task, Set<Long> newUserIds) {
        Set<User> currentUsers = task.getAssignedUsers();
        Set<Long> currentUserIds = currentUsers.stream().map(User::getId).collect(Collectors.toSet());

        Set<Long> idsToAdd = new HashSet<>();
        Set<Long> idsToRemove = new HashSet<>(currentUserIds); // Start with all current

        if (newUserIds != null) {
            idsToAdd.addAll(newUserIds);
            idsToAdd.removeAll(currentUserIds); // Only add those not already present
            idsToRemove.removeAll(newUserIds); // Keep only those not in the new set
        } else {
            // If newUserIds is null, remove all existing users
            idsToRemove.addAll(currentUserIds);
        }


        // Remove users
        if (!idsToRemove.isEmpty()) {
            List<User> usersToRemove = userRepository.findAllById(idsToRemove);
            usersToRemove.forEach(task::removeUser); // Použij helper metodu, pokud existuje
            // Alternativně: task.getAssignedUsers().removeAll(usersToRemove);
        }

        // Add users
        if (!idsToAdd.isEmpty()) {
            List<User> usersToAdd = userRepository.findAllById(idsToAdd);
            if (usersToAdd.size() != idsToAdd.size()) {
                throw new ResourceNotFoundException("One or more assigned Users not found for adding");
            }
            usersToAdd.forEach(task::assignUser); // Použij helper metodu, pokud existuje
            // Alternativně: task.getAssignedUsers().addAll(usersToAdd);
        }
    }


    public void deleteTask(Long id) {
        Task task = getTaskById(id); // Ověření existence
        // Před smazáním tasku se automaticky odstraní záznamy ze spojovací tabulky task_user
        taskRepository.delete(task);
    }
}

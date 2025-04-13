package service;

import domain.Kanban;
import exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.KanbanRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class KanbanService {

    private final KanbanRepository kanbanRepository;

    public List<Kanban> getAllKanbans() {
        return kanbanRepository.findAll();
    }

    public Kanban getKanbanById(Long id) {
        return kanbanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kanban", "id", id));
    }

    public Kanban createKanban(Kanban kanban) {
        // Může obsahovat validaci názvu atd.
        Kanban newKanban = new Kanban();
        newKanban.setName(kanban.getName());
        // Tasky se přidávají samostatně přes TaskService
        return kanbanRepository.save(newKanban);
    }

    public Kanban updateKanban(Long id, Kanban kanbanDetails) {
        Kanban existingKanban = getKanbanById(id); // Zajistí existenci
        existingKanban.setName(kanbanDetails.getName());
        // Úprava tasků v rámci kanbanu se řeší přes TaskService
        return kanbanRepository.save(existingKanban);
    }

    public void deleteKanban(Long id) {
        Kanban kanban = getKanbanById(id); // Ověření existence
        // Díky CascadeType.ALL a orphanRemoval=true v Kanban entitě
        // by se měly smazat i všechny asociované Tasky.
        kanbanRepository.delete(kanban);
    }
}

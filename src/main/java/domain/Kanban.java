package domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Kanban {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Vztah 1:N s Task - Kanban může obsahovat více Tasků
    // 'mappedBy = "kanban"' znamená, že Task entita vlastní tento vztah (má @JoinColumn)
    // CascadeType.ALL znamená, že operace (uložení, smazání) na Kanban se přenesou na Tasky
    // orphanRemoval = true zajistí smazání Tasku z DB, pokud je odstraněn z kolekce v Kanban
    @OneToMany(mappedBy = "kanban", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Task> tasks = new ArrayList<>();

    // Metody pro snadnější správu obousměrného vztahu s Task (volitelné, ale doporučené)
    public void addTask(Task task) {
        tasks.add(task);
        task.setKanban(this);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        task.setKanban(null);
    }

    // Konstruktor pro snadnější vytváření (bez ID a tasks)
    public Kanban(String name) {
        this.name = name;
    }
}

package domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    // Můžete zvážit Enum pro status
    private String status;

    // Vztah N:1 s Kanban - Task patří k jednomu Kanbanu
    @ManyToOne(fetch = FetchType.LAZY) // LAZY je obecně lepší pro výkon
    @JoinColumn(name = "kanban_id", nullable = false) // Cizí klíč v tabulce Task
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Kanban kanban;

    // Vztah N:M s User - Task může mít přiřazeno více Userů
    // Tato strana vztahu vlastní join tabulku 'task_user'
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "task_user", // Název spojovací tabulky
            joinColumns = @JoinColumn(name = "task_id"), // Sloupec odkazující na Task
            inverseJoinColumns = @JoinColumn(name = "user_id") // Sloupec odkazující na User
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<User> assignedUsers = new HashSet<>();

    // Metody pro snadnější správu obousměrného vztahu s User (volitelné, ale doporučené)
    public void assignUser(User user) {
        this.assignedUsers.add(user);
        user.getTasks().add(this);
    }

    public void removeUser(User user) {
        this.assignedUsers.remove(user);
        user.getTasks().remove(this);
    }
}

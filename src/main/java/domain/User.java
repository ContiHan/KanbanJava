package domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "app_user") // "user" je často rezervované slovo v SQL
@Data // Generuje gettery, settery, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    // V reálné aplikaci by heslo mělo být hashované
    @Column(nullable = false)
    private String password;

    // Vztah N:M s Task - User může být přiřazen k více Taskům
    // 'mappedBy = "assignedUsers"' znamená, že Task entita vlastní tento vztah (má @JoinTable)
    // Používáme Set pro efektivitu a zamezení duplicit
    @ManyToMany(mappedBy = "assignedUsers", fetch = FetchType.LAZY)
    @ToString.Exclude // Zabraňuje rekurzi v toString()
    @EqualsAndHashCode.Exclude // Zabraňuje rekurzi v equals() a hashCode()
    private Set<Task> tasks = new HashSet<>();

    // Konstruktor pro snadnější vytváření (bez ID a tasks)
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}

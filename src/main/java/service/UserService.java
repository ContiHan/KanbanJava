package service;

import domain.User;
import exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor; // Lombok pro generování konstruktoru s @Autowired
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Pro správu transakcí
import repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor // Vytvoří konstruktor pro všechny final pole (pro DI)
@Transactional // Všechny public metody budou transakční
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    public User createUser(User user) {
        // Zde by mohla být validace, hashování hesla atd.
        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        User existingUser = getUserById(id); // Zajistí, že user existuje

        existingUser.setUsername(userDetails.getUsername());
        existingUser.setEmail(userDetails.getEmail());
        // Pozor na heslo - aktualizaci řešit opatrně (např. samostatná metoda)
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            // Zde by mělo být hashování nového hesla
            existingUser.setPassword(userDetails.getPassword());
        }
        // Správa vztahů (tasks) se typicky řeší přes TaskService

        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        User user = getUserById(id); // Ověření existence
        // Před smazáním usera může být potřeba odpojit ho od tasků,
        // i když N:M by to mělo zvládnout smazáním záznamů ve spojovací tabulce.
        // Záleží na nastavení cascade operací.
        userRepository.delete(user);
    }
}

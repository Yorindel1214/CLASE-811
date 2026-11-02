package co.com.crediya.jpa;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JPARepositoryAdapter implements UserRepository {

    private final JPARepository repository;
    private final ObjectMapper mapper;

    @Override
    public void saveUser(User user) {
        repository.save(mapper.map(user, UserEntity.class));
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        repository.findAll().forEach(e -> users.add(mapper.map(e, User.class)));
        return users;
    }

    @Override
    public User getUserByIdNumber(Long idNumber) {
        return repository.findById(idNumber)
                .map(e -> mapper.map(e, User.class))
                .orElse(null);
    }

    @Override
    public User editUser(User user) {
        return mapper.map(repository.save(mapper.map(user, UserEntity.class)), User.class);
    }

    @Override
    public void deleteUser(Long idNumber) {
        repository.deleteById(idNumber);
    }
}
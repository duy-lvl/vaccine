package fa.training.backend.service.impl;

import fa.training.backend.model.Admin;
import fa.training.backend.repository.EmployeeRepository;
import fa.training.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Value("${admin.username}")
    private String adminUsername;
    @Value("${admin.password}")
    private String adminPassword;

    private final EmployeeRepository employeeRepository;

    @Autowired
    public UserServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                if (adminUsername.equals(username)) {
                    return new Admin(adminUsername, adminPassword);
                }
                var employee = employeeRepository.findByUsername(username);
                if (employee.isPresent()) {
                    return employee.get();
                }
                throw new UsernameNotFoundException("User not found");
            }
        };
    }
}

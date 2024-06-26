package com.mobiauto.service.implementation;

import com.mobiauto.model.Role;
import com.mobiauto.service.repository.RoleRepository;
import com.mobiauto.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository repository;

    @Override
    public Role findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<Role> findAll() {
        return repository.findAll();
    }

    @Override
    public Role save(Role role) {
        return repository.save(role);
    }
}

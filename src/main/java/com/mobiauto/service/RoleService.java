package com.mobiauto.service;


import com.mobiauto.model.Role;
import java.util.List;

public interface RoleService {

    Role findById(Long id);

    List<Role> findAll();

    Role save(Role obj);

}
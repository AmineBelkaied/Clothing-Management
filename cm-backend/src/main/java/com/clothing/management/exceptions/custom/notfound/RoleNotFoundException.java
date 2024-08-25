package com.clothing.management.exceptions.custom.notfound;

import com.clothing.management.exceptions.generic.EntityNotFoundException;

public class RoleNotFoundException extends EntityNotFoundException {

    public RoleNotFoundException(Long roleId, String roleName) {
        super("Role", roleId, roleName);
    }
}

package com.clothing.management.exceptions.custom.alreadyexists;

import com.clothing.management.exceptions.generic.AlreadyExistsException;

public class RoleAlreadyExistsException extends AlreadyExistsException {

    public RoleAlreadyExistsException(Long roleId, String roleName) {
        super("Role", roleId, roleName);
    }
}

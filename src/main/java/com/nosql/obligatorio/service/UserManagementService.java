package com.nosql.obligatorio.service;

import com.nosql.obligatorio.entity.CodigoError;
import com.nosql.obligatorio.entity.Usuario;

import java.util.ArrayList;
import java.util.List;

public interface UserManagementService {

    CodigoError add(Usuario usuario) throws Exception;

    CodigoError addRole(Usuario usuario) throws Exception;

    CodigoError deleteRole(Usuario usuario) throws Exception;

    List<CodigoError> errores();

    String auth(Usuario usuario);
}

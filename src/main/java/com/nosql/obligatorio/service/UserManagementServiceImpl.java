package com.nosql.obligatorio.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.nosql.obligatorio.entity.CodigoError;
import com.nosql.obligatorio.entity.Usuario;
import com.nosql.obligatorio.exception.CustomException;
import com.nosql.obligatorio.firebase.FirebaseInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class UserManagementServiceImpl implements UserManagementService{

    @Autowired
    private FirebaseInitializer firebase;

    @Override
    public CodigoError add(Usuario usuario) throws Exception {

        Boolean existeUsuario = false;
        // -- getColeccionUsuarios() = firebase.getFirestore().collection("Usuarios");
        ApiFuture<DocumentSnapshot> querySnapshotApiFuture = getColeccionUsuarios().document(usuario.getCorreo()).get();
        CodigoError mensRet = new CodigoError();
        // -- Se usa codigo 200 en todos los casos, menos cuando se genera el error de usuario ya existente
        mensRet.setCodigo("200");
        DocumentSnapshot document = querySnapshotApiFuture.get();
        try {
            if (document.getId().equals(usuario.getCorreo()) && document.exists()){
                existeUsuario = true;
            }
            if (existeUsuario){
                throw new CustomException("El usuario con correo " + usuario.getCorreo() + " ya existe en el sistema.");
            }
        }  catch (Exception e) {
            mensRet.setCodigo("101");
            mensRet.setMensaje(e.getMessage());
            return mensRet;
        }

        Map<String, Object> docData = new HashMap<>();
        docData.put("pass", usuario.getPass());
        docData.put("nombre", usuario.getNombre());
        docData.put("apellido", usuario.getApellido());

        CollectionReference usuarios = getColeccionUsuarios();
        ApiFuture<WriteResult> writeResultApiFuture = usuarios.document(usuario.getCorreo()).set(docData);

        try{
            if (writeResultApiFuture.get() != null){
                mensRet.setMensaje("El usuario " + usuario.getCorreo() + " se agreg?? correctamente.");
                return mensRet;
            }
            mensRet.setMensaje("Hubo un problema al grabar el usuario " + usuario.getCorreo());
            return mensRet;
        } catch (Exception e) {
            mensRet.setMensaje(e.getMessage());
            return mensRet;
        }
    }

    @Override
    public CodigoError addRole(Usuario user) throws Exception {
        Boolean existeUsuario = false;
        Boolean passCorrecta = true;
        Usuario usuario = null;
        ApiFuture<DocumentSnapshot> querySnapshotApiFuture = getColeccionUsuarios().document(user.getCorreo()).get();
        CodigoError mensRet = new CodigoError();
        // -- Se usa codigo 200 en todos los casos, menos cuando se genera el error de usuario no existe en el sistema
        // -- o si la contrase??a ingresada no coincide
        mensRet.setCodigo("200");
        try {
            DocumentSnapshot document = querySnapshotApiFuture.get();
                if (document.getId().equals(user.getCorreo()) && document.exists()){
                    existeUsuario = true;
                    usuario = document.toObject(Usuario.class);
                    usuario.setCorreo(user.getCorreo());
                    if (!usuario.getPass().equals(user.getPass())){
                        passCorrecta = false;
                    }
                }
            if (!existeUsuario){
                mensRet.setCodigo("102");
                throw new CustomException("El usuario con correo " + user.getCorreo() + " NO existe en el sistema."  );
            }
            if (!passCorrecta) {
                mensRet.setCodigo("104");
                throw new CustomException("La contrase??a ingresada no coincide.");
            }
        }  catch (Exception e) {
            mensRet.setMensaje(e.getMessage());
            return mensRet;
        }

        ArrayList<String> rolesNuevos = new ArrayList<>();
        if (usuario.getRoles() != null && !usuario.getRoles().isEmpty()) {
            rolesNuevos = usuario.getRoles();
        }

        for (int i = 0; i < user.getRoles().size(); i++) {
            String rol = user.getRoles().get(i);
            Boolean hayRol = false;
            if (usuario.getRoles() != null && !usuario.getRoles().isEmpty()) {
                hayRol = usuario.getRoles().contains(rol);
            }
            if (!hayRol) {
                rolesNuevos.add(rol);
            }
        }

        Map<String, Object> docData = new HashMap<>();
        docData.put("roles", rolesNuevos);

        CollectionReference usuarios = getColeccionUsuarios();
        ApiFuture<WriteResult> writeResultApiFuture = usuarios.document(user.getCorreo()).set(docData, SetOptions.merge());

        try{
            if (writeResultApiFuture.get() != null){
                mensRet.setMensaje("Se agregaron los roles correctamente.");
                return mensRet;
            }
            mensRet.setMensaje("Hubo un problema al grabar los roles del usuario " + user.getCorreo());
            return mensRet;
        } catch (Exception e) {
            mensRet.setMensaje(e.getMessage());
            return mensRet;
        }
    }

    @Override
    public CodigoError deleteRole(Usuario user) throws Exception {

        // -- Se verifica la existencia del usuario y la contrase??a
        Boolean existeUsuario = false;
        Boolean passCorrecta = true;
        Usuario usuario = null;
        ApiFuture<DocumentSnapshot> querySnapshotApiFuture = getColeccionUsuarios().document(user.getCorreo()).get();
        CodigoError mensRet = new CodigoError();
        // -- Se usa codigo 200 en todos los casos, menos cuando se genera el error de usuario no existe en el sistema
        // -- o si la contrase??a ingresada no coincide
        mensRet.setCodigo("200");
        try {
            DocumentSnapshot document = querySnapshotApiFuture.get();
                if (document.getId().equals(user.getCorreo()) && document.exists()){
                    existeUsuario = true;
                    usuario = document.toObject(Usuario.class);
                    usuario.setCorreo(user.getCorreo());
                    if (!usuario.getPass().equals(user.getPass())){
                        passCorrecta = false;
                    }
                }
            if (!existeUsuario){
                mensRet.setCodigo("102");
                throw new CustomException("El usuario con correo " + user.getCorreo() + " NO existe en el sistema."  );
            }
            if (!passCorrecta) {
                mensRet.setCodigo("104");
                throw new CustomException("La contrase??a ingresada no coincide.");
            }
        }  catch (Exception e) {
            mensRet.setMensaje(e.getMessage());
            return mensRet;
        }

        // -- Se verifican si los roles ingresados los tiene el usuario.
        Boolean hayMsgError = false;
        String msgError = "Los Roles ";
        ArrayList<String> rolesExistentesUsuario = usuario.getRoles();
        try{
                for (int i = 0; i < user.getRoles().size(); i++) {
                    String rol = user.getRoles().get(i);
                    Boolean hayRol = rolesExistentesUsuario.contains(rol);
                    if (!hayRol) {
                        hayMsgError = true;
                        msgError = msgError + rol + ", ";
                    }
                }

            if (hayMsgError){
                mensRet.setCodigo("103");
                msgError = msgError + "no est??n asociados al usuario con correo " + user.getCorreo();

                throw new CustomException(msgError);
            }
        } catch (CustomException e) {
            mensRet.setMensaje(e.getMessage());
            return mensRet;
        }

        // -- Se actualizan los roles del usuario
        ArrayList<String> rolesActualizados = new ArrayList<>();
        ArrayList<String> rolesBorrarUsuario = user.getRoles();
        if (rolesBorrarUsuario != null && !rolesBorrarUsuario.isEmpty()) {
            for (int i = 0; i < usuario.getRoles().size(); i++) {
                String rol = usuario.getRoles().get(i);        // -- roles que tiene el usuario
                Boolean hayRol = rolesBorrarUsuario.contains(rol);
                if (!hayRol) {
                    rolesActualizados.add(rol);
                }
            }
        }
        Map<String, Object> docData = new HashMap<>();
        docData.put("roles", rolesActualizados);
        CollectionReference usuarios = getColeccionUsuarios();
        ApiFuture<WriteResult> writeResultApiFuture = usuarios.document(user.getCorreo()).update(docData);
        try{
            if (writeResultApiFuture.get() != null){
                mensRet.setMensaje("Se borraron los roles ingresados correctamente.");
                return mensRet;
            }
            mensRet.setMensaje("Hubo un problema al borrar los roles del usuario " + user.getCorreo());
            return mensRet;

        } catch (CustomException e) {
            mensRet.setMensaje(e.getMessage());
            return mensRet;
        }
    }

    @Override
    public List<CodigoError> errores() {
        List<CodigoError> listaErrores = new ArrayList<>();
        CodigoError error;
        ApiFuture<QuerySnapshot> querySnapshotApiFuture = firebase.getFirestore().collection("CodigoErrores").get();
        try {
            for (DocumentSnapshot doc : querySnapshotApiFuture.get().getDocuments()){
                error = doc.toObject(CodigoError.class);
                error.setCodigo(doc.getId());
                listaErrores.add(error);
            }
            return listaErrores;
        }  catch (Exception e) {
            return null;
        }
    }

    @Override
    public String auth(String correo, String passw){
        // -- Se verifica la existencia del usuario y la contrase??a
        ApiFuture<DocumentSnapshot> querySnapshotApiFuture = getColeccionUsuarios().document(correo).get();

        try {
            DocumentSnapshot document = querySnapshotApiFuture.get();
            if (document.exists()){
                Map<String,Object> infoUser = document.getData();
                if (infoUser.get("pass").equals(passw)){
                    return "true";
                }

            }

        }  catch (Exception e) {
            e.printStackTrace();
        }

        return "false";

    }

    private CollectionReference getColeccionUsuarios() {
        return firebase.getFirestore().collection("Usuarios");
    }
}

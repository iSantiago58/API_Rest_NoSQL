package com.nosql.obligatorio.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.nosql.obligatorio.entity.CodigoError;
import com.nosql.obligatorio.entity.Usuario;
import com.nosql.obligatorio.service.UserManagementService;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.coyote.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = "/api")
public class ApiController {

    @Autowired
    private UserManagementService service;

    @GetMapping(value = "/errores",produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CodigoError>> errores() throws JSONException {
        List<CodigoError> codigosErrores = service.errores();
        return new ResponseEntity<>(codigosErrores, HttpStatus.OK);
    }

    @PostMapping(value = "/agregar/usuario")
    // -- @RequestBody -> Transforma el JSON al objeto que se quiere mapear
    public ResponseEntity add(@RequestBody Usuario usuario) throws Exception {
        return new ResponseEntity(service.add(usuario), HttpStatus.OK);
    }

    @PutMapping(value = "/agregar/roles")
    // -- @RequestBody -> Transforma el JSON al objeto que se quiere mapear
    public ResponseEntity addRole(@RequestBody Usuario usuario) throws Exception {
        return new ResponseEntity(service.addRole(usuario), HttpStatus.OK);
    }

    @DeleteMapping(value = "/eliminar/roles")
    public ResponseEntity deleteRole(@RequestBody Usuario usuario) throws Exception {
        return new ResponseEntity(service.deleteRole(usuario), HttpStatus.OK);
    }

    @GetMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> auth(@RequestBody Usuario usuario) {
        HashMap<String, String> map = new HashMap<>();
        String auth = service.auth(usuario);
        map.put("Autenticación", auth);
        return map;
    }

}

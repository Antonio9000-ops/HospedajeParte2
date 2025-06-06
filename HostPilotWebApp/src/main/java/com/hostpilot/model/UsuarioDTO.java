package com.hostpilot.model;

import java.time.format.DateTimeFormatter; 

/**
 * DTO para transferir datos de usuario de forma segura.
 
 */
public class UsuarioDTO {

    private Long id;
    private String nombre;
    private String apellido; 
    private String email;
    private String rol;
    private Integer edad; 
    private String genero;
    private String telefono; 
    private boolean activo;
    private String fechaCreacion;     
    private String fechaModificacion; 
    private String ultimoAcceso;   

    
    private static final DateTimeFormatter DTO_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // Constructor por defecto
    public UsuarioDTO() {}

    public UsuarioDTO(Usuario usuario) {
        if (usuario == null) {
          
            return;
        }
        this.id = usuario.getId();
        this.nombre = usuario.getNombre();
        this.apellido = usuario.getApellido(); // Añadido
        this.email = usuario.getEmail();
        this.rol = usuario.getRol();
        this.edad = usuario.getEdad(); // Añadido
        this.genero = usuario.getGenero(); // Añadido
        this.telefono = usuario.getTelefono(); // Añadido
        this.activo = usuario.isActivo();

        if (usuario.getFechaCreacion() != null) {
            
            this.fechaCreacion = usuario.getFechaCreacion().format(DTO_DATE_FORMATTER); 
        } else {
            this.fechaCreacion = null;
        }

        if (usuario.getFechaModificacion() != null) { 
            this.fechaModificacion = usuario.getFechaModificacion().format(DTO_DATE_FORMATTER);
        } else {
            this.fechaModificacion = null;
        }
        
        if (usuario.getUltimoAcceso() != null) { 
            this.ultimoAcceso = usuario.getUltimoAcceso().format(DTO_DATE_FORMATTER);
        } else {
            this.ultimoAcceso = null;
        }
    }

 
    public UsuarioDTO(Long id, String nombre, String apellido, String email, String rol,
                      Integer edad, String genero, String telefono, boolean activo,
                      String fechaCreacion, String fechaModificacion, String ultimoAcceso) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.rol = rol;
        this.edad = edad;
        this.genero = genero;
        this.telefono = telefono;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
        this.fechaModificacion = fechaModificacion;
        this.ultimoAcceso = ultimoAcceso;
    }

    // Getters y Setters 

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getFechaModificacion() { 
        return fechaModificacion;
    }

    public void setFechaModificacion(String fechaModificacion) { 
        this.fechaModificacion = fechaModificacion;
    }

    public String getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(String ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }

    @Override
    public String toString() {
        return "UsuarioDTO{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' +
                ", rol='" + rol + '\'' +
                ", edad=" + edad +
                ", genero='" + genero + '\'' +
                ", telefono='" + telefono + '\'' +
                ", activo=" + activo +
                ", fechaCreacion='" + fechaCreacion + '\'' +
                ", fechaModificacion='" + fechaModificacion + '\'' +
                ", ultimoAcceso='" + ultimoAcceso + '\'' +
                '}';
    }
}
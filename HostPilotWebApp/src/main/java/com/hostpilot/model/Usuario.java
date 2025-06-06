package com.hostpilot.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad Usuario que representa un usuario en el sistema.
 */
public class Usuario {

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String password; 
    private String rol;
    private Integer edad;
    private String genero;
    private String telefono;
    private boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    private LocalDateTime ultimoAcceso;

   
    public Usuario() {
        
    }

   
    public Usuario(Long id, String nombre, String apellido, String email, String password,
                   String rol, Integer edad, String genero, String telefono,
                   boolean activo, LocalDateTime fechaCreacion,
                   LocalDateTime fechaModificacion, LocalDateTime ultimoAcceso) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }


    public boolean esAdmin() {
        return "ADMIN".equalsIgnoreCase(this.rol);
    }

    // Métodos equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
   
        if (id != null && usuario.id != null) {
            return Objects.equals(id, usuario.id);
        }
        return Objects.equals(email, usuario.email); // Considerar si el email debe ser único
    }

    @Override
    public int hashCode() {
        
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' +
                ", rol='" + rol + '\'' +
                ", edad=" + edad +
                ", genero='" + genero + '\'' +
                ", telefono='" + telefono + '\'' +
                ", activo=" + activo +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaModificacion=" + fechaModificacion +
                ", ultimoAcceso=" + ultimoAcceso +
                '}';
    }
}
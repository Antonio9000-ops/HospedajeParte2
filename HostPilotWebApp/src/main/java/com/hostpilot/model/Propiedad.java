package com.hostpilot.model;

/**
 * Representa una propiedad de alquiler en el sistema.
 * Contiene todos los atributos de una propiedad.
 */
public class Propiedad {
    private int id;
    private String titulo;
    private String descripcion;
    private String direccion;
    private String ciudad;
    private double precioPorNoche;
    private int capacidad;
    private String tipo;
    private String imgUrl;
    private double lat;
    private double lng;
    private double rating;
    private int reviews; // <--- CORREGIDO para que coincida con la BD
    private int anfitrionId;

    // Constructor por defecto
    public Propiedad() {
    }

    // --- Getters y Setters ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public double getPrecioPorNoche() { return precioPorNoche; }
    public void setPrecioPorNoche(double precioPorNoche) { this.precioPorNoche = precioPorNoche; }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }



    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getImgUrl() { return imgUrl; }
    public void setImgUrl(String imgUrl) { this.imgUrl = imgUrl; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    
    // --- MÉTODOS CORREGIDOS para que coincidan con el campo 'reviews' ---
    public int getReviews() { return reviews; }
    public void setReviews(int reviews) { this.reviews = reviews; }

    public int getAnfitrionId() { return anfitrionId; }
    public void setAnfitrionId(int anfitrionId) { this.anfitrionId = anfitrionId; }
}
package com.hostpilot.model;

public class Propiedad {
    private int id; // Cambiado a int para que coincida con la BD
    private String titulo; // Cambiado de 'nombre' a 'titulo'
    private String direccion;
    private String ciudad;
    private double precioPorNoche; // Cambiado de 'precio' a 'precioPorNoche'
    private int capacidad;
    private String tipo;
    private String descripcion;
    private double lat;
    private double lng;
    private double rating; // Este campo no está en tu BD, podría causar un error si no se maneja.
    private int reviews;   // Este campo tampoco está en tu BD.
    private String imgUrl; // Este campo tampoco está en tu BD, asegúrate de añadirlo.
    private int anfitrionId;

    public Propiedad() {
    }

    // GETTERS Y SETTERS
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

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
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    // Campos que usabas antes pero no veo en la estructura actual de tu BD.
    // Si no los vas a usar, puedes borrarlos. Si los necesitas, tienes que AÑADIRLOS a tu tabla en phpMyAdmin.
    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getReviews() { return reviews; }
    public void setReviews(int reviews) { this.reviews = reviews; }

    public String getImgUrl() { return imgUrl; }
    public void setImgUrl(String imgUrl) { this.imgUrl = imgUrl; }
    
    public int getAnfitrionId() { return anfitrionId; }
    public void setAnfitrionId(int anfitrionId) { this.anfitrionId = anfitrionId; }
}
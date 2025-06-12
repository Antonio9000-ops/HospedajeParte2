package com.hostpilot.model;

public class Propiedad {
    private int id;
    private String nombre;
    private double lat;
    private double lng;
    private String ciudad;
    private double precio;
    private double rating;
    private int reviews;
    private String imgUrl;

    // Constructor
    public Propiedad(int id, String nombre, double lat, double lng, String ciudad, double precio, double rating, int reviews, String imgUrl) {
        this.id = id;
        this.nombre = nombre;
        this.lat = lat;
        this.lng = lng;
        this.ciudad = ciudad;
        this.precio = precio;
        this.rating = rating;
        this.reviews = reviews;
        this.imgUrl = imgUrl;
    }

    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public double getLat() { return lat; }
    public double getLng() { return lng; }
    public String getCiudad() { return ciudad; }
    public double getPrecio() { return precio; }
    public double getRating() { return rating; }
    public int getReviews() { return reviews; }
    public String getImgUrl() { return imgUrl; }
}
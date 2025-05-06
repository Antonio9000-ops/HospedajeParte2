<?php
$host = 'localhost'; // O usa 127.0.0.1
$usuario = 'root';   // Cambia si tu usuario de MySQL es diferente
$contrasena = '';    // Cambia si tu MySQL tiene contraseña
$base_datos = 'usuarios_db';

$conn = new mysqli($host, $usuario, $contrasena, $base_datos);

// Verificar conexión
if ($conn->connect_error) {
    die("Conexión fallida: " . $conn->connect_error);
}
?>

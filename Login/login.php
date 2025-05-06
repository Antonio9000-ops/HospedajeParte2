<?php
require 'conexion.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $email = $_POST['email'];
    $password = $_POST['password'];

    $stmt = $conn->prepare("SELECT id, password FROM usuarios WHERE email = ?");
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $stmt->store_result();

    if ($stmt->num_rows === 1) {
        $stmt->bind_result($id, $hash);
        $stmt->fetch();

        if (password_verify($password, $hash)) {
            session_start();
            $_SESSION['usuario_id'] = $id;
            echo "<script>alert('¡Bienvenido!'); window.location.href='bienvenido.php';</script>";
        } else {
            echo "<script>alert('Contraseña incorrecta.'); window.history.back();</script>";
        }
    } else {
        echo "<script>alert('Correo no registrado.'); window.history.back();</script>";
    }

    $stmt->close();
    $conn->close();
}
?>

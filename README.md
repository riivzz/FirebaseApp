# Gestión de Usuarios con Firebase

Aplicación Android nativa desarrollada en Java para la gestión CRUD de usuarios en tiempo real mediante Firebase Firestore, con sistema de validación de datos para DNI y correo electrónico.

## ⚠️ Configuración de Firebase (Importante)

Por motivos de seguridad, el archivo de credenciales de la base de datos no está incluido en este repositorio. Para poder compilar y ejecutar la aplicación en tu entorno local, debes vincular tu propio proyecto de Firebase:

1. Crea un nuevo proyecto en la [Consola de Firebase](https://console.firebase.google.com/).
2. Registra una nueva aplicación de Android asegurándote de usar el mismo nombre de paquete del proyecto.
3. Habilita el servicio **Firestore Database** en modo prueba o configura las reglas de seguridad.
4. Descarga el archivo `google-services.json` que te proporciona Firebase.
5. Coloca ese archivo dentro de la carpeta `app/` de este proyecto.
6. Sincroniza el proyecto con Gradle y listo para ejecutar.

# MedidoresApp 📊

¡Hola! Esta es **MedidoresApp**, una aplicación sencilla y eficiente para llevar el control de tus consumos de servicios básicos como **Agua, Luz y Gas**. 

La idea de la app es que puedas registrar las lecturas de tus medidores mes a mes, ver el historial y organizar la información como mejor te convenga para entender tus consumos.

## ✨ Características principales

*   **Registro de Mediciones**: Anota la lectura, el tipo de servicio y la fecha (con un cómodo selector de calendario).
*   **Edición y Borrado**: ¿Te equivocaste en un número? No pasa nada, puedes editar cualquier registro o eliminarlo si ya no lo necesitas.
*   **Sistema de Ordenamiento Avanzado**: Puedes organizar tu lista de mediciones por:
    *   📅 **Fecha**: De la más reciente a la más antigua (y viceversa).
    *   🔢 **Lectura**: De mayor a menor consumo para detectar picos.
    *   💧 **Tipo de Servicio**: Agrupa todas las de Agua, luego Luz y luego Gas.
*   **Base de Datos Local**: Tus datos se guardan en tu teléfono usando **Room**, así que no necesitas internet para usar la app.
*   **Diseño Moderno**: Interfaz limpia basada en Material Design 3 con soporte para modo claro y oscuro.

## 🛠️ Tecnologías utilizadas

Este proyecto fue construido con las mejores prácticas de desarrollo Android actual:

*   **Kotlin**: El lenguaje principal, moderno y seguro.
*   **MVVM (Model-View-ViewModel)**: Arquitectura que separa la lógica de los datos de la interfaz visual.
*   **Room Database**: Para el almacenamiento persistente de datos de forma local.
*   **Jetpack Navigation**: Para movernos entre pantallas de forma fluida.
*   **View Binding**: Para conectar el código con el diseño de forma segura.
*   **Coroutines**: Para que la app sea rápida y no se trabe al guardar datos.

## 📂 Estructura del Proyecto

El código está organizado de forma fácil de seguir:

*   `data/`: Aquí vive todo lo relacionado con la base de datos (Entidades, DAOs y el Repositorio).
*   `ui/`: Aquí están los Fragmentos, que son las pantallas que ves (Listado y Registro).
*   `viewmodel/`: El "cerebro" que procesa los datos y el ordenamiento antes de mostrarlos.
*   `adapter/`: La lógica que le dice a la lista cómo dibujar cada tarjetita de medición.

---
*Desarrollado para ayudar a controlar el consumo del hogar.*
v

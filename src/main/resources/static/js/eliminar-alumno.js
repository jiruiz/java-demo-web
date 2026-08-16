async function eliminarAlumno(dni) {
    const confirmar = confirm(
        `¿Seguro que querés eliminar al alumno con DNI ${dni}?`
    );

    if (!confirmar) {
        return;
    }

    try {
        const respuesta = await fetch(`/api/alumnos/${dni}`, {
            method: "DELETE"
        });

        if (respuesta.ok) {
            document.getElementById("mensaje").textContent =
                "Alumno eliminado correctamente.";

            listarAlumnos();
        } else if (respuesta.status === 404) {
            document.getElementById("mensaje").textContent =
                "El alumno no existe.";
        } else {
            document.getElementById("mensaje").textContent =
                "No se pudo eliminar el alumno.";
        }
    } catch (error) {
        document.getElementById("mensaje").textContent =
            "Error de conexión con el servidor.";

        console.error(error);
    }
}
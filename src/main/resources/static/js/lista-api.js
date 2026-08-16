async function listarAlumnosApi() {
    try {
        const respuesta = await fetch("/api/alumnos");

        if (!respuesta.ok) {
            throw new Error("Error al consultar la API");
        }

        const alumnos = await respuesta.json();
        const tabla = document.getElementById("tablaAlumnos");

        tabla.innerHTML = "";

        alumnos.forEach(function (alumno) {
            const fila = document.createElement("tr");

            fila.innerHTML = `
                <td>${alumno.dni}</td>
                <td>${alumno.nombre}</td>
                <td>${alumno.apellido}</td>
                <td>${alumno.promedio}</td>
            `;

            tabla.appendChild(fila);
        });

    } catch (error) {
        document.getElementById("mensaje").textContent =
            "No se pudieron cargar los alumnos.";

        console.log(error);
    }
}

document.addEventListener(
    "DOMContentLoaded",
    listarAlumnosApi
);
async function listarAlumnos() {
    try {
        const respuesta = await fetch("/api/alumnos");
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
                <td>
                    <button
                        type="button"
                        class="botonEditar">
                        Editar
                    </button>

                    <button
                        type="button"
                        onclick="eliminarAlumno(${alumno.dni})">
                        Eliminar
                    </button>
                </td>
            `;

            const botonEditar =
                fila.querySelector(".botonEditar");

            botonEditar.addEventListener("click", function () {
                mostrarFormularioEditar(alumno);
            });

            tabla.appendChild(fila);
        });

    } catch (error) {
        document.getElementById("mensaje").textContent =
            "Error al cargar los alumnos.";

        console.log(error);
    }
}

document.addEventListener("DOMContentLoaded", listarAlumnos);
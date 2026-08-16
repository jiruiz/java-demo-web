async function listarAlumnos() {
    try {
        const respuesta = await fetch("/api/alumnos");

        if (!respuesta.ok) {
            throw new Error("No se pudo cargar la lista");
        }

        const alumnos = await respuesta.json();
        const tabla = document.getElementById("tablaAlumnos");

        tabla.innerHTML = "";

        alumnos.forEach(function (alumno) {
            crearFila(alumno);
        });

    } catch (error) {
        document.getElementById("mensaje").textContent =
            "Error al cargar los alumnos.";

        console.log(error);
    }
}

function crearFila(alumno) {
    const tabla = document.getElementById("tablaAlumnos");
    const fila = document.createElement("tr");

    mostrarFilaNormal(fila, alumno);

    tabla.appendChild(fila);
}

function mostrarFilaNormal(fila, alumno) {
    fila.innerHTML = `
        <td>${alumno.dni}</td>
        <td>${alumno.nombre}</td>
        <td>${alumno.apellido}</td>
        <td>${alumno.promedio}</td>
        <td>
            <button type="button" class="botonEditar">
                Editar
            </button>

            <button
                type="button"
                onclick="eliminarAlumno(${alumno.dni})">
                Eliminar
            </button>
        </td>
    `;

    const botonEditar = fila.querySelector(".botonEditar");

    botonEditar.addEventListener("click", function () {
        mostrarFilaEditable(fila, alumno);
    });
}

function mostrarFilaEditable(fila, alumno) {
    fila.innerHTML = `
        <td>${alumno.dni}</td>

        <td>
            <input
                type="text"
                class="editarNombre"
                value="${alumno.nombre}">
        </td>

        <td>
            <input
                type="text"
                class="editarApellido"
                value="${alumno.apellido}">
        </td>

        <td>
            <input
                type="number"
                class="editarPromedio"
                step="0.01"
                value="${alumno.promedio}">
        </td>

        <td>
            <button type="button" class="botonGuardar">
                Guardar
            </button>

            <button type="button" class="botonCancelar">
                Cancelar
            </button>
        </td>
    `;

    const botonGuardar = fila.querySelector(".botonGuardar");
    const botonCancelar = fila.querySelector(".botonCancelar");

    botonGuardar.addEventListener("click", function () {
        guardarCambios(fila, alumno);
    });

    botonCancelar.addEventListener("click", function () {
        mostrarFilaNormal(fila, alumno);
    });
}

async function guardarCambios(fila, alumno) {
    const nombre = fila.querySelector(".editarNombre").value;
    const apellido = fila.querySelector(".editarApellido").value;
    const promedio = fila.querySelector(".editarPromedio").value;

    if (nombre === "" || apellido === "" || promedio === "") {
        document.getElementById("mensaje").textContent =
            "Completá todos los campos.";

        return;
    }

    const alumnoModificado = {
        dni: alumno.dni,
        nombre: nombre,
        apellido: apellido,
        promedio: Number(promedio),

        // Se mantienen los campos que no aparecen en la tabla
        fechaNacimiento: alumno.fechaNacimiento,
        estado: alumno.estado,
        cantidadMateriasAprobadas:
            alumno.cantidadMateriasAprobadas,
        fechaIngreso: alumno.fechaIngreso
    };

    try {
        const respuesta = await fetch(
            "/api/alumnos/" + alumno.dni,
            {
                method: "PUT",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify(alumnoModificado)
            }
        );

        if (respuesta.ok) {
            const alumnoActualizado = await respuesta.json();

            document.getElementById("mensaje").textContent =
                "Alumno modificado correctamente.";

            mostrarFilaNormal(fila, alumnoActualizado);
        } else {
            document.getElementById("mensaje").textContent =
                "No se pudo modificar el alumno.";
        }

    } catch (error) {
        document.getElementById("mensaje").textContent =
            "Error de conexión con el servidor.";

        console.log(error);
    }
}

document.addEventListener("DOMContentLoaded", listarAlumnos);
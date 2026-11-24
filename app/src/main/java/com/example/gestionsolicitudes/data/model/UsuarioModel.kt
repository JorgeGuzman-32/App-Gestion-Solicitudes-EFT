package com.example.gestionsolicitudes.data.model

data class UsuarioModel(
    val username: String,
    val contrasena: String,
    val nombre: String,
    val perfil: String, // "Cliente", "Administrador / Coordinador", "Técnico / Ejecutor"
    val userId: String
)

object UsuariosRepository {
    val listaUsuarios = listOf(
        UsuarioModel(
            username = "cliente",
            contrasena = "cliente123",
            nombre = "Juan Pérez (Cliente)",
            perfil = "Cliente",
            userId = "CLI-001"
        ),
        UsuarioModel(
            username = "cliente_demo",
            contrasena = "duoc2026",
            nombre = "Cliente Demo",
            perfil = "Cliente",
            userId = "CLI-001"
        ),
        UsuarioModel(
            username = "admin",
            contrasena = "admin123",
            nombre = "Jorge Guzmán (Coordinador Admin)",
            perfil = "Administrador / Coordinador",
            userId = "ADM-001"
        ),
        UsuarioModel(
            username = "tecnico1",
            contrasena = "tecnico123",
            nombre = "Francisco Órdenes (Técnico Ejecutor)",
            perfil = "Técnico / Ejecutor",
            userId = "TEC-001"
        ),
        UsuarioModel(
            username = "tecnico2",
            contrasena = "tecnico123",
            nombre = "Pedro Pascal (Técnico Especialista)",
            perfil = "Técnico / Ejecutor",
            userId = "TEC-002"
        )
    )

    fun autenticar(usernameInput: String, contrasenaInput: String): ResultadoAutenticacion {
        val userFound = listaUsuarios.find {
            it.username.equals(usernameInput.trim(), ignoreCase = true)
        }

        if (userFound == null) {
            return ResultadoAutenticacion.Error("El usuario '$usernameInput' no existe en el sistema.")
        }

        if (userFound.contrasena != contrasenaInput.trim()) {
            return ResultadoAutenticacion.Error("La contraseña ingresada es incorrecta.")
        }

        return ResultadoAutenticacion.Exito(userFound)
    }
}

sealed class ResultadoAutenticacion {
    data class Exito(val usuario: UsuarioModel) : ResultadoAutenticacion()
    data class Error(val mensaje: String) : ResultadoAutenticacion()
}

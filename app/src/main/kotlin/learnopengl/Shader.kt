package learnopengl

import org.lwjgl.opengl.GL30C.*
import kotlin.system.exitProcess

class Shader(
    vertexPath: String,
    fragmentPath: String
) {
    var id: Int = 0

    init {
        // 1. retrieve the vertex/fragment source code from filePath
        val vertexCode: String? = Shader::class.java.getResource(vertexPath)?.readText()
        val fragmentCode: String? = Shader::class.java.getResource(fragmentPath)?.readText()
        if (null == vertexCode) {
            System.err.print("ERROR::SHADER::FILE_NOT_SUCCESSFULLY_READ:$vertexPath")
            exitProcess(-1)
        }
        if (null == fragmentCode) {
            System.err.print("ERROR::SHADER::FILE_NOT_SUCCESSFULLY_READ:$fragmentPath")
            exitProcess(-1)
        }

        // 2. compile shaders
        // vertex shader
        val vertex = glCreateShader(GL_VERTEX_SHADER)
        glShaderSource(vertex, vertexCode)
        glCompileShader(vertex)
        checkCompileErrors(vertex, "VERTEX")
        // fragment shader
        val fragment = glCreateShader(GL_FRAGMENT_SHADER)
        glShaderSource(fragment, fragmentCode)
        glCompileShader(fragment)
        checkCompileErrors(fragment, "FRAGMENT")
        // shader program
        id = glCreateProgram()
        glAttachShader(id, vertex)
        glAttachShader(id, fragment)
        glLinkProgram(id)
        checkCompileErrors(id, "PROGRAM")
        glDeleteShader(vertex)
        glDeleteShader(fragment)
    }

    // activate the shader
    // ------------------------------------------------------------------------
    fun use()
    {
        glUseProgram(id)
    }
    // utility uniform functions
    // ------------------------------------------------------------------------
    fun setBool(name: String, value: Boolean)
    {
        glUniform1i(glGetUniformLocation(id, name), if (value) 1 else 0)
    }
    // ------------------------------------------------------------------------
    fun setInt(name: String, value: Int)
    {
        glUniform1i(glGetUniformLocation(id, name), value)
    }
    // ------------------------------------------------------------------------
    fun setFloat(name: String, value: Float)
    {
        glUniform1f(glGetUniformLocation(id, name), value)
    }

    // utility function for checking shader compilation/linking errors.
    // ------------------------------------------------------------------------
    private fun checkCompileErrors(shader: Int, type: String) {
        if (type != "PROGRAM") {
            if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
                val infoLog = glGetShaderInfoLog(shader)
                System.err.print("ERROR::SHADER_COMPILATION_ERROR of type: $type\n$infoLog")
            }
        } else {
            if (glGetProgrami(shader, GL_LINK_STATUS) == GL_FALSE) {
                val infoLog = glGetProgramInfoLog(shader)
                System.err.print("ERROR::PROGRAM_LINKING_ERROR of type: $type\n$infoLog")
            }
        }
    }
}

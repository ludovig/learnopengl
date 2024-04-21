package learnopengl.a_getting_started

import glm_.vec3.Vec3
import org.lwjgl.BufferUtils.createIntBuffer
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWFramebufferSizeCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL30C.*
import org.lwjgl.system.MemoryUtil.NULL

fun main() {
    with(HelloTriangleIndexed()) {
        main()
    }
}

class HelloTriangleIndexed {

    // settings
    companion object {
        val WINDOW_SIZE = Pair(800, 600)

        const val VERTEX_SHADER_SOURCE = """
            #version 330 core
            layout (location = 0) in vec3 aPos;

            void main()
            {
                gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);
            }
        """

        const val FRAGMENT_SHADER_SOURCE = """
            #version 330 core
            out vec4 FragColor;

            void main()
            {
                FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);
            }
        """
    }

    private var window: Long = 0

    fun main() {
        // glfw: initialize and configure
        // ------------------------------
        glfwInit()

        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)

        // glfw window creation
        // --------------------
        window = glfwCreateWindow(WINDOW_SIZE.first, WINDOW_SIZE.second, "Learn OpenGL", NULL, NULL)
        if (window == NULL) {
            glfwTerminate()
            throw RuntimeException("Failed to create the GLFW window")
        }
        glfwMakeContextCurrent(window)
        glfwSetFramebufferSizeCallback(window, framebufferSizeCallback)

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        GL.createCapabilities()

        // build and compile our shader program
        // ------------------------------------
        // vertex shader
        val vertexShader = glCreateShader(GL_VERTEX_SHADER)
        glShaderSource(vertexShader, VERTEX_SHADER_SOURCE)
        glCompileShader(vertexShader)
        // check for shader compile error
        if (glGetShaderi(vertexShader, GL_COMPILE_STATUS) == GL_FALSE) {
            val infoLog = glGetShaderInfoLog(vertexShader)
            System.err.print("ERROR::SHADER::VERTEX::COMPILATION_FAILED\n$infoLog")
        }
        // fragment shader
        val fragmentShader = glCreateShader(GL_FRAGMENT_SHADER)
        glShaderSource(fragmentShader, FRAGMENT_SHADER_SOURCE)
        glCompileShader(fragmentShader)
        // check for shader compile error
        if (glGetShaderi(fragmentShader, GL_COMPILE_STATUS) == GL_FALSE) {
            val infoLog = glGetShaderInfoLog(fragmentShader)
            System.err.print("ERROR::SHADER::FRAGMENT::COMPILATION_FAILED\n$infoLog")
        }
        // link shaders
        val shaderProgram : Int = glCreateProgram()
        glAttachShader(shaderProgram, vertexShader)
        glAttachShader(shaderProgram, fragmentShader)
        glLinkProgram(shaderProgram)
        // check for linking errors
        if (glGetProgrami(shaderProgram, GL_LINK_STATUS) == GL_FALSE) {
            val infoLog = glGetProgramInfoLog(shaderProgram)
            System.err.print("ERROR::SHADER::PROGRAM::LINKING_FAILED\n$infoLog")
        }
        glDeleteShader(vertexShader)
        glDeleteShader(fragmentShader)

        // set up vertex data (and buffer(s)) and configure vertex attributes
        // ------------------------------------------------------------------
        val vertices = floatArrayOf(
             0.5f,  0.5f, 0f, // top right
             0.5f, -0.5f, 0f, // bottom right
            -0.5f, -0.5f, 0f, // bottom left
            -0.5f,  0.5f, 0f  // top left
        )
        val indices = intArrayOf( // note that we start from 0!
            0, 1, 3, // first Triangle
            1, 2, 3  // second Triangle
        )

        val vao = createIntBuffer(1)
        val vbo = createIntBuffer(1)
        val ebo = createIntBuffer(1)
        glGenVertexArrays(vao)
        glGenBuffers(vbo)
        glGenBuffers(ebo)
        // bind the Vertex Array Object first, then bind and set vertex buffer(s), and then configure vertex attributes(s).
        glBindVertexArray(vao.get(0))

        glBindBuffer(GL_ARRAY_BUFFER, vbo.get(0))
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo.get(0))
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)

        glVertexAttribPointer(0, Vec3.length, GL_FLOAT, false, Vec3.size, 0)
        glEnableVertexAttribArray(0)

        // note that this is allowed, the call to glVertexAttribPointer registered VBO as the vertex attribute's bound vertex buffer object so afterward we can safely unbind
        glBindBuffer(GL_ARRAY_BUFFER, 0)

        // remember: do NOT unbind the EBO while a VAO is active as the bound element buffer object IS stored in the VAO; keep the EBO bound.
        //glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        // You can unbind the VAO afterward so other VAO calls won't accidentally modify this VAO, but this rarely happens. Modifying other
        // VAOs requires a call to glBindVertexArray anyway, so we generally don't unbind VAOs (nor VBOs) when it's not directly necessary.
        glBindVertexArray(0)

        // uncomment this call to draw in wireframe polygons.
        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        // render loop
        // -----------
        while (!glfwWindowShouldClose(window)) {
            // input
            // -----
            processInput(window)

            // render
            // ------
            glClearColor(0.2f, 0.3f, 0.3f, 1.0f)
            glClear(GL_COLOR_BUFFER_BIT)

            // draw our first triangle
            glUseProgram(shaderProgram)
            glBindVertexArray(vao.get(0)) // seeing as we only have a single VAO there's no need to bind it every time, but we'll do so to keep things a bit more organized
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0)
            // glBindVertexArray(0) // no need to unbind it every time

            // glfw: swap buffers and poll IO events (keys pressed/released, mouse moved etc.)
            // -------------------------------------------------------------------------------
            glfwSwapBuffers(window)
            glfwPollEvents()
        }

        // optional: de-allocate all resources once they've outlived their purpose:
        // ------------------------------------------------------------------------
        glDeleteVertexArrays(vao)
        glDeleteBuffers(vbo)
        glDeleteBuffers(ebo)
        glDeleteProgram(shaderProgram)

        // glfw: terminate, clearing all previously allocated GLFW resources.
        // ------------------------------------------------------------------
        glfwTerminate()
    }

    // process all input: query GLFW whether relevant keys are pressed/released this frame and react accordingly
    // ---------------------------------------------------------------------------------------------------------
    private fun processInput(window: Long) {
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            glfwSetWindowShouldClose(window, true)
        }
    }

    // glfw: whenever the window size changed (by OS or user resize) this callback function executes
    // ---------------------------------------------------------------------------------------------
    private val framebufferSizeCallback = object : GLFWFramebufferSizeCallback() {
        override fun invoke(
            window: Long,
            width: Int,
            height: Int,
        ) {
            // make sure the viewport matches the new window dimensions; note that width and
            // height will be significantly larger than specified on retina displays.
            glViewport(0, 0, width, height)
        }
    }
}

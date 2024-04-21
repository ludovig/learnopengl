package learnopengl.a_getting_started

import glm_.vec3.Vec3
import org.lwjgl.BufferUtils.createIntBuffer
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWFramebufferSizeCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL30C.*
import org.lwjgl.system.MemoryUtil.NULL
import learnopengl.Shader

fun main() {
    with(ShadersClass()) {
        main()
    }
}

class ShadersClass {

    // settings
    companion object {
        val WINDOW_SIZE = Pair(800, 600)
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

        val ourShader = Shader(
            "/shaders/a_getting_started/shaders_class/shaders.vert",
            "/shaders/a_getting_started/shaders_class/shaders.frag",
        ) // you can name your shader files however you like

        // set up vertex data (and buffer(s)) and configure vertex attributes
        // ------------------------------------------------------------------
        val vertices = floatArrayOf(
            // positions         // colors
             0.5f, -0.5f, 0.0f,  1.0f, 0.0f, 0.0f,   // bottom right
            -0.5f, -0.5f, 0.0f,  0.0f, 1.0f, 0.0f,   // bottom left
             0.0f,  0.5f, 0.0f,  0.0f, 0.0f, 1.0f    // top
        )

        val vao = createIntBuffer(1)
        val vbo = createIntBuffer(1)
        glGenVertexArrays(vao)
        glGenBuffers(vbo)
        // bind the Vertex Array Object first, then bind and set vertex buffer(s), and then configure vertex attributes(s).
        glBindVertexArray(vao.get(0))

        glBindBuffer(GL_ARRAY_BUFFER, vbo.get(0))
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)

        // position attribute
        glVertexAttribPointer(0, Vec3.length, GL_FLOAT, false, Vec3.size * 2, 0)
        glEnableVertexAttribArray(0)

        // color attribute
        glVertexAttribPointer(1, Vec3.length, GL_FLOAT, false, Vec3.size * 2, Vec3.size.toLong())
        glEnableVertexAttribArray(1)

        // You can unbind the VAO afterward so other VAO calls won't accidentally modify this VAO, but this rarely happens. Modifying other
        // VAOs requires a call to glBindVertexArray anyway, so we generally don't unbind VAOs (nor VBOs) when it's not directly necessary.
        //glBindVertexArray(0)

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

            // be sure to activate the shader
            ourShader.use()

            // render the triangle
            glBindVertexArray(vao.get(0))
            glDrawArrays(GL_TRIANGLES, 0, 3)

            // glfw: swap buffers and poll IO events (keys pressed/released, mouse moved etc.)
            // -------------------------------------------------------------------------------
            glfwSwapBuffers(window)
            glfwPollEvents()
        }

        // optional: de-allocate all resources once they've outlived their purpose:
        // ------------------------------------------------------------------------
        glDeleteVertexArrays(vao)
        glDeleteBuffers(vbo)

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

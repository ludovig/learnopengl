package learnopengl.a_getting_started

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWFramebufferSizeCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil.NULL

fun main() {
    with(HelloWindowClear()) {
        main()
    }
}

open class HelloWindowClear {

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
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities()

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

            // glfw: swap buffers and poll IO events (keys pressed/released, mouse moved etc.)
            // -------------------------------------------------------------------------------
            glfwSwapBuffers(window)
            glfwPollEvents()
        }

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

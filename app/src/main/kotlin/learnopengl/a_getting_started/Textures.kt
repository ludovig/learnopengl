package learnopengl.a_getting_started

import glm_.vec3.Vec3
import glm_.vec2.Vec2
import org.lwjgl.BufferUtils.createIntBuffer
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWFramebufferSizeCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL30C.*
import org.lwjgl.system.MemoryUtil.NULL
import learnopengl.Shader
import learnopengl.toBuffer
import learnopengl.Image

fun main() {
    with(Textures()) {
        main()
    }
}

class Textures {

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

        // build and compile our shader program
        // ------------------------------------
        val ourShader = Shader(
            "/shaders/a_getting_started/textures/textures.vert",
            "/shaders/a_getting_started/textures/textures.frag",
        )

        // set up vertex data (and buffer(s)) and configure vertex attributes
        // ------------------------------------------------------------------
        val vertices = floatArrayOf(
            // positions          // colors           // texture coords
             0.5f,  0.5f, 0.0f,   1.0f, 0.0f, 0.0f,   1.0f, 1.0f, // top right
             0.5f, -0.5f, 0.0f,   0.0f, 1.0f, 0.0f,   1.0f, 0.0f, // bottom right
            -0.5f, -0.5f, 0.0f,   0.0f, 0.0f, 1.0f,   0.0f, 0.0f, // bottom left
            -0.5f,  0.5f, 0.0f,   1.0f, 1.0f, 0.0f,   0.0f, 1.0f  // top left
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

        glBindVertexArray(vao.get(0))

        glBindBuffer(GL_ARRAY_BUFFER, vbo.get(0))
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo.get(0))
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)

        // position attribute
        glVertexAttribPointer(0, Vec3.length, GL_FLOAT, false, Vec3.size * 2 + Vec2.size, 0)
        glEnableVertexAttribArray(0)
        // color attribute
        glVertexAttribPointer(1, Vec3.length, GL_FLOAT, false, Vec3.size * 2 + Vec2.size, Vec3.size.toLong())
        glEnableVertexAttribArray(1)
        // texture attribute
        glVertexAttribPointer(2, Vec2.length, GL_FLOAT, false, Vec3.size * 2 + Vec2.size, Vec3.size.toLong() * 2)
        glEnableVertexAttribArray(2)

        // load and create a texture
        // -------------------------
        val texture = createIntBuffer(1)
        glGenTextures(texture)
        glBindTexture(GL_TEXTURE_2D, texture.get(0)) // all upcoming GL_TEXTURE_2D operations now have effect on this texture object
        // set the texture wrapping parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)	// set texture wrapping to GL_REPEAT (default wrapping method)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
        // set texture filtering parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

        val image = Image("textures/container.jpg").readImage()
        val image2 = Image("textures/container.jpg")

        // ByteBuffered images used BGR instead of RGB
        glTexImage2D(
            GL_TEXTURE_2D,
            0,
            GL_RGB,
            image.width,
            image.height,
            0,
            GL_BGR,
            GL_UNSIGNED_BYTE,
            image.toBuffer()
        )
        glGenerateMipmap(GL_TEXTURE_2D)

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

            // bind texture
            glBindTexture(GL_TEXTURE_2D, texture.get(0))

            // be sure to activate the shader
            ourShader.use()

            // render the triangle
            glBindVertexArray(vao.get(0))
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0)

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

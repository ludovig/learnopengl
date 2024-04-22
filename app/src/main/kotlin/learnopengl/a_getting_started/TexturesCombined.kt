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
import org.lwjgl.opengl.EXTABGR

fun main() {
    with(TexturesCombined()) {
        main()
    }
}

class TexturesCombined {

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
            "/shaders/a_getting_started/textures_combined/textures.vert",
            "/shaders/a_getting_started/textures_combined/textures.frag",
        )

        // set up vertex data (and buffer(s)) and configure vertex attributes
        // ------------------------------------------------------------------
        val vertices = floatArrayOf(
            // positions          // colors           // texture coords
             0.5f,  0.5f, 0.0f,   1.0f, 0.0f, 0.0f,   1.0f, 0.0f, // top right
             0.5f, -0.5f, 0.0f,   0.0f, 1.0f, 0.0f,   1.0f, 1.0f, // bottom right
            -0.5f, -0.5f, 0.0f,   0.0f, 0.0f, 1.0f,   0.0f, 1.0f, // bottom left
            -0.5f,  0.5f, 0.0f,   1.0f, 1.0f, 0.0f,   0.0f, 0.0f  // top left
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
        val texture1 = createIntBuffer(1)
        val texture2 = createIntBuffer(1)
        // texture 1
        // ---------
        glGenTextures(texture1)
        glBindTexture(GL_TEXTURE_2D, texture1.get(0)) // all upcoming GL_TEXTURE_2D operations now have effect on this texture object
        // set the texture wrapping parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)	// set texture wrapping to GL_REPEAT (default wrapping method)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
        // set texture filtering parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

        val image1 = Image("textures/container.jpg").readImage()

        // ByteBuffered images used BGR instead of RGB
        glTexImage2D(
            GL_TEXTURE_2D,
            0,
            GL_RGB,
            image1.width,
            image1.height,
            0,
            GL_BGR,
            GL_UNSIGNED_BYTE,
            image1.toBuffer()
        )
        glGenerateMipmap(GL_TEXTURE_2D)
        // texture 2
        // ---------
        glGenTextures(texture2)
        glBindTexture(GL_TEXTURE_2D, texture2.get(0)) // all upcoming GL_TEXTURE_2D operations now have effect on this texture object
        // set the texture wrapping parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)	// set texture wrapping to GL_REPEAT (default wrapping method)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
        // set texture filtering parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

        val image2 = Image("textures/awesomeface.png").readImage()

        // ByteBuffered images used BRGA instead RGBA
        glTexImage2D(
            GL_TEXTURE_2D,
            0,
            GL_RGB,
            image2.width,
            image2.height,
            0,
            EXTABGR.GL_ABGR_EXT,
            GL_UNSIGNED_BYTE,
            image2.toBuffer()
        )
        glGenerateMipmap(GL_TEXTURE_2D)

        // tell opengl for each sampler to which texture unit it belongs to (only has to be done once)
        // -------------------------------------------------------------------------------------------
        ourShader.use() // don't forget to activate/use the shader before setting uniforms!
        // either set it manually like so:
        glUniform1i(glGetUniformLocation(ourShader.id, "texture1"), 0)
        // or set it via the texture class
        ourShader.setInt("texture2", 1)

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

            // bind textures on corresponding texture units
            glActiveTexture(GL_TEXTURE0)
            glBindTexture(GL_TEXTURE_2D, texture1.get(0))
            glActiveTexture(GL_TEXTURE1)
            glBindTexture(GL_TEXTURE_2D, texture2.get(0))

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
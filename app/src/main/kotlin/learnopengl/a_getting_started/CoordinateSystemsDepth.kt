package learnopengl.a_getting_started

import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import glm_.vec2.Vec2
import glm_.func.toRadians
import glm_.glm
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
    with(CoordinateSystemsDepth()) {
        main()
    }
}

class CoordinateSystemsDepth {

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

        // configure global opengl state
        // -----------------------------
        glEnable(GL_DEPTH_TEST)

        // build and compile our shader program
        // ------------------------------------
        val ourShader = Shader(
            "/shaders/a_getting_started/coordinate_systems/coordinate_systems.vert",
            "/shaders/a_getting_started/coordinate_systems/coordinate_systems.frag",
        )

        // set up vertex data (and buffer(s)) and configure vertex attributes
        // ------------------------------------------------------------------
        val vertices = floatArrayOf(
            -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,
             0.5f, -0.5f, -0.5f,  1.0f, 0.0f,
             0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
             0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
            -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,

            -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
             0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
             0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
             0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
            -0.5f,  0.5f,  0.5f,  0.0f, 1.0f,
            -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,

            -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
            -0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
            -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
            -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

             0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
             0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
             0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
             0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
             0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
             0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

            -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
             0.5f, -0.5f, -0.5f,  1.0f, 1.0f,
             0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
             0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
            -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,

            -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
             0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
             0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
             0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
            -0.5f,  0.5f,  0.5f,  0.0f, 0.0f,
            -0.5f,  0.5f, -0.5f,  0.0f, 1.0f
        )

        val vao = createIntBuffer(1)
        val vbo = createIntBuffer(1)

        glGenVertexArrays(vao)
        glGenBuffers(vbo)

        glBindVertexArray(vao.get(0))

        glBindBuffer(GL_ARRAY_BUFFER, vbo.get(0))
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)

        // position attribute
        glVertexAttribPointer(0, Vec3.length, GL_FLOAT, false, Vec3.size + Vec2.size, 0)
        glEnableVertexAttribArray(0)
        // texture attribute
        glVertexAttribPointer(1, Vec2.length, GL_FLOAT, false, Vec3.size + Vec2.size, Vec3.size.toLong())
        glEnableVertexAttribArray(1)

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
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT) // also clear the depth buffer now!

            // bind textures on corresponding texture units
            glActiveTexture(GL_TEXTURE0)
            glBindTexture(GL_TEXTURE_2D, texture1.get(0))
            glActiveTexture(GL_TEXTURE1)
            glBindTexture(GL_TEXTURE_2D, texture2.get(0))

            // activate shader
            ourShader.use()

            // create transformations
            val model = glm.rotate(Mat4(), glfwGetTime().toFloat() * toRadians(50f), 0.5f, 1.0f, 0f)
            val view = glm.translate(Mat4(), 0f, 0f, -3f)
            val projection = glm.perspective(toRadians(45f), WINDOW_SIZE.first.toFloat() / WINDOW_SIZE.second.toFloat(), 0.1f, 100.0f)

            // get matrix's uniform location and set matrix
            val modelLoc = glGetUniformLocation(ourShader.id, "model")
            val viewLoc = glGetUniformLocation(ourShader.id, "view")
            // pass them to the shaders (2 different ways)
            glUniformMatrix4fv(modelLoc,  false, model.toFloatArray())
            glUniformMatrix4fv(viewLoc,  false, view.toFloatArray())
            // note: currently we set the projection matrix each frame, but since the projection matrix rarely changes it's often best practice to set it outside the main loop only once.
            ourShader.setMat4("projection", projection)

            // render the triangle
            glBindVertexArray(vao.get(0))
            glDrawArrays(GL_TRIANGLES, 0, 36)

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

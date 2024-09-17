package learnopengl.a_getting_started

import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import glm_.vec2.Vec2
import glm_.func.toRadians
import glm_.glm
import org.lwjgl.BufferUtils.createIntBuffer
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWFramebufferSizeCallback
import org.lwjgl.glfw.GLFWCursorPosCallback
import org.lwjgl.glfw.GLFWScrollCallback
import org.lwjgl.opengl.GL30C.*
import org.lwjgl.opengl.GL
import org.lwjgl.system.MemoryUtil.NULL
import learnopengl.Shader
import learnopengl.toBuffer
import learnopengl.Image
import learnopengl.Camera
import learnopengl.CameraMovement.*
import org.lwjgl.opengl.EXTABGR

fun main() {
    with(CameraClass()) {
        main()
    }
}

class CameraClass {

    // settings
    companion object {
        val WINDOW_SIZE = Pair(800, 600)
    }

    private var window: Long = 0

    // camera
    private val camera = Camera(Vec3(0.0f, 0.0f, 3.0f))
    private var lastX = WINDOW_SIZE.first / 2f
    private var lastY = WINDOW_SIZE.second / 2f
    private var firstMouse = true

    // timing
    private var deltaTime = 0.0f // Time between current frame and last frame
    private var lastFrame = 0.0f // Time of last frame/

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
        glfwSetCursorPosCallback(window, mouseCallback)
        glfwSetScrollCallback(window, scrollCallback)

        // tell GLFW to capture our mouse
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED)

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        GL.createCapabilities()

        // configure global opengl state
        // -----------------------------
        glEnable(GL_DEPTH_TEST)

        // build and compile our shader program
        // ------------------------------------
        val ourShader = Shader(
            "/shaders/a_getting_started/coordinate_systems_multiple/coordinate_systems_multiple.vert",
            "/shaders/a_getting_started/coordinate_systems_multiple/coordinate_systems_multiple.frag",
        )

        // set up vertex data (and buffer(s)) and configure vertex attributes
        // ------------------------------------------------------------------
        val vertices = floatArrayOf(
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,
            0.5f, -0.5f, -0.5f, 1.0f, 0.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,

            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
            -0.5f, 0.5f, 0.5f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,

            -0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
            -0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, 1.0f, 0.0f,

            0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
            0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f,

            -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, -0.5f, -0.5f, 1.0f, 1.0f,
            0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,

            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, 0.0f, 0.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f
        )

        // world space positions of our cubes
        val cubePositions = arrayOf(
            Vec3(0f, 0f, 0f),
            Vec3(2f, 5f, -15f),
            Vec3(-1.5f, -2.2f, -2.5f),
            Vec3(-3.8f, -2f, -12.3f),
            Vec3(2.4f, -0.4f, -3.5f),
            Vec3(-1.7f, 3f, -7.5f),
            Vec3(1.3f, -2f, -2.5f),
            Vec3(1.5f, 2f, -2.5f),
            Vec3(1.5f, 0.2f, -1.5f),
            Vec3(-1.3f, 1f, -1.5f)
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
        glBindTexture(
            GL_TEXTURE_2D,
            texture1.get(0)
        ) // all upcoming GL_TEXTURE_2D operations now have effect on this texture object
        // set the texture wrapping parameters
        glTexParameteri(
            GL_TEXTURE_2D,
            GL_TEXTURE_WRAP_S,
            GL_REPEAT
        )    // set texture wrapping to GL_REPEAT (default wrapping method)
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
        glBindTexture(
            GL_TEXTURE_2D,
            texture2.get(0)
        ) // all upcoming GL_TEXTURE_2D operations now have effect on this texture object
        // set the texture wrapping parameters
        glTexParameteri(
            GL_TEXTURE_2D,
            GL_TEXTURE_WRAP_S,
            GL_REPEAT
        )    // set texture wrapping to GL_REPEAT (default wrapping method)
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
        ourShader.setInt("texture1", 0)
        ourShader.setInt("texture2", 1)

        // render loop
        // -----------
        while (!glfwWindowShouldClose(window)) {
            // per-frame time logic
            // --------------------
            val currentFrame = glfwGetTime().toFloat()
            deltaTime = currentFrame - lastFrame
            lastFrame = currentFrame

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

            // pass projection matrix to shader (note that in this case it could change every frame)
            val projection =
                glm.perspective(toRadians(camera.zoom), WINDOW_SIZE.first.toFloat() / WINDOW_SIZE.second.toFloat(), 0.1f, 100.0f)
            ourShader.setMat4("projection", projection)

            // camera / view transformation
            val view = camera.getViewMatrix()
            ourShader.setMat4("view", view)

            // render boxes
            glBindVertexArray(vao.get(0))
            for ((i, translation) in cubePositions.withIndex()) {
                val angle = toRadians(20f * i)
                val model = Mat4()
                    .translate(translation)
                    .rotate(angle, Vec3(1.0f, 0.3f, 0.5f))

                ourShader.setMat4("model", model)

                glDrawArrays(GL_TRIANGLES, 0, 36)
            }


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

        if (glfwGetKey(window, GLFW_KEY_E) == GLFW_PRESS)
            camera.processKeyboard(FORWARD, deltaTime)
        if (glfwGetKey(window, GLFW_KEY_F) == GLFW_PRESS)
            camera.processKeyboard(BACKWARD, deltaTime)
        if (glfwGetKey(window, GLFW_KEY_Q) == GLFW_PRESS)
            camera.processKeyboard(LEFT, deltaTime)
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS)
            camera.processKeyboard(RIGHT, deltaTime)
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

    // glfw: whenever the mouse moves, this callback is called
    // -------------------------------------------------------
    private val mouseCallback = object : GLFWCursorPosCallback() {
        override fun invoke(
            window: Long,
            xposIn: Double,
            yposIn: Double
        ) {
            val xpos = xposIn.toFloat()
            val ypos = yposIn.toFloat()

            if (firstMouse) {
                lastX = xpos
                lastY = ypos
                firstMouse = false
            }

            val xoffset = xpos -lastX
            val yoffset = lastY -ypos // reversed since y-coordinates go from bottom to top
            lastX = xpos
            lastY = ypos

            camera.processMouseMovement(xoffset, yoffset)
        }
    }

    // glfw: whenever the mouse scroll wheel scrolls, this callback is called
    // ----------------------------------------------------------------------
    private val scrollCallback = object : GLFWScrollCallback() {
        override fun invoke(
            window: Long,
            xoffset: Double,
            yoffset: Double
        ) {
            camera.processMouseScroll(yoffset.toFloat())
        }
    }
}

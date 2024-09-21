package learnopengl.lighting

import glm_.L
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
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
import learnopengl.Camera
import learnopengl.CameraMovement.*

fun main() {
    with(BasicLightingDiffuse()) {
        main()
    }
}

class BasicLightingDiffuse {

    // settings
    companion object {
        val WINDOW_SIZE = Pair(720, 880)
    }

    private var window: Long = 0

    // camera
    private val camera = Camera(Vec3(0.5f, 1f, 5.0f))
    private var lastX = WINDOW_SIZE.first / 2f
    private var lastY = WINDOW_SIZE.second / 2f
    private var firstMouse = true

    // timing
    private var deltaTime = 0.0f // Time between current frame and last frame
    private var lastFrame = 0.0f // Time of last frame/

    // lighting
    private val lightPos = Vec3(1.2f, 1.0f, 2.0f)

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
        val lightingShader = Shader(
            "/shaders/lighting/basic_lighting_diffuse/basic_lighting.vert",
            "/shaders/lighting/basic_lighting_diffuse/basic_lighting.frag",
        )
        val lightCubeShader = Shader(
            "/shaders/lighting/basic_lighting_diffuse/light_cube.vert",
            "/shaders/lighting/basic_lighting_diffuse/light_cube.frag",
        )

        // set up vertex data (and buffer(s)) and configure vertex attributes
        // ------------------------------------------------------------------
        val vertices = floatArrayOf(
            -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
             0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
             0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
             0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
            -0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
            -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,

            -0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
             0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
             0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
             0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
            -0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
            -0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,

            -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,
            -0.5f,  0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
            -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
            -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
            -0.5f, -0.5f,  0.5f, -1.0f,  0.0f,  0.0f,
            -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,

             0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,
             0.5f,  0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
             0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
             0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
             0.5f, -0.5f,  0.5f,  1.0f,  0.0f,  0.0f,
             0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,

            -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,
             0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,
             0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
             0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
            -0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,

            -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,
             0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,
             0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
             0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
            -0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
            -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f
        )

        val cubeVAO = createIntBuffer(1)
        val vbo = createIntBuffer(1)

        glGenVertexArrays(cubeVAO)
        glGenBuffers(vbo)

        glBindVertexArray(cubeVAO.get(0))

        glBindBuffer(GL_ARRAY_BUFFER, vbo.get(0))
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)

        // position attribute
        glVertexAttribPointer(0, Vec3.length, GL_FLOAT, false, Vec3.size * 2, 0)
        glEnableVertexAttribArray(0)

        // normal attribute
        glVertexAttribPointer(1, Vec3.length, GL_FLOAT, false, Vec3.size * 2, Vec3.size.L)
        glEnableVertexAttribArray(1)

        // second, configure the light's VAO (VBO stays the same; the vertices are the same for the light object which is also a 3D cube)
        val lightVAO = createIntBuffer(1)

        glGenVertexArrays(lightVAO)
        glBindVertexArray(lightVAO.get(0))
        // we only need to bind to the VBO, the container's VBO's data already contains the data.
        glBindBuffer(GL_ARRAY_BUFFER, vbo.get(0))
        // set the vertex attribute
        glVertexAttribPointer(0, Vec3.length, GL_FLOAT, false, Vec3.size * 2, 0)
        glEnableVertexAttribArray(0)

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

            // don't forget to use the corresponding shader program first (to set the uniform)
            lightingShader.use()
            lightingShader.setVec3("objectColor", Vec3(1.0f, 0.5f, 0.31f))
            lightingShader.setVec3("lightColor",  Vec3(1.0f, 1.0f, 1.0f))
            lightingShader.setVec3("lightPos",  lightPos)

            // view/projection transformations
            val projection =
                glm.perspective(toRadians(camera.zoom), WINDOW_SIZE.first.toFloat() / WINDOW_SIZE.second.toFloat(), 0.1f, 100.0f)
            val view = camera.getViewMatrix()
            lightingShader.setMat4("projection", projection)
            lightingShader.setMat4("view", view)

            // world transformation
            var model = Mat4()
            lightingShader.setMat4("model", model)

            // render the cube
            glBindVertexArray(cubeVAO.get(0))
            glDrawArrays(GL_TRIANGLES, 0, 36)

            // also draw the lamp object
            lightCubeShader.use()
            lightCubeShader.setMat4("projection", projection)
            lightCubeShader.setMat4("view", view)
            model = Mat4()
                .translate(lightPos)
                .scale(Vec3(0.2f)) // a smaller cube
            lightCubeShader.setMat4("model", model)

            // draw the light cube object
            glBindVertexArray(lightVAO.get(0))
            glDrawArrays(GL_TRIANGLES, 0, 36)

            // glfw: swap buffers and poll IO events (keys pressed/released, mouse moved etc.)
            // -------------------------------------------------------------------------------
            glfwSwapBuffers(window)
            glfwPollEvents()
        }

        // optional: de-allocate all resources once they've outlived their purpose:
        // ------------------------------------------------------------------------
        glDeleteVertexArrays(cubeVAO)
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

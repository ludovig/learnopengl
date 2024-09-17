package learnopengl

import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import glm_.func.toRadians
import glm_.glm
import kotlin.math.cos
import kotlin.math.sin
import learnopengl.CameraMovement.*

// Default camera values
const val YAW         = -90.0f
const val PITCH       =  0.0f
const val SPEED       =  2.5f
const val SENSITIVITY =  0.1f
const val ZOOM        =  45.0f

// Defines several possible options for camera movement. Used as abstraction to stay away from window-system specific input methods
enum class CameraMovement {
    FORWARD,
    BACKWARD,
    LEFT,
    RIGHT
}

// An abstract camera class that processes input and calculates the corresponding Euler Angles, Vectors and Matrices for use in OpenGL
class Camera(
    // camera Attributes
    val position: Vec3 = Vec3(),
    var up: Vec3 = Vec3(0f, 1f, 0f),
    private var yaw: Float = YAW,
    private var pitch: Float = PITCH,
    private var front: Vec3 = Vec3(0f, 0f, -1f),
    private val movementSpeed: Float = SPEED,
    private val mouseSensitivity: Float = SENSITIVITY,
    var zoom: Float = ZOOM
) {
    var right = Vec3()
    private val worldUp = up

    init {
        updateCameraVectors()
    }

    // constructor with scalar values
    constructor(
        posX: Float,
        posY: Float ,
        posZ: Float ,
        upX: Float,
        upY: Float,
        upZ: Float,
        yaw: Float,
        pitch: Float
    ) : this(Vec3(posX, posY, posZ), Vec3(upX, upY, upZ), yaw, pitch)

    // returns the view matrix calculated using Euler Angles and the LookAt Matrix
    fun getViewMatrix(): Mat4 = glm.lookAt(
        position,
        position + front,
        up
    )

    // processes input received from any keyboard-like input system. Accepts input parameter in the form of camera defined ENUM (to abstract it from windowing systems)
    fun processKeyboard(direction: CameraMovement, deltaTime: Float) {
        val velocity = movementSpeed * deltaTime
        position += when(direction) {
            FORWARD -> front * velocity
            BACKWARD -> -front * velocity
            LEFT -> -right * velocity
            RIGHT -> right * velocity
        }
    }

    // processes input received from a mouse input system. Expects the offset value in both the x and y direction.
    fun processMouseMovement(
        xoffset: Float,
        yoffset: Float,
        constrainPitch: Boolean = true
    ) {
        val x = xoffset * mouseSensitivity
        val y = yoffset * mouseSensitivity

        yaw   += x
        pitch += y

        // make sure that when pitch is out of bounds, screen doesn't get flipped
        if (constrainPitch)
            glm.clamp(pitch, -89f, 89f)

        // update Front, Right and Up Vectors using the updated Euler angles
        updateCameraVectors()
    }

    // processes input received from a mouse scroll-wheel event. Only requires input on the vertical wheel-axis
    fun processMouseScroll(yoffset: Float) {
        zoom -= yoffset
        glm.clamp(zoom, 1f, 45f)
    }

    // calculates the front vector from the Camera's (updated) Euler Angles
    private fun updateCameraVectors() {
        // calculate the new Front vector
        front = Vec3()
        front.x = cos(toRadians(yaw)) * cos(toRadians(pitch))
        front.y = sin(toRadians(pitch))
        front.z = sin(toRadians(yaw)) * cos(toRadians(pitch))
        front = glm.normalize(front)
        // also re-calculate the Right and Up vector
        right = glm.normalize(glm.cross(front, worldUp))  // normalize the vectors, because their length gets closer to 0 the more you look up or down which results in slower movement.
        up = glm.normalize(glm.cross(right, front))
    }
}

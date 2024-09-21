#version 330 core
out vec4 FragColor;
in vec3 FragPos;
in vec3 Normal;

uniform vec3 objectColor;
uniform vec3 lightColor;
uniform vec3 lightPos;

void main()
{
    float ambiantStrength = 0.1;
    vec3 ambiant = ambiantStrength * lightColor;

    vec3 norm = normalize(Normal);
    vec3 lightDir = normalize(lightPos - FragPos);  
    float diffuse = max(dot(norm, lightDir), 0.0);

    vec3 result = (ambiant + diffuse) * objectColor;
    FragColor = vec4(result, 1.0);
}
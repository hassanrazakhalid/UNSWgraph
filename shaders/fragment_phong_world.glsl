
out vec4 outputColor;

uniform vec4 input_color;

uniform mat4 view_matrix;

// Light properties
//uniform vec3 lightPos;
uniform vec3 sunVec;
uniform vec3 lightIntensity;
uniform vec3 ambientIntensity;
uniform float diffuseCoeff;

// Material properties
uniform float ambientCoeff;

uniform vec3 specularCoeff;
uniform float phongExp;


in vec4 viewPosition;
in vec3 m;
in vec2 texCoordFrag;
in vec4 globalPosition;

uniform sampler2D tex;

void main()
{
//    vec3 m_unit = normalize(m);
//    // Compute the s, v and r vectors
//    vec3 lightPos = viewPosition.xyz + sunVec; //adding sunVec to get the position of light source
//    vec3 s = normalize(view_matrix*vec4(lightPos,1) - viewPosition).xyz;
//    vec3 v = normalize(-viewPosition.xyz);
//    vec3 r = normalize(reflect(-s,m_unit));
//
//    vec3 ambient = ambientIntensity*ambientCoeff
	
//    vec3 diffuse = max(lightIntensity*diffuseCoeff*dot(m_unit,s), 0.0);
//    vec3 specular;
//
//    // Only show specular reflections for the front face
//    //if (dot(m_unit,s) > 0)
//        specular = max(lightIntensity*specularCoeff*pow(dot(r,v),phongExp), 0.0);
//    //else
//     //   specular = vec3(0);
//
//    vec3 intensity = ambient + diffuse + specular;
//
//    outputColor = vec4(intensity,1)*input_color*texture(tex, texCoordFrag);
    //outputColor = input_color;
     
	
	vec3 m_unit = normalize(m);
	vec3 lightDir_unit = normalize(sunVec - viewPosition.xyz);
	float diff = max(dot(m_unit, lightDir_unit), 0.0);
//	float ambientStrength = 0.4;
	vec3 lightColor = vec3(1,1,1);
	vec3 ambient = lightColor * diffuseCoeff;
//	vec4 result = input_color * ambient;
//	outputColor = result;
	
	vec3 diffuse = diff * lightColor;
	
	vec3 intensity = ambient + diffuse;
//	 * texture(tex, texCoordFrag)
	vec4 result = vec4(intensity, 1) * texture(tex, texCoordFrag);
	outputColor = result;
//	outputColor = input_color * texture(tex, texCoordFrag);
//	outputColor = vec4(result, 1);
	
	
}

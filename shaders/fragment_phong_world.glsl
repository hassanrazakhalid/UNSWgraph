
out vec4 outputColor;

uniform vec4 input_color;

uniform mat4 view_matrix;

// Light properties
//uniform vec3 lightPos;
uniform vec3 sunVec;
uniform float diffuseCoeff;

uniform int isDay;

//setting light
struct Light {
	vec3  position;
    vec3  direction;
    float cutOff;  
  
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
	
    float ambientStrength;
    float specularStrength;
    
    float constant;
    float linear;
    float quadratic;
}; 
uniform Light light;

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
	
	vec3 lightColor = vec3(1,1,1);
	vec3 ambient = lightColor * light.ambientStrength;
//	vec4 result = input_color * ambient;
//	outputColor = result;
	
	//calculating diffuse color
	float diff = max(dot(m_unit, lightDir_unit), 0.0);
	vec3 diffuse = diff * lightColor;
	
	//calculating specular light
	vec3 viewDir = normalize(light.position - viewPosition.xyz);
	vec3 reflectDir = reflect(-normalize(light.direction), m_unit); 
	float spec = pow(max(dot(viewDir, reflectDir), 0.0), 8);
	vec3 specular = light.specularStrength * spec * lightColor;  
		
	vec3 intensity = ambient + diffuse + specular;
	
	if(isDay == 1) {
		vec4 result = vec4(intensity, 1) * texture(tex, texCoordFrag);
		outputColor = result;
	}
	else {
		
		float distance    = length(light.position - viewPosition.xyz);
		float attenuation = 1.0 / (light.constant + light.linear * distance + 
		    		    light.quadratic * (distance * distance));
		
//		ambient *= attenuation;
//		specular *= attenuation;
//		diffuse *= diffuse;
		
		float theta = dot(lightDir_unit, normalize(light.direction));
	    
		if(theta < light.cutOff) 
		{       
//		   do lighting calculations
			intensity *= attenuation;
					
			vec4 result = vec4(intensity, 1) * texture(tex, texCoordFrag);
			outputColor = result;
		}
		else {
			vec4 result = vec4(ambient + diffuse, 1) * texture(tex, texCoordFrag);
//			outputColor = vec4(0,0,0,1); //result;
			outputColor = result;
		}
	}	
//	outputColor = input_color * texture(tex, texCoordFrag);
//	outputColor = vec4(result, 1);
	
	
}

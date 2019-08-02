
out vec4 outputColor;

uniform vec4 input_color;

uniform mat4 view_matrix;

// Light properties
//uniform vec3 lightPos;
uniform vec3 sunVec;

uniform int isDay;

struct Material {
    sampler2D diffuse;
    sampler2D specular;    
    float shininess;
}; 
uniform Material material;

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
uniform vec3 viewPos;
uniform float diffuseCoeff;
// Material properties
uniform float ambientCoeff;

uniform vec3 specularCoeff;
uniform float phongExp;


in vec4 viewPosition;
in vec3 m;
in vec2 texCoordFrag;
in vec3 FragPos;

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
	vec3 lightColor = vec3(1,1,1);
	vec3 m_unit = normalize(m);
	vec3 lightDir_unit = normalize(sunVec - viewPosition.xyz);
	
	//calculating ambient light
	vec3 ambient = lightColor * light.ambientStrength;
	//calculating diffuse light
	float diff = max(dot(m_unit, lightDir_unit), 0.0);
	vec3 diffuse = diff * lightColor * diffuseCoeff;
	
	
	if(isDay == 1) {
		//calculating specular light for spotlight
//		vec3 viewDir = normalize(light.position - viewPosition.xyz);
//		vec3 reflectDir = reflect(-normalize(light.direction), m_unit); 
//		float spec = pow(max(dot(viewDir, reflectDir), 0.0), 8);
//		vec3 specular = light.specularStrength * spec * lightColor;  
		vec3 intensity = ambient+ diffuse;
		vec4 result = vec4(intensity, 1) * texture(tex, texCoordFrag) * input_color; 
		outputColor = result;//texture(tex, texCoordFrag);
	}
	else {
				//light is camera position
		vec3 lightDir = normalize(viewPosition.xyz - light.position);
	    // check if lighting is inside the spotlight cone
	    float theta = dot(lightDir, normalize(light.direction)); 		
//		vec3 lightDir = normalize(light.position - viewPosition.xyz);
//		float theta = dot(lightDir, normalize(-light.direction));
		// ambient
	    vec2 TexCoords = texCoordFrag;
//        vec3 ambient = light.ambient * texture(material.diffuse, TexCoords).rgb;
	    
	    
		if(theta > light.cutOff) 
		{       
//		   do lighting calculations
//			outputColor = vec4(1,0,0,1);
	        
//	        // diffuse 
	        vec3 norm = normalize(m);
////	        float diff = max(dot(norm, lightDir), 0.0);
////	        vec3 diffuse = light.diffuse * diff * texture(material.diffuse, TexCoords).rgb;  
////	        
////	        // specular
	        vec3 viewDir = normalize(viewPosition.xyz - FragPos);
	        vec3 reflectDir = reflect(-lightDir, norm);  
	        float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32);
			vec3 specular = light.specularStrength * spec * lightColor;
//	        float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
//	        vec3 specular = light.specular * spec * texture(material.specular, TexCoords).rgb;  
////	        
////	        // attenuation
	        float distance = length(light.position - FragPos);
	        float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance));    
////
//	         ambient  *= attenuation; // remove attenuation from ambient, as otherwise at large distances the light would be darker inside than outside the spotlight due the ambient term in the else branche
//	        diffuse   *= attenuation;
//	        specular *= attenuation;   
	        vec3 intensity = ambient + diffuse + specular ;
////	        vec3 result = ambient + diffuse + specular;
////	        vec4 FragColor = vec4(result, 1.0);
	        vec4 result = vec4(intensity, 1) * texture(tex, texCoordFrag);
			outputColor = result;
		}
		else {
			vec3 intensity = ambient;
//			vec4 result = vec4(ambient, 1) * texture(tex, texCoordFrag);
			vec4 result = vec4(intensity, 1) * texture(tex, texCoordFrag);
//			outputColor = vec4(0,0,0,1); //result;
			outputColor = result;
		}
	}	
//	outputColor = input_color * texture(tex, texCoordFrag);
//	outputColor = vec4(result, 1);
	
	
}

uniform mat4 g_WorldViewProjectionMatrix;
uniform float g_Time;

attribute vec3 inPosition;
attribute vec2 inTexCoord;

varying vec2 worldPosition;

void main(){
    vec4 pos = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);
    gl_Position = pos;
    worldPosition = pos.xz;
}
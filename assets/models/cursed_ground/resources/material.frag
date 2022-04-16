uniform float g_Time;

uniform sampler2D m_ColorMap;

varying vec2 worldPosition;

void main() {
    float textureScaling = 0.1;
    float progress = g_Time * -0.5;
    float x = (worldPosition.x + progress) * textureScaling;
    float y = (worldPosition.y + progress) * textureScaling;
    gl_FragColor = texture2D(m_ColorMap, vec2(x, y));
}
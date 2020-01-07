#version 120
//顶点的坐标
attribute vec4 aPosition;
//纹理的坐标
attribute vec4 aTextureCoord;
//输出到 fragmentshader的变量
varying vec2 vTextureCoord;

void main() {
    //内置的变量
    gl_Position =  aPosition;

    vTextureCoord = aTextureCoord.xy;
}

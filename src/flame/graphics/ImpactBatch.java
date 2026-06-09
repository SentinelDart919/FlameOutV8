package flame.graphics;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.math.*;

public class ImpactBatch extends SpriteBatch{
    public float u, v, u2, v2;
    public float rx, ry;
    public float lastRotation;
    public boolean heavyShader = false;
    public boolean useColor = false;
    public boolean canChangeShader = true;
    boolean white = false;
    float[] svt = new float[1024 * SPRITE_SIZE];

    public static ImpactBatch batch;
    static Batch lastBatch;

    public static void init(){
        batch = new ImpactBatch();
    }
    public static void beginSwap(){
        lastBatch = Core.batch;
        Mat proj = Draw.proj(), trans = Draw.trans();
        //Draw.flush();
        Core.batch = batch;
        Draw.proj(proj);
        Draw.trans(trans);
    }
    public static void endSwap(){
        Draw.flush();
        Core.batch = lastBatch;
    }

    public void setWhite(boolean w){
        //if(white != w) flush();
        white = w;
    }

    public Texture getTexture(){
        return lastTexture;
    }

    @Override
    protected void draw(Texture texture, float[] spriteVertices, int offset, int count){
        //super.draw(texture, spriteVertices, offset, count);
        float color = Color.whiteFloatBits;
        float mixColor = white ? Color.whiteFloatBits : Color.blackFloatBits;

        int size = Math.min(count, svt.length);
        System.arraycopy(spriteVertices, offset, svt, 0, size);
        for(int i = 0; i < size; i += VERTEX_SIZE){
            svt[i + 2] = color;
            svt[i + 5] = mixColor;
        }

        super.draw(texture, svt, 0, size);

        u = v = 0f;
        u2 = v2 = 1f;
        lastRotation = 0f;
    }

    @Override
    protected void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float rotation){
        float[] vertexData = new float[SPRITE_SIZE];

        if(!Mathf.zero(rotation)){
            float worldOriginX = x + originX;
            float worldOriginY = y + originY;
            float fx = -originX;
            float fy = -originY;
            float fx2 = width - originX;
            float fy2 = height - originY;

            float cos = Mathf.cosDeg(rotation);
            float sin = Mathf.sinDeg(rotation);

            float x1 = cos * fx - sin * fy + worldOriginX;
            float y1 = sin * fx + cos * fy + worldOriginY;
            float x2 = cos * fx - sin * fy2 + worldOriginX;
            float y2 = sin * fx + cos * fy2 + worldOriginY;
            float x3 = cos * fx2 - sin * fy2 + worldOriginX;
            float y3 = sin * fx2 + cos * fy2 + worldOriginY;
            float x4 = x1 + (x3 - x2);
            float y4 = y3 - (y2 - y1);

            float u = region.u;
            float v = region.v2;
            float u2 = region.u2;
            float v2 = region.v;

            float color = useColor ? this.colorPacked : Color.whiteFloatBits;
            float mixColor = white ? Color.whiteFloatBits : Color.blackFloatBits;

            vertexData[0] = x1;
            vertexData[1] = y1;
            vertexData[2] = color;
            vertexData[3] = u;
            vertexData[4] = v;
            vertexData[5] = mixColor;

            vertexData[6] = x2;
            vertexData[7] = y2;
            vertexData[8] = color;
            vertexData[9] = u;
            vertexData[10] = v2;
            vertexData[11] = mixColor;

            vertexData[12] = x3;
            vertexData[13] = y3;
            vertexData[14] = color;
            vertexData[15] = u2;
            vertexData[16] = v2;
            vertexData[17] = mixColor;

            vertexData[18] = x4;
            vertexData[19] = y4;
            vertexData[20] = color;
            vertexData[21] = u2;
            vertexData[22] = v;
            vertexData[23] = mixColor;
        }else{
            float fx2 = x + width;
            float fy2 = y + height;
            float u = region.u;
            float v = region.v2;
            float u2 = region.u2;
            float v2 = region.v;

            float color = useColor ? this.colorPacked : Color.whiteFloatBits;
            float mixColor = white ? Color.whiteFloatBits : Color.blackFloatBits;

            vertexData[0] = x;
            vertexData[1] = y;
            vertexData[2] = color;
            vertexData[3] = u;
            vertexData[4] = v;
            vertexData[5] = mixColor;

            vertexData[6] = x;
            vertexData[7] = fy2;
            vertexData[8] = color;
            vertexData[9] = u;
            vertexData[10] = v2;
            vertexData[11] = mixColor;

            vertexData[12] = fx2;
            vertexData[13] = fy2;
            vertexData[14] = color;
            vertexData[15] = u2;
            vertexData[16] = v2;
            vertexData[17] = mixColor;

            vertexData[18] = fx2;
            vertexData[19] = y;
            vertexData[20] = color;
            vertexData[21] = u2;
            vertexData[22] = v;
            vertexData[23] = mixColor;
        }
        super.draw(region.texture, vertexData, 0, SPRITE_SIZE);
        u = region.u;
        v = region.v;
        u2 = region.u2;
        v2 = region.v2;
        lastRotation = rotation;
    }

    @Override
    protected void setShader(Shader shader, boolean apply){
        if(!canChangeShader) return;
        super.setShader(shader, apply);
    }
}

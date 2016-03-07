/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.illumine.srb.utils;

import com.jme3.asset.TextureKey;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.material.MatParam;
import com.jme3.material.MatParamTexture;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.IntMap;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author tsr
 */
public class J3MOutputCapsule implements OutputCapsule {

    private final HashMap<String, String> _parameters;
    protected final J3MExporter _exporter;

    public J3MOutputCapsule(J3MExporter exporter) {
        _exporter = exporter;
        _parameters = new HashMap<>();
    }

    public void writeToStream(OutputStreamWriter out) throws IOException {
        for (String key : _parameters.keySet()) {
            out.write("      ");
            writeParameter(out, key, _parameters.get(key));
            out.write("\n");
        }
    }

    protected void writeParameter(OutputStreamWriter out, String name, String value) throws IOException {
        out.write(name);
        out.write(" : ");
        out.write(value);
    }

    public void clear() {
        _parameters.clear();
    }

    protected void putParameter(String name, String value) {
        _parameters.put(name, value);
    }

    @Override
    public void write(boolean value, String name, boolean defVal) throws IOException {
        if (value == defVal) {
            return;
        }

        putParameter(name, ((value) ? "On" : "Off"));
    }

    @Override
    public void writeStringSavableMap(Map<String, ? extends Savable> map, String name, Map<String, ? extends Savable> defVal) throws IOException {
        map.forEach((key, value) -> {
            if (defVal == null || !defVal.containsKey(key) || !defVal.get(key).equals(value)) {
                putParameter(key, format(value));
            }
        });
    }

    protected String format(Savable value) {
        if (value instanceof MatParamTexture) {
            return formatMatParamTexture((MatParamTexture) value);
        }
        if (value instanceof MatParam) {
            return ((MatParam) value).getValueAsString();
        }

        throw new UnsupportedOperationException(value.getClass() + ": Not supported yet.");
    }

    protected static String formatMatParamTexture(MatParamTexture param) {
        StringBuilder l_ret = new StringBuilder();
        Texture l_tex = (Texture) param.getValue();
        TextureKey l_key;
        if (l_tex != null) {
            l_key = (TextureKey) l_tex.getKey();

            if (l_key != null && l_key.isFlipY()) {
                l_ret.append("Flip ");
            }

            l_ret.append(formatWrapMode(l_tex, Texture.WrapAxis.S));
            l_ret.append(formatWrapMode(l_tex, Texture.WrapAxis.T));
            l_ret.append(formatWrapMode(l_tex, Texture.WrapAxis.R));

            //Min and Mag filter
            Texture.MinFilter def = Texture.MinFilter.BilinearNoMipMaps;
            if (l_tex.getImage().hasMipmaps() || (l_key != null && l_key.isGenerateMips())) {
                def = Texture.MinFilter.Trilinear;
            }
            if (l_tex.getMinFilter() != def) {
                l_ret.append("Min").append(l_tex.getMinFilter().name()).append(" ");
            }

            if (l_tex.getMagFilter() != Texture.MagFilter.Bilinear) {
                l_ret.append("Mag").append(l_tex.getMagFilter().name()).append(" ");
            }

            l_ret.append("\"").append(l_key.getName()).append("\"");
        }

        return l_ret.toString();
    }

    protected static String formatWrapMode(Texture texVal, Texture.WrapAxis axis) {
        WrapMode mode;
        try {
            mode = texVal.getWrap(axis);
        } catch (IllegalArgumentException e) {
            //this axis doesn't exist on the texture
            return "";
        }
        if (mode != WrapMode.EdgeClamp) {
            return "Wrap" + mode.name() + "_" + axis.name() + " ";
        }
        return "";
    }

    @Override
    public void write(Enum value, String name, Enum defVal) throws IOException {
        if (value == defVal) {
            return;
        }

        putParameter(name, value.toString());
    }

    @Override
    public void write(float value, String name, float defVal) throws IOException {
        if (value == defVal) {
            return;
        }

        putParameter(name, Float.toString(value));
    }

    @Override
    public void write(float[] value, String name, float[] defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(float[][] value, String name, float[][] defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(double value, String name, double defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(double[] value, String name, double[] defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(double[][] value, String name, double[][] defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(long value, String name, long defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(long[] value, String name, long[] defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(long[][] value, String name, long[][] defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(short value, String name, short defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(short[] value, String name, short[] defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(short[][] value, String name, short[][] defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(boolean[] value, String name, boolean[] defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(boolean[][] value, String name, boolean[][] defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(String value, String name, String defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(String[] value, String name, String[] defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(String[][] value, String name, String[][] defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(BitSet value, String name, BitSet defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(Savable object, String name, Savable defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(Savable[] objects, String name, Savable[] defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(Savable[][] objects, String name, Savable[][] defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void writeSavableArrayList(ArrayList array, String name, ArrayList defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void writeSavableArrayListArray(ArrayList[] array, String name, ArrayList[] defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void writeSavableArrayListArray2D(ArrayList[][] array, String name, ArrayList[][] defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void writeFloatBufferArrayList(ArrayList<FloatBuffer> array, String name, ArrayList<FloatBuffer> defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void writeByteBufferArrayList(ArrayList<ByteBuffer> array, String name, ArrayList<ByteBuffer> defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void writeSavableMap(Map<? extends Savable, ? extends Savable> map, String name, Map<? extends Savable, ? extends Savable> defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void writeIntSavableMap(IntMap<? extends Savable> map, String name, IntMap<? extends Savable> defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(FloatBuffer value, String name, FloatBuffer defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(IntBuffer value, String name, IntBuffer defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(ByteBuffer value, String name, ByteBuffer defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(ShortBuffer value, String name, ShortBuffer defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(byte value, String name, byte defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(byte[] value, String name, byte[] defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(byte[][] value, String name, byte[][] defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(int value, String name, int defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(int[] value, String name, int[] defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(int[][] value, String name, int[][] defVal) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

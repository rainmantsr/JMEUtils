/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.illumine.srb.utils;

import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;

/**
 *
 * @author tsr
 */
public class J3MRenderStateOutputCapsule extends J3MOutputCapsule {    
    protected final static HashMap<String, String> _NAME_MAP;
    protected String _offsetUnit;
    
    static {
        _NAME_MAP = new HashMap<>();
        _NAME_MAP.put( "wireframe", "Wireframe");
        _NAME_MAP.put( "cullMode", "FaceCull");
        _NAME_MAP.put( "depthWrite", "DepthWrite");
        _NAME_MAP.put( "depthTest", "DepthTest");
        _NAME_MAP.put( "blendMode", "Blend");
        _NAME_MAP.put( "alphaFallOff", "AlphaTestFalloff");
        _NAME_MAP.put( "offsetFactor", "PolyOffset");
        _NAME_MAP.put( "colorWrite", "ColorWrite");
        _NAME_MAP.put( "pointSprite", "PointSprite");
        _NAME_MAP.put( "depthFunc", "DepthFunc");
        _NAME_MAP.put( "alphaFunc", "AlphaFunc");
    }
    public J3MRenderStateOutputCapsule(J3MExporter exporter) {
        super(exporter);
    }

    public OutputCapsule getCapsule(Savable object) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void clear() {
        super.clear();
        _offsetUnit = "";
    }

    @Override
    public void writeToStream(OutputStreamWriter out) throws IOException {
        out.write("    AdditionalRenderState {\n");
        super.writeToStream(out);
        out.write("    }\n");
    }  
    
    @Override
    protected void writeParameter(OutputStreamWriter out, String name, String value) throws IOException {
        out.write(name);
        out.write(" ");        
        out.write(value);
        
        if( "PolyOffset".equals(name) ) {
            out.write(" ");
            out.write(_offsetUnit);
        }        
    }
    
    @Override
    protected void putParameter(String name, String value ) {
        if( "offsetUnits".equals(name) ) {
            _offsetUnit = value;
            return;
        }
        
        if( !_NAME_MAP.containsKey(name) )
            return;
        
        super.putParameter(_NAME_MAP.get(name), value);
    }
}

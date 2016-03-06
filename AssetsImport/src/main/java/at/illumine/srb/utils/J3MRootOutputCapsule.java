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
public class J3MRootOutputCapsule extends J3MOutputCapsule {

    private final HashMap<Savable, J3MOutputCapsule> _outCapsules;
    private String _name;
    private String _materialDefinition;
    private Boolean _isTransparent;

    public J3MRootOutputCapsule(J3MExporter exporter) {
        super(exporter);
        _outCapsules = new HashMap<>();
    }

    @Override
    public void clear() {
        super.clear();
        _isTransparent = null;
        _name = "";
        _materialDefinition = "";
        _outCapsules.clear();   
        
    }

    public OutputCapsule getCapsule(Savable object) {
        if( !_outCapsules.containsKey(object) ) {
            _outCapsules.put(object, new J3MRenderStateOutputCapsule(_exporter) );
        }
        
        return _outCapsules.get(object);
    }

    @Override
    public void writeToStream(OutputStreamWriter out) throws IOException {
        out.write("Material " + _name + " : " + _materialDefinition + " {\n\n");
        if( _isTransparent != null )
            out.write("    Transparent " + ( (_isTransparent)?"On":"Off" ) + "\n\n" );
        
        out.write("    MaterialParameters {\n");
        super.writeToStream(out);
        out.write("    }\n\n");    
        
        for( J3MOutputCapsule c : _outCapsules.values() ) {
            c.writeToStream(out);
        }
        out.write("}\n");
    }

    @Override
    public void write(String value, String name, String defVal) throws IOException {
        switch (name) {
            case "material_def":
                _materialDefinition = value;
                break;
            case "name":
                _name = value;
                break;
            default:
                throw new UnsupportedOperationException(name + " string material parameter not supported yet");
        }
    }

    @Override
    public void write(boolean value, String name, boolean defVal) throws IOException {
        if( value == defVal)
            return;
        
        switch (name) {
            case "is_transparent":
                _isTransparent = value;
                break;
            default:
                throw new UnsupportedOperationException(name + " boolean material parameter not supported yet");
        }
    }

    @Override
    public void write(Savable object, String name, Savable defVal) throws IOException {
        object.write(_exporter);
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.illumine.srb.utils;

import com.jme3.export.JmeExporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 *
 * @author tsr
 */
public class J3MExporter implements JmeExporter {

    private final J3MRootOutputCapsule _rootCapsule;

    public J3MExporter() {
        _rootCapsule = new J3MRootOutputCapsule(this);
    }

    @Override
    public void save(Savable object, OutputStream f) throws IOException {
        OutputStreamWriter l_out = new OutputStreamWriter(f);

        _rootCapsule.clear();
        object.write(this);
        _rootCapsule.writeToStream(l_out);

        l_out.flush();
    }

    @Override
    public void save(Savable object, File f) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(f)) {
            save(object, fos);
        }
    }

    @Override
    public OutputCapsule getCapsule(Savable object) {
        if ((object instanceof Material) || (object instanceof MaterialDef)) {
            return _rootCapsule;
        }

        return _rootCapsule.getCapsule(object);
    }

}

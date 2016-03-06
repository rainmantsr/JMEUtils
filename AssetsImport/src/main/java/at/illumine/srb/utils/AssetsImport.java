package at.illumine.srb.utils;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.MaterialKey;
import com.jme3.asset.TextureKey;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.material.MatParam;
import com.jme3.material.MatParamTexture;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.plugins.blender.BlenderLoader;
import com.jme3.system.JmeContext;
import com.jme3.texture.Texture;
import com.jme3.util.TangentBinormalGenerator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class AssetsImport extends SimpleApplication {

    private final static Logger logger = Logger.getLogger(AssetsImport.class.getName());
    private final CommandLine _cmd;
    protected final Path _exportPath;
    protected final Path _importPath;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            AssetsImport app;
            CommandLine l_cmd;

            l_cmd = new DefaultParser().parse(createOptions(), args, true);

            app = new AssetsImport(l_cmd);
            app.start(JmeContext.Type.Headless);
            app.stop();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            logger.info(Arrays.toString(args));
            (new HelpFormatter()).printHelp("AssetsImport", createOptions());
        }
    }

    private static Options createOptions() {
        Options l_opts = new Options();
        Option l_opt;

        l_opt = new Option("asset_dir", true, "Target directeory for the assets");
        l_opt.setRequired(true);
        l_opts.addOption(l_opt);

        l_opt = new Option("import_dir", true, "Import directeory");
        l_opt.setRequired(true);
        l_opts.addOption(l_opt);

        l_opts.addOption("create_tangents", false, "Flag for creating tangents");

        return l_opts;
    }

    public AssetsImport(CommandLine cmd) {
        _cmd = cmd;

        _exportPath = (new File(_cmd.getOptionValue("asset_dir"))).toPath();
        _importPath = (new File(_cmd.getOptionValue("import_dir"))).toPath();
    }

    @Override
    public void simpleInitApp() {
        assetManager.registerLoader(BlenderLoader.class, "blend");
        try {
            importModels();
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    protected void importModels() throws Exception {
        logger.log(Level.INFO, "Looking for models in: {0}", _importPath.toString());        

        Files.walk(_importPath)
                .filter(f -> f.toString().endsWith(".blend"))
                .forEach((f) -> {
                    try {
                        importModel(_importPath.relativize(f));
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                });
    }

    protected void importModel(Path relativeModelPath) throws Exception {
        Spatial model;

        logger.log(Level.INFO, "Importing: {0}", relativeModelPath.toString());
        assetManager.registerLocator( _importPath.toString() + "/" + relativeModelPath.getParent().toString() + "/", FileLocator.class);
        model = assetManager.loadModel(relativeModelPath.getFileName().toString());
        convert( model );
        export(relativeModelPath, model);
    }
    
    protected void convert(Spatial model) {
        if( _cmd.hasOption("create_tangents") ) {
            logger.log(Level.INFO, "Generating tangents");
            TangentBinormalGenerator.generate(model);
        }
        
    }

    protected void export(Path relativeModelPath, Spatial model) throws Exception {
        exportComponents(relativeModelPath, model);
        exportModel(relativeModelPath, model);
    }

    protected File getExportFile(Path relativeModelPath, String type, String name) {
        File l_exportPath;
        File l_exportFile;

        l_exportPath = new File(_exportPath.toString() + "/" + type + "/" + relativeModelPath.toString()).getParentFile();
        l_exportFile = new File(l_exportPath.toString() + "/" + name);

        if (!l_exportPath.exists()) {
            logger.log(Level.INFO, "Creating export path: {0}", l_exportPath.toString());
            l_exportPath.mkdirs();
        }

        return l_exportFile;
    }

    protected void exportModel(Path relativeModelPath, Spatial model) throws IOException {
        String l_modelName;
        File l_modelFile;

        l_modelName = relativeModelPath.getFileName().toString().substring(0, relativeModelPath.getFileName().toString().indexOf(".")) + ".j3o";
        l_modelFile = getExportFile(relativeModelPath, "Models", l_modelName);

        try (FileOutputStream out = new FileOutputStream(l_modelFile)) {
            BinaryExporter l_exporter = new BinaryExporter();
            l_modelFile.createNewFile();
            l_exporter.save(model, out);
        }
    }

    protected void exportComponents(Path relativeModelPath, Spatial spatial) throws Exception {
        if (spatial instanceof Node) {
            ((Node) spatial).getChildren().forEach((c) -> {
                try {
                    exportComponents(relativeModelPath, c);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });

            return;
        }

        if (spatial instanceof Geometry) {
            exportGeometryComponents(relativeModelPath, (Geometry) spatial);
        }
    }

    protected void exportGeometryComponents(Path relativeModelPath, Geometry geometry) throws Exception {
        exportMaterial(relativeModelPath, geometry.getMaterial());
    }

    protected void exportMaterial(Path relativeModelPath, Material material) throws Exception {
        File l_file;
        
        exportTextures( relativeModelPath, material );
//com.jme3.material.plugins.J3MLoader loader;
        
        l_file = getExportFile(relativeModelPath, "Materials", material.getName() + ".j3m");
        try (FileOutputStream out = new FileOutputStream(l_file)) {
            J3MExporter j3m_exp = new J3MExporter();
            l_file.createNewFile();
            j3m_exp.save(material, out);

            logger.log(Level.INFO, "Set MaterialKey to: {0}", _exportPath.relativize(l_file.toPath()).toString());
            material.setKey(new MaterialKey(_exportPath.relativize(l_file.toPath()).toString()));
        }
    }
    
    protected void exportTextures(Path relativeModelPath, Material material) throws Exception {
        for( MatParam p : material.getParams() ) {
            if( p instanceof MatParamTexture ) {
                exportTexture(relativeModelPath, (Texture)p.getValue());                
            }
        }
    }
    
    protected void exportTexture( Path relativeModelPath, Texture texture) throws Exception {
        TextureKey l_key = (TextureKey)texture.getKey();
        TextureKey l_newKey;
        File l_outFile;
        File l_inFile;
        String l_name;        
        
        l_name = l_key.getName();
        if( true ) {
            l_name = l_name.substring(l_key.getFolder().length());
        }
        logger.log(Level.INFO, "Exporting texture: {0}", l_name);
                
        l_outFile = getExportFile(relativeModelPath, "Textures", l_name );
        l_inFile = new File( _importPath.toString() + "/" + relativeModelPath.getParent().toString() + l_key.getName());
        
        logger.log(Level.INFO, "Copying from {0} to {1}", new String[] {l_inFile.toString(), l_outFile.toString()} );
        
        Files.copy(l_inFile.toPath(), l_outFile.toPath(), StandardCopyOption.REPLACE_EXISTING );
        
        l_newKey = new TextureKey( _exportPath.relativize(l_outFile.toPath()).toString(), l_key.isFlipY() );
        l_newKey.setAnisotropy( l_key.getAnisotropy() );
        
        logger.log(Level.INFO, "Set TextureKey to: {0}", l_newKey.toString() );
        texture.setKey( l_newKey );
    }
}

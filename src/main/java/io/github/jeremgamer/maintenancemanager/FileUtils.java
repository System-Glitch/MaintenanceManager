package io.github.jeremgamer.maintenancemanager;


import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtils {
    public static void copy(InputStream from , OutputStream to) throws IOException {
        byte[] buf = new byte[1024];
        int len;
        while ( (len=from.read(buf)) > 0 ) {
            to.write(buf, 0, len);
        }
        to.flush();
        from.close();
        to.close();
    }

    public static void zip(File zipFile , File source , File...excludedFiles) {
        byte[] buffer = new byte[1024];
        FileOutputStream fos;
        ZipOutputStream zos = null;
        try {
            zipFile.createNewFile();
            fos = new FileOutputStream(zipFile);
            zos = new ZipOutputStream(fos);
            FileInputStream in = null;
            for (String file :listFiles(source , source  , new ArrayList<String>() , Arrays.asList(excludedFiles))){
                if(new File(file).equals(zipFile))continue;
                ZipEntry ze = new ZipEntry(file);
                zos.putNextEntry(ze);
                copy(in , zos);
            }
            zos.closeEntry();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if(zos != null)zos.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<String> listFiles(File source , File f , List<String> list , List<File> excludedFile) {
        for(File file : excludedFile){
            if(file.getAbsolutePath().equals(f.getAbsolutePath()))return list;
        }
        if (f.isFile()) list.add(f.getAbsolutePath().replaceFirst(source.getAbsolutePath() + "/" , ""));
        else
            for (File file : f.listFiles()) listFiles(source , file , list ,excludedFile);
        return list;
    }
}

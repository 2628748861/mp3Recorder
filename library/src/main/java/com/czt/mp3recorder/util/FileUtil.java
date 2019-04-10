package com.czt.mp3recorder.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtil {
    public static void ReName(File file,String newname) {//文件重命名
//        File newfile=new File(file.getParent()+File.separator+newname);
//        Files.copy(file.getPath(),newfile.getPath(),null);
    }
    public static void copyFile(File source, File dest)
    {
        try
        {
            InputStream input = null;
            OutputStream output = null;
            try {
                input = new FileInputStream(source);
                output = new FileOutputStream(dest);
                byte[] buf = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buf)) > 0) {
                    output.write(buf, 0, bytesRead);
                }
            } finally {
                input.close();
                output.close();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static File mkFile(File director,String fileName)
    {
        return new File(director,fileName);
    }

    public static void delete(File file)
    {
        file.delete();
    }
}

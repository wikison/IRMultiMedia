package inroids.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipFile {
    public static final String TAG = "IRLibrary";

    /**
     * DeCompress the ZIP to the path
     * 
     * @param zipFileString
     *            name of ZIP
     * @param outPathString
     *            path to be unZIP
     * @throws Exception
     */
    public static void UnZipFolder(String zipFileString, String outPathString) {
        try {
            // InputStream is = new ByteArrayInputStream(zipFileString.getBytes("utf-8"));
            // ZipInputStream inZip = new ZipInputStream(is);
            ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
            ZipEntry zipEntry;
            String szName = "";
            while ((zipEntry = inZip.getNextEntry()) != null) {
                szName = zipEntry.getName();
                if (zipEntry.isDirectory()) {
                    // get the folder name of the widget
                    szName = szName.substring(0, szName.length() - 1);
                    File folder = new File(outPathString + File.separator + szName);
                    folder.mkdirs();
                } else {

                    File file = new File(outPathString + File.separator + szName);
                    file.createNewFile();
                    // get the output stream of the file
                    FileOutputStream out = new FileOutputStream(file);
                    int len;
                    byte[] buffer = new byte[1024];
                    // read (len) bytes into buffer
                    while ((len = inZip.read(buffer)) != -1) {
                        // write (len) byte from buffer at the position 0
                        out.write(buffer, 0, len);
                        out.flush();
                    }
                    out.close();
                }
            }
            inZip.close();
        } catch (Exception e) {
            MyLog.e(TAG, "ZipFile.UnZipFolder:" + e.toString());
        }
    }

    /**
     * DeCompress the ZIP to the path
     * 
     * @param zipFileString
     *            name of ZIP
     * @param outPathString
     *            path to be unZIP
     * @throws Exception
     */
    public static void UnZipFile(String zipFileString, String outPathString, String fileName) {
        try {
            // ByteArrayInputStream bai = new ByteArrayInputStream(zipFileString.getBytes("utf-8"));
            // ZipInputStream inZip = new ZipInputStream(bai);
            ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
            ZipEntry zipEntry;
            String szName = "";
            while ((zipEntry = inZip.getNextEntry()) != null) {
                szName = zipEntry.getName();
                if (!zipEntry.isDirectory()) {
                    if (fileName.equals(szName)) {
                        File file = new File(outPathString + File.separator + szName);
                        file.createNewFile();
                        // get the output stream of the file
                        FileOutputStream out = new FileOutputStream(file);
                        int len;
                        byte[] buffer = new byte[1024];
                        // read (len) bytes into buffer
                        while ((len = inZip.read(buffer)) != -1) {
                            // write (len) byte from buffer at the position 0
                            out.write(buffer, 0, len);
                            out.flush();
                        }
                        out.close();

                        break;
                    }
                }
            }
            inZip.close();
        } catch (Exception e) {
            MyLog.e(TAG, "ZipFile.UnZipFile:" + e.toString());
        }
    }

}

package main.java.Study.Java;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class SearchString {

    private String searchTerm = null;           // Holds our search term
    private String path = null;                 // The path where it starts searching
    private File[] filesInsidePath = null;
    private ArrayList<File> filesToBeArchived = new ArrayList<File>();
    private static final Logger LOGGER = Logger.getLogger(SearchString.class.getName());

    public void setString(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public String getFilePath() {
        // Retrieve the name of the path
        return path;
    }

    public void setFilePath(String path) {
        // Set the path to the folder
        Path p = Paths.get(path);
        if (!new File(path).exists()) {
            LOGGER.info("Path does not exist. Will use current directory.");
            this.path = System.getProperty("user.dir");
        }
        else {
            this.path = path;
        }
    }

    public void createZipFromFiles() {

        LOGGER.info("Will now zip: " + filesToBeArchived);

        ZipOutputStream zipOutputStream = null;
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(path + "\\" + searchTerm + ".zip");
            zipOutputStream = new ZipOutputStream(out);
        }
        catch (IOException e) {
            e.printStackTrace();
            LOGGER.severe("Unable to create stream to write to: " + ".zip");
        }

        for (File file : this.filesToBeArchived) {
            LOGGER.info("Now zipping: " + file.getName());
            try {

                ZipEntry zipEntry = new ZipEntry(file.getName());
                zipOutputStream.putNextEntry(zipEntry);

                FileInputStream in = new FileInputStream(file);
                byte[] buf = new byte[1024];
                int bytesRead;

                while ((bytesRead = in.read(buf)) >= 0) {
                    zipOutputStream.write(buf, 0, bytesRead);
                }
                LOGGER.info("Zipped file: " + file.getName());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            zipOutputStream.closeEntry();
            zipOutputStream.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.severe("Unable to close the streams!");
        }

        // Creating the archive, then moving it to the subfolder
        File archive = new File(path + "\\" + searchTerm + ".zip");
        archive.renameTo(new File(path + "\\" + searchTerm + "\\" + archive.getName()));
    }

    public void processString(String searchTerm) {
        /* Processes the string. If the files in the current path contain the
           it, a new folder will be created with the search string and an
           archive containing it will be created and moved within that very
           same folder.
         */
        this.searchTerm = searchTerm;
        filesInsidePath = new File(path).listFiles();

        Scanner scanner = null;

        for(File file : filesInsidePath) {
            try {
                // Omit directories. Reading from them leads to access denial errors.
                if(file.isDirectory()) {
                    continue;
                }
                scanner = new Scanner(file);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
                LOGGER.info("Cannot read from file: " + file);
            }

            while (scanner.hasNextLine()) {
                // Process each line of the file
                String line = scanner.nextLine();
                // If the line contains the term, the file is elligible for archiving
                if (line.contains(searchTerm)) {
                    // Unless the folder alreadys exists, create it
                    if (!new File(path + "\\" + searchTerm).exists()) {
                        LOGGER.info("The folder will be created as: " + path + "\\" + searchTerm);
                        new File(path + "\\" + searchTerm).mkdir();
                    }

                    // The file is going to be archived, so add him to the list
                    filesToBeArchived.add(file);
                    break;
                }
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException{

        SearchString object = new SearchString();
        object.setFilePath("C:\\Users\\Cosmin\\Documents\\src\\Study\\Java");
        object.processString("平仮名");
        object.createZipFromFiles();
    }
}
package ets.transfersystem;

import java.io.File;

/**
 * Created by gab on 27/07/17.
 */

public class FileClass {
    public int id;
    public String path;
    public String title;

    public FileClass(File file, int id)
    {
        this.id = id;
        this.path = file.getAbsolutePath();
        this.title = file.getName();
    }
}

package com.example.fleanegan.reiserechner;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by fleanegan on 19.03.17.
 */

public class IOManager {

    MainActivity mainActivity;


    public IOManager(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    public void serialize(File serFileName) {

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(serFileName), "utf-8"))) {
            writer.write("");
        } catch (Exception ef) {
            ef.getCause();
        }

        Serializer serializer = new Serializer();
        serializer.addUser(this.mainActivity.userList, User.totalAmount);

        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(serFileName);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(serializer);
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Serializer deserialize(File fileName, boolean dry) {
        FileInputStream fileInputStream = null;
        ObjectInputStream objectInputStream = null;
        Serializer serializer = new Serializer();

        try {
            fileInputStream = new FileInputStream(fileName.getAbsolutePath());
            objectInputStream = new ObjectInputStream(fileInputStream);
            serializer = (Serializer) objectInputStream.readObject();
        } catch (Exception e) {
        }

        if (!dry) {
            this.mainActivity.userList = serializer.getUserArrayList();
            User.totalAmount = serializer.getSaveTheAmount();
            User.numberOfUsers = serializer.getNumberOfUsers();
            this.mainActivity.getSupportActionBar().setTitle(fileName.getName().substring(0, fileName.getName().toCharArray().length - 4));
            if (serializer.getItemList() != null) {
                User.itemList = serializer.getItemList();

                for (User u : this.mainActivity.userList) {
                    Log.d("DE-SERIALIZATION", "accessed");
                    String cc = u.getName();
                    this.mainActivity.userId++;
                    this.mainActivity.addUser(cc, true);
                }
            } else {
                User.itemList = new ArrayList<>();
                this.mainActivity.userId = 0;
            }
            this.mainActivity.saveTo = fileName;
        }
        return serializer;
    }

    public ArrayList<File> getFiles() {
        File path = new File(this.mainActivity.getApplicationContext().getFileStreamPath("").getPath());
        File[] items = path.listFiles();
        ArrayList<File> returnList = new ArrayList<>();

        for (File f : items) {
            if (f.isFile() && (f.getName().substring(f.getName().lastIndexOf(".") + 1, f.getName().length())).equals("ser")) {
                returnList.add(f);
            }
        }
        Collections.sort(returnList, new Comparator<File>() {
            public int compare(File f1, File f2) {
                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
            }
        });
        return returnList;
    }

    public File rename(String newName, File oldFile) {
        File newFile = oldFile;
        if (!newName.equals("")) {
            newFile = new File(this.mainActivity.getApplicationContext().getFileStreamPath(newName).getPath());
            this.loadProject(oldFile, false);
            if (!newFile.exists()) {
                oldFile.renameTo(newFile);
            }
            this.serialize(newFile);
            this.loadProject(newFile, false);
            if (this.mainActivity.saveTo.equals(oldFile)) this.loadProject(newFile, false);
            oldFile.delete();
        }
        Log.d("rename", newFile.getName());
        return newFile;
    }

    public void deleteProject(File file) {
        file.delete();
        ArrayList<File> fileList = this.getFiles();
        if (fileList.size() == 0) {
            this.mainActivity.saveTo = new File((this.mainActivity.getApplicationContext().getFileStreamPath(this.mainActivity.serFileName).getPath()));
            try {
                this.mainActivity.saveTo.createNewFile();
            } catch (Exception e) {
                System.out.println("mothefuckea");
            }
            this.loadProject(this.mainActivity.saveTo, true);
        } else {
            this.loadProject(fileList.get(0), true);
        }
    }

    public void loadProject(File projectToBeLoaded, boolean afterDeletion) {
        //1. save existing project
        if (!afterDeletion)
            this.serialize(mainActivity.saveTo);
        //2. clean mainactivity for loading
        this.mainActivity.userId = 0;
        this.mainActivity.userList = null;
        this.mainActivity.headerView = null;
        this.mainActivity.dummyView = null;
        User.itemList = null;
        User.totalAmount = null;
        User.numberOfUsers = 0;
        this.mainActivity.saferDeletionTextView = null;
        this.mainActivity.saveTo = projectToBeLoaded;
        if (this.mainActivity.testNavMenuAlternative != null) {
            this.mainActivity.testNavMenuAlternative.removeAllViews();
        }

        //3. load new File(possibly invoked by the create new project feature)
        this.deserialize(projectToBeLoaded, false);
        this.mainActivity.initialize();
    }
}

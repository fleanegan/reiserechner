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

/**
 * Created by fleanegan on 19.03.17.
 */

public class IOManager {

    MainActivity mainActivity;


    public IOManager(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    public void serialize(File serFileName) {
        Log.d("SERIALIZATION", "initialized");

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
        Log.d("SERIALIZATION", "terminated");
    }

    public Serializer deserialize(File fileName, boolean dry) {
        Log.d("DE-SERIALIZATION", "initialized");
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
            if (serializer.getItemList() != null) User.itemList = serializer.getItemList();

            for (User u : this.mainActivity.userList) {
                Log.d("DE-SERIALIZATION", "accessed");
                String cc = u.getName();
                this.mainActivity.userId++;
                this.mainActivity.addUser(cc, true);
            }
        }
        return serializer;
    }

    public ArrayList<File> getFiles() {
        File path = new File(this.mainActivity.getApplicationContext().getFileStreamPath("").getPath());
        File[] items = path.listFiles();
        ArrayList<File> returnList = new ArrayList<>();

        for (File f : items) {
            if (f.isFile() && (f.getName().substring(f.getName().lastIndexOf(".") + 1, f.getName().length())).equals("ser")) {
                Log.d("DYNPATH", f.getName());
                returnList.add(f);
            }
        }

        return returnList;
    }

    public void switchProjects(File projectToBeLoaded) {
        //1. save existing project
        this.serialize(mainActivity.saveTo);
        //2. clean mainactivity for loading
        this.mainActivity.remember = -1;
        this.mainActivity.userId = 0;
        this.mainActivity.userList = null;
        this.mainActivity.headerView = null;
        this.mainActivity.dummyView = null;
        this.mainActivity.saferDeletionTextView = null;
        this.mainActivity.saveTo = projectToBeLoaded;
        this.mainActivity.testNavMenuAlternative.removeAllViews();
        this.mainActivity.initialize();

        //3. load new File(possibly invoked by the create new project feature)
        this.deserialize(projectToBeLoaded, false);
    }
}

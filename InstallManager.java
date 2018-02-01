package com.demo.media.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * description:
 * Created by 007 on 2017/12/20.
 */

public class InstallManager {

    private static InstallManager installManager;
    private Lock mLock=new ReentrantLock();
    private PkgInstallThread mInstallThread;
    private InstallManager() {

    }

    public static InstallManager getInstance() {
        if (installManager == null) {
            synchronized (InstallManager.class) {
                if (installManager == null) {
                    installManager = new InstallManager();
                }
            }
        }
        return installManager;
    }

    public void installApps(String appPath) {
        final String command = "pm install -r " + appPath;
        mInstallThread=new PkgInstallThread(command);
        new Thread(mInstallThread).start();
    }

    public void unInstallApps(String packageName) {
        String command = "pm uninstall " + packageName;
//        mInstallThread=new PkgInstallThread(command);
//        new Thread(mInstallThread).start();
        runShellCommand2(command);
    }

    private void runShellCommand2(String command) {
        Process process = null;
        BufferedReader bufferedReader = null;
        StringBuilder mShellCommandSB = new StringBuilder();
        Log.d("shitian_InstallManager", "runShellCommand :" + command);
        mShellCommandSB.delete(0, mShellCommandSB.length());
        String[] cmd = new String[]{"/system/bin/sh", "-c", command}; //调用bin文件
        try {
            byte b[] = new byte[1024];
            process = Runtime.getRuntime().exec(command);
            bufferedReader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                mShellCommandSB.append(line);
            }
            Log.d("shitian_InstallManager", "runShellCommand result : " + mShellCommandSB.toString());
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    // TODO: handle exception
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
    }

    private void runShellCommand(String command) {
        Process process = null;
        BufferedReader bufferedReader = null;
        StringBuilder mShellCommandSB = new StringBuilder();
        Log.d("shitian_InstallManager", "runShellCommand :" + command);
        mShellCommandSB.delete(0, mShellCommandSB.length());
        String[] cmd = new String[]{"/system/bin/sh", "-c", command}; //调用bin文件
        try {
            byte b[] = new byte[1024];
            process = Runtime.getRuntime().exec(cmd);
            bufferedReader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                mShellCommandSB.append(line);
            }
            Log.d("shitian_InstallManager", "runShellCommand result : " + mShellCommandSB.toString());
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    // TODO: handle exception
                }
            }
            if (process != null) {
                process.destroy();
            }
            if (mLock!=null){
                mLock.unlock();
            }
        }
    }

    class PkgInstallThread implements Runnable{

        private String command;

        PkgInstallThread(String command){
            this.command=command;
        }

        @Override
        public void run() {
            if (mLock!=null){
                mLock.lock();
            }
            runShellCommand(command);
        }
    }
}

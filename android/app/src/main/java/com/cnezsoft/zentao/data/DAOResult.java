package com.cnezsoft.zentao.data;

import android.content.ContentResolver;
import android.content.Context;

import com.cnezsoft.zentao.OperateResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * DAO result
 * Created by Catouse on 2015/1/28.
 */
public class DAOResult extends OperateResult<Boolean> {

    private HashMap<EntryType, DAOOperateInfo>  infos;

    public HashMap<EntryType, DAOOperateInfo> getInfos() {
        return infos;
    }

    public Set<EntryType> types() {
        return infos.keySet();
    }

    public void setResult(boolean result) {
        super.setResult(result);
    }

    public void setResult(boolean result, String message) {
        super.setResult(result);
        super.setMessage(message);
    }

    public int getCorrect(EntryType type) {
        DAOOperateInfo info = infos.get(type);
        if(info != null) {
            return info.correct();
        }
        return 0;
    }

    public int getAdd(EntryType type) {
        DAOOperateInfo info = infos.get(type);
        if(info != null) {
            return info.add();
        }
        return 0;
    }

    public int getUpdate(EntryType type) {
        DAOOperateInfo info = infos.get(type);
        if(info != null) {
            return info.update();
        }
        return 0;
    }

    public int getDelete(EntryType type) {
        DAOOperateInfo info = infos.get(type);
        if(info != null) {
            return info.delete();
        }
        return 0;
    }

    public void setDelete(EntryType type, int count) {
        DAOOperateInfo info = infos.get(type);
        if(info == null) {
            info = new DAOOperateInfo();
        }
        info.delete(count);
        infos.put(type, info);
    }

    public void setDelete(EntryType type) {
        setDelete(type, 1);
    }

    public void setUpdate(EntryType type, int count) {
        DAOOperateInfo info = infos.get(type);
        if(info == null) {
            info = new DAOOperateInfo();
        }
        info.update(count);
        infos.put(type, info);
    }

    public void setUpdate(EntryType type) {
        setUpdate(type, 1);
    }

    public void setAdd(EntryType type, int count) {
        DAOOperateInfo info = infos.get(type);
        if(info == null) {
            info = new DAOOperateInfo();
        }
        info.add(count);
        infos.put(type, info);
    }

    public void setAdd(EntryType type) {
        setAdd(type, 1);
    }

    public void setCorrect(EntryType type, int count) {
        DAOOperateInfo info = infos.get(type);
        if(info == null) {
            info = new DAOOperateInfo();
        }
        info.correct(count);
        infos.put(type, info);
    }

    public int sum(EntryType type) {
        DAOOperateInfo info = infos.get(type);
        if(info != null) {
            return info.sum();
        }
        return 0;
    }

    public int sum() {
        int s = 0;
        for(DAOOperateInfo info: infos.values()) {
            s += info.sum();
        }
        return s;
    }

    public boolean notEmpty(EntryType type) {
        DAOOperateInfo info = infos.get(type);
        if(info != null) {
            return info.notEmpty();
        }
        return false;
    }

    public boolean notEmpty() {
        return sum() > 0;
    }

    /**
     * Constructor with result
     *
     * @param result
     */
    public DAOResult(boolean result) {
        super(result);
        infos = new HashMap<>(4);
    }

    public DAOResult(boolean result, String message) {
        super(result, message);
        infos = new HashMap<>(4);
    }

    public DAOResult() {
        super(false);
        infos = new HashMap<>(4);
    }

    @Override
    public String toString() {
        String str = "";
        int sum = 0;
        for(Map.Entry<EntryType, DAOOperateInfo> info: infos.entrySet()) {
            str += "\n" + info.getKey().name() + "=" + info.getValue().toString();
            sum += info.getValue().sum();
        }
        str = "DAOResult: sum=" + sum + str;
        return str;
    }

    public void notifyChange(ContentResolver contentResolver) {
        for(EntryType type: infos.keySet()) {
            if(type != EntryType.Default) {
                contentResolver.notifyChange(DAO.getUri(type), null);
            }
        }
        contentResolver.notifyChange(DAO.getUri(EntryType.Default), null);
    }

    public void notifyChange(Context context) {
        notifyChange(context.getContentResolver());
    }
}

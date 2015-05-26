package xiao.teammanagement;

import android.util.SparseArray;


/**
 * Created by Xiao on 2015/5/12.
 */
public class PlayerSlot {
    private static int[] slots = {0, 0, 0, 0};
    private static SparseArray<String> players = new SparseArray<String>();

    public static int getNextSlot(){
        int i = 0;
        while ( i < slots.length && slots[i] != 0) i++;

        return i;
     }

    public static void setSlot(int slot){
        slots[slot] = 1;
    }

    public static void clearSlot(int slot){
        slots[slot] = 0;
    }

    public static void clearAllSlot(){
        for (int i = 0; i < slots.length; i++) slots[i] = 0;
    }

    public static void savePlayer(int key, String name){
       players.put(key, name);
    }

    public static SparseArray<String> getSavedPlayer(){
        return players;
    }

    public static void removePlayer(int key){
        players.delete(key);
    }

    public static void clearPlayer() { players.clear(); };
}

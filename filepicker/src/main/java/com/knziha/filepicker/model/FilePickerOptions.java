package com.knziha.filepicker.model;


public class FilePickerOptions {

    public long FirstFlag;

    public long getFlag() {
        return FirstFlag;
    }

    private void updateFFAt(int o, boolean val) {
        FirstFlag &= (~o);
        if(val) FirstFlag |= o;
        //defaultReader.edit().putInt("MFF",FirstFlag).commit();
    }

    public boolean getBkmkShown(){
        return (FirstFlag & 0x1)==0;
    }
    public boolean setBkmkShown(boolean val){
        updateFFAt(0x1,!val);
        return val;
    }

    public boolean getCreatingFile(){
        return (FirstFlag & 0x2)==0;
    }
    public boolean setCreatingFile(boolean val){
        updateFFAt(0x2,val);
        return val;
    }

    public boolean getBottombarShown(){
        return (FirstFlag & 0x4)!=0;
    }
    public boolean setBottombarShown(boolean val){
        updateFFAt(0x4,val);
        return val;
    }

    public boolean getPinSortDialog(){
        return (FirstFlag & 0x8)!=0;
    }
    public boolean setPinSortDialog(boolean val){
        updateFFAt(0x8,val);
        return val;
    }

    public boolean getEnableTumbnails(){
        return (FirstFlag & 0x10)!=0;
    }
    public boolean getEnableTumbnails(long FirstFlag){
        return (FirstFlag & 0x10)!=0;
    }
    public boolean setEnableTumbnails(boolean val){
        updateFFAt(0x10,val);
        return val;
    }

    public boolean getCropTumbnails(){
        return (FirstFlag & 0x20)!=0;
    }
    public boolean getCropTumbnails(long FirstFlag){
        return (FirstFlag & 0x20)!=0;
    }
    public boolean setCropTumbnails(boolean val){
        updateFFAt(0x20,val);
        return val;
    }

    public boolean getEnableList(){
        return true;//(FirstFlag & 0x40)==0;
    }
    public boolean getEnableList(long FirstFlag){
        return (FirstFlag & 0x40)==0;
    }
    public boolean setEnableList(boolean val){
        updateFFAt(0x40,!val);
        return val;
    }

    public int getListIconSize(){
        return (int) ((FirstFlag >> 7) & 15);
    }
    public int getListIconSize(long FirstFlag){
        return (int) ((FirstFlag >> 7) & 15);
    }
    public int setListIconSize(int val){
        FirstFlag &= (~0x80);
        FirstFlag &= (~0x100);
        FirstFlag &= (~0x200);
        FirstFlag &= (~0x400);
        FirstFlag |= ((long)(val & 15)) << 7;
        return val;
    }

    /** Default to 7 : 按修改时间逆序 */
    public int getSortMode(){
        return ((int) ((FirstFlag >> 11) & 7)+7)%8;
    }
    public int setSortMode(int val){
        FirstFlag &= (~0x800);
        FirstFlag &= (~0x1000);
        FirstFlag &= (~0x2000);
        FirstFlag |= ((long)((val+1)%8 & 7)) << 11;
        return val;
    }

    public int getGridSize(){
        return (int) ((FirstFlag >> 14) & 7);
    }
    public int getGridSize(long FirstFlag){
        return (int) ((FirstFlag >> 14) & 7);
    }
    public int setGridSize(int val){//1~64
        FirstFlag &= (~0x4000);
        FirstFlag &= (~0x8000);
        FirstFlag &= (~0x10000);
        FirstFlag &= (~0x20000);
        FirstFlag &= (~0x40000);
        FirstFlag &= (~0x80000);
        FirstFlag |= ((long)(val & 7)) << 14;
        return val;
    }

    public boolean getAutoThumbsHeight(){
        return false;//(FirstFlag & 0x100000)!=0;
    }
    public boolean getAutoThumbsHeight(long FirstFlag){
        return false;//(FirstFlag & 0x100000)!=0;
    }
    public boolean setAutoThumbsHeight(boolean val){
        updateFFAt(0x100000,val);
        return val;
    }

    public boolean getSlideShowMode(){
        return (FirstFlag & 0x200000)!=0;
    }
    public boolean setSlideShowMode(boolean val){
        updateFFAt(0x200000,val);
        return val;
    }
}

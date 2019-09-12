import zipfile, os, re
import time, datetime
from pathlib import Path

RejectFilter = ["build.gradle".lower()
,"AlertController.java".lower()
,"AlertDialog.java".lower()
,"ActionMenuItemView.java".lower()
,"BaseMenuPresenter.java".lower()
,"CascadingMenuPopup.java".lower()
,"ExpandedMenuView.java".lower()
,"MenuBuilder.java".lower()
,"MenuItemImpl.java".lower()
,"MenuPopup.java".lower()
,"StandardMenuPopup.java".lower()
,"ActionMenuPresenter.java".lower()
,"ActionMenuView.java".lower()
,"DialogTitle.java".lower()
,"DropDownListView.java".lower()
,"ListPopupWindow.java".lower()
,"Toolbar.java".lower()
,"select_dialog_singlechoice_material_holo.xml".lower()
,"ids.xml".lower()
]
new_repo_dir=r"D:\Code\ODPlayer\new_repo"
old_repo_dir=r"D:\Code\ODPlayer\AxtAppCompat"
for root, dirs, files in os.walk(new_repo_dir):
    for name in files:
        #print(os.path.join(root, name))
        filename = os.path.join(root, name)
        targetfile = old_repo_dir+"\\"+filename[len(new_repo_dir):]
        #print(targetfile,os.path.isfile(targetfile))
        if name.lower() in RejectFilter:
            print("rejected : ", filename)
            RejectFilter.remove(name.lower())
        else:
            ParentFolder = os.path.split(targetfile)[0]
            
            COMMAND = "copy /y "+'"'+filename+'"'+" "+'"'+targetfile+'"'
            if not os.path.isdir(ParentFolder):
                os.makedirs(ParentFolder)
            print(0, os.path.isdir(ParentFolder))
            os.system(COMMAND)
    #for name in dirs:



print()
for left in RejectFilter:
    print("left : ", left)
        
        
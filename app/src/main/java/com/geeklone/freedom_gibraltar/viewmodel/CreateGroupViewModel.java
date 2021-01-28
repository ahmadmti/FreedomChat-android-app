package com.geeklone.freedom_gibraltar.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CreateGroupViewModel extends ViewModel {

    public MutableLiveData<String> name = new MutableLiveData<>();
    public MutableLiveData<Boolean> isEmpty = new MutableLiveData<>();
    public MutableLiveData<String> errorField = new MutableLiveData<>();

    private MutableLiveData<String> liveData;

    public MutableLiveData<String> getName() {
        if (liveData == null) {
            liveData = new MutableLiveData<>();
        }
        return liveData;
    }

    public void onCreateGroupClicked() {
        if (name.getValue() == null || name.getValue().isEmpty()) {
            errorField.setValue("This field is required");
            isEmpty.setValue(true);
            return;
        } else isEmpty.setValue(false);


        liveData.setValue(name.getValue().trim());
    }

}


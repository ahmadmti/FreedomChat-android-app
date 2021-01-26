package com.geeklone.freedom_gibraltar.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.geeklone.freedom_gibraltar.model.User;

public class SignUpViewModel extends ViewModel {

    public MutableLiveData<String> name = new MutableLiveData<>();
    public MutableLiveData<String> mobile = new MutableLiveData<>();
    public MutableLiveData<String> email = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();
    public MutableLiveData<String> errorField = new MutableLiveData<>();
    public MutableLiveData<String> errorEmail = new MutableLiveData<>();
    public MutableLiveData<String> errorPassword = new MutableLiveData<>();
    public MutableLiveData<Boolean> isEmailValid = new MutableLiveData<>();
    public MutableLiveData<Boolean> isPasswordValid = new MutableLiveData<>();
    public MutableLiveData<Boolean> isEmpty = new MutableLiveData<>();


    private MutableLiveData<User> liveData;

    public MutableLiveData<User> getUser() {
        if (liveData == null) {
            liveData = new MutableLiveData<>();
        }
        return liveData;
    }

    public void onSignUpClicked() {
        User user = new User(name.getValue(), mobile.getValue(), email.getValue(), password.getValue());

        if (user.getName()==null || user.getMobile()==null || user.getName().isEmpty() || user.getMobile().isEmpty()) {
            errorField.setValue("This field is required");
            isEmpty.setValue(true);
            return;
        } else isEmpty.setValue(false);


        isEmailValid.setValue(!user.isEmailValid());
        if (!user.isEmailValid()) {
            errorEmail.setValue("Enter a valid email address");
            return;
        }

        isPasswordValid.setValue(!user.isPasswordValid());
        if (!user.isPasswordValid()) {
            errorPassword.setValue("Password Length should be greater than 6");
            return;
        }

        liveData.setValue(user);
    }
}


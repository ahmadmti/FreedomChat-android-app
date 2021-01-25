package com.geeklone.freedom_gibraltar.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.geeklone.freedom_gibraltar.model.User;

public class LoginViewModel extends ViewModel {

//    String errorMsgEmail = "Incorrect email entered";
//    String errorField = "All fields are required";
//    String errorMsgPwd = "At least 6 characters required";
//
//    User user;
//
//    @Bindable
//    private String toastMessage = null;
//
//    public String getToastMessage() {
//        return toastMessage;
//    }
//
//    private void setToastMessage(String toastMessage) {
//        this.toastMessage = toastMessage;
//        notifyPropertyChanged(BR.toastMessage);
//    }
//
//
//    public void setUserEmail(String email) {
//        user.setEmail(email);
//        notifyPropertyChanged(BR.userEmail);
//    }
//
//    @Bindable
//    public String getUserEmail() {
//        return user.getEmail();
//    }
//
//    @Bindable
//    public String getUserPassword() {
//        return user.getPassword();
//    }
//
//    public void setUserPassword(String password) {
//        user.setPassword(password);
//        notifyPropertyChanged(BR.userPassword);
//    }
//
//    public LoginViewModel() {
//        user = new User("", "");
//    }
//
//
//    public boolean isInputDataValid() {
//        if (TextUtils.isEmpty(getUserEmail()) || TextUtils.isEmpty(getUserPassword())) {
//            setToastMessage(errorField);
//            return false;
//        }
//
//        if (!Patterns.EMAIL_ADDRESS.matcher(getUserEmail()).matches()) {
//            setToastMessage(errorMsgEmail);
//            return false;
//        }
//
//        if (getUserPassword().length() < 6) {
//            setToastMessage(errorMsgPwd);
//            return false;
//        }
//
//        return true;
//    }

    public MutableLiveData<String> userEmail = new MutableLiveData<>();
    public MutableLiveData<String> userPassword = new MutableLiveData<>();

    private MutableLiveData<User> liveData;

    public MutableLiveData<User> getUser() {
        if (liveData == null) {
            liveData = new MutableLiveData<>();
        }
        return liveData;
    }

    public void onLoginClicked() {
        User loginUser = new User(userEmail.getValue(), userPassword.getValue());
        liveData.setValue(loginUser);
    }
}


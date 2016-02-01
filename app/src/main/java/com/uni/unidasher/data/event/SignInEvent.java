package com.uni.unidasher.data.event;

import com.uni.unidasher.data.status.SignInState;

/**
 * Created by Administrator on 2015/6/19.
 */
public class SignInEvent {
    SignInState signInState;
    String errorDescription;

    public SignInEvent(SignInState signInState,String errorDescription) {
        this.signInState = signInState;
        this.errorDescription = errorDescription;
    }

    public SignInState getSignInState() {
        return signInState;
    }

    public String getErrorDescription() {
        return errorDescription;
    }
}

package com.uni.unidasher.data.status;

/**
 * Created by Administrator on 2015/6/19.
 */
public enum SignInState {
    NOT_LOGGED_IN,      /** user not logged in **/
    LOGGED_OUT,         /** user is logged out **/
    LOGGED,             /** user is logged in **/
    WRONG_CREDENTIALS,  /** wrong password or login **/
    LOGGED_OUT_BY_SERVER   /** when the auth token is no longer valid**/
}

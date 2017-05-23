package org.commonlibrary.clsdk.curricula

/**
 * Created by diugalde on 21/05/17.
 */
class Utils {

    static def generateRandomString(len) {
        def text = "";
        def possible = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';

        for(int i = 0; i < len; i++) {
            int index = (int) Math.floor(Math.random() * possible.length())
            text += possible[index];
        }
        return text;
    }

}

#include <jni.h>
#include <string>

//
// Created by FrTech on 21-Jan-23.
//


extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_tsz_live_football_tv_streaming_hd_ui_activities_SplashActivity_getStringArray5(JNIEnv *env,
                                                                                  jobject thiz) {
    jobjectArray strarr = env->NewObjectArray(10, env->FindClass("java/lang/String"), nullptr);
    std::string IzR = "iDN&pvh1vAaSx$BCv8#Vx$m$QHs$caph&&ir0n56";
    env->SetObjectArrayElement(strarr, 0, env->NewStringUTF(IzR.c_str()));
    std::string V6 = "UWeSQ1aMx850XXfyNYes6iriVllLDkE3KzPFuRdO";
    env->SetObjectArrayElement(strarr, 1, env->NewStringUTF(V6.c_str()));
    std::string sWe = "ln2uX9qCv^h#eH7LgLo5OKjcBQwM&!P7E4!vQ!&R";
    env->SetObjectArrayElement(strarr, 2, env->NewStringUTF(sWe.c_str()));
    std::string EyY = "@&MJZ^K&40KZ5$6Z$At5pqpezfvE73%hT^U@y#V3";
    env->SetObjectArrayElement(strarr, 3, env->NewStringUTF(EyY.c_str()));
    std::string h70 = "X0vXeJTpLx9864YfH6rhsqGzGO3M9mK01^4MK^uL";
    env->SetObjectArrayElement(strarr, 4, env->NewStringUTF(h70.c_str()));
    std::string bg = "964E#TBxAjCla@KGRBQ8#xOmnfMNwvgHw9vmRTKc";
    env->SetObjectArrayElement(strarr, 5, env->NewStringUTF(bg.c_str()));
    std::string QbB = "E3KzPFuRdOyWvG2Dr1vPGAZpzCmzddNUInDld6Vk";
    env->SetObjectArrayElement(strarr, 6, env->NewStringUTF(QbB.c_str()));
    std::string Mz9 = "kaKFc%Nj^w6lDWw7$Lxza2P4t%Go$i&W1xW@bLrk";
    env->SetObjectArrayElement(strarr, 7, env->NewStringUTF(Mz9.c_str()));
    std::string BfB = "8a4rGc!&@iC3TJGIB7xVb5O2V9gJR1T9LUWDCIZ1";
    env->SetObjectArrayElement(strarr, 8, env->NewStringUTF(BfB.c_str()));
    std::string b4P = "wV8o22ELtTe8NtTLCBfnvK9JxJjNKvDu#EBlDELO";
    env->SetObjectArrayElement(strarr, 9, env->NewStringUTF(b4P.c_str()));

    return strarr;
}
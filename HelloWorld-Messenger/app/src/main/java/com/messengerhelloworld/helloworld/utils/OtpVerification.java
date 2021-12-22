package com.messengerhelloworld.helloworld.utils;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import com.messengerhelloworld.helloworld.interfaces.AfterOtpIsEntered;

public class OtpVerification {
	private static final String TAG = "hwmLogOtpVerification";
	private final FirebaseAuth mAuth;
	private final Activity activity;
	private String verification_id;

	public OtpVerification(Activity activity) {
		this.mAuth = FirebaseAuth.getInstance();
		this.activity = activity;
		this.verification_id = null;
	}

	public void sendOtp(String mob) {
		PhoneAuthOptions options =
			PhoneAuthOptions.newBuilder(mAuth)
				.setPhoneNumber("+91" + mob)
				.setTimeout(60L, TimeUnit.SECONDS)
				.setActivity(activity)
				.setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

					@Override
					public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
						Log.i(TAG, "OTP Verification Completed");
					}

					@Override
					public void onVerificationFailed(@NonNull FirebaseException e) {
						Log.i(TAG, "OTP Verification Failed");
					}

					@Override
					public void onCodeSent(@NonNull String verificationId,
										   @NonNull PhoneAuthProvider.ForceResendingToken token) {
						verification_id = verificationId;
						Log.i(TAG, "OTP is sent to the user successfully");
					}
				})
				.build();
		PhoneAuthProvider.verifyPhoneNumber(options);
	}

	public void verifyOtp(String enteredOtp, AfterOtpIsEntered afterOtpIsEntered) {
		if(verification_id != null) {
			afterOtpIsEntered.execute();
			PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verification_id, enteredOtp);
			mAuth.signInWithCredential(credential)
				.addOnCompleteListener(activity, task -> {
					if(task.isSuccessful())
						afterOtpIsEntered.ifCorrectOtp();
					else
						afterOtpIsEntered.ifWrongOtp();
				});
		}
		else
			Toast.makeText(activity, "Please enter the valid OTP.", Toast.LENGTH_SHORT).show();
	}
}

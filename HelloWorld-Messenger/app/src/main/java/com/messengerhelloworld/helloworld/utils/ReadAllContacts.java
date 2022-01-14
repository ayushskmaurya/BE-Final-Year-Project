package com.messengerhelloworld.helloworld.utils;

import android.app.Activity;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;

public class ReadAllContacts {
	public static String getContacts(Activity activity) {
		ArrayList<String> phoneNos = new ArrayList<>();
		String mobNo;
		int len;

		Cursor cursor = activity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				null, null, null, null);
		while(cursor.moveToNext()) {
			mobNo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			mobNo = mobNo.replaceAll("\\D", "");
			len = mobNo.length();
			if(len>=10)
				phoneNos.add("'" + mobNo.substring(len-10, len) + "'");
		}

		if(phoneNos.size() == 0)
			phoneNos.add("''");
		return phoneNos.toString();
	}
}

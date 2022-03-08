# BE-Final-Year-Project
Android Chat Application for Final Year Project.

* HelloWorld Messenger helps you connect with your closed ones and business acquaintances.
* It is an efficient and easy-to-use Android Chat application.
* This application is created using Java for Android app and PHP, MySQL for the server.
* Firebase Authentication is used to verify phone number.
* User will be able to login simultaneously on multiple Android devices.
* Since, all the attachments & chats of the user are stored in server, user do not have to worry about taking backups.
* User can chat as well as can share large sized files with each other without the internet connectivity & data charges.
* User can separate the spammers from normal chats i.e., Chats which are stated as Spammers by the user will not be populated along with the normal chats.
* User can edit the sent message as long as the recipient has not seen it.
* Groups can also be viewed separately. So, the user is able to manage & see all the groups together.

#### Instructions
1. Host the PHP and MySQL based HelloWorld Server.

2. Connect the Android app to Firebase.

3. Add Firebase Authentication to the app and enable Phone Authentication.

4. Change `BASE_URL` as per your requirement in the `Base.java` file present in `com.messengerhelloworld.helloworld.utils` package.

5. For testing, open File Manager in your Android mobile, create `HelloWorld` folder if not created already & save a text file with name as `ipForTesting.txt`. Under this file write your hostname.

6. Change `BASE_URL` as per your requirement in the `base.php` file.

7. Create a MySQL Database using the following command:
	```
	CREATE DATABASE helloworld;
	```

8. Create all the tables in this database using following command:
	```
	-- Users Table
	CREATE TABLE users (
		userid INT AUTO_INCREMENT,
		name VARCHAR(100) NOT NULL,
		mobile_no VARCHAR(10) UNIQUE NOT NULL,
		password VARCHAR(255) NOT NULL,
		profile_image VARCHAR(100) UNIQUE,
		PRIMARY KEY(userid)
	);
	```

	```
	-- Chats Table
	CREATE TABLE chats (
		chatid INT AUTO_INCREMENT,
		user1id INT,
		user2id INT,
		spammer INT DEFAULT -1 NOT NULL,
		PRIMARY KEY(chatid),
		FOREIGN KEY (user1id) REFERENCES users(userid),
		FOREIGN KEY (user2id) REFERENCES users(userid)
	);
	```
	
	```
	-- Groups Table
	CREATE TABLE groups (
		groupid INT AUTO_INCREMENT,
		chatid INT NOT NULL,
		name VARCHAR(100) NOT NULL,
		PRIMARY KEY(groupid),
		FOREIGN KEY (chatid) REFERENCES chats(chatid)
	);
	```

	```
	-- Group Members Table
	CREATE TABLE group_members (
		memberid INT AUTO_INCREMENT,
		chatid INT NOT NULL,
		userid INT NOT NULL,
		lastRetrievalDateTime DATETIME(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
		PRIMARY KEY(memberid),
		FOREIGN KEY (chatid) REFERENCES chats(chatid),
		FOREIGN KEY (userid) REFERENCES users(userid)
	);
	```

	```
	-- Messages Table
	CREATE TABLE messages (
		msgid INT AUTO_INCREMENT,
		chatid INT NOT NULL,
		senderid INT NOT NULL,
		message TEXT,
		dateTime DATETIME(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
		isMsgSeen BOOLEAN DEFAULT false NOT NULL,
		PRIMARY KEY(msgid),
		FOREIGN KEY (chatid) REFERENCES chats(chatid),
		FOREIGN KEY (senderid) REFERENCES users(userid)
	);
	```

	```
	-- Attachments Table
	CREATE TABLE attachments (
		attachmentid INT AUTO_INCREMENT,
		msgid INT NOT NULL,
		filename VARCHAR(100) NOT NULL,
		temp_filename VARCHAR(100) NOT NULL,
		isFileUploaded BOOLEAN DEFAULT false NOT NULL,
		PRIMARY KEY(attachmentid),
		FOREIGN KEY (msgid) REFERENCES messages(msgid)
	);
	```

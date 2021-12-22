<?php
	if ($_SERVER['REQUEST_METHOD'] === 'POST') {
		include 'connection.php';
		
		// Insering caption associated with the attachment.
		if($_POST['whatToDo'] === "insertCaption") {
			$chatid = $_POST['chatid'];
			$senderid = $_POST['senderid'];
			$receiverid = $_POST['receiverid'];
			$message = $_POST['message'];
			$filename = $_POST['filename'];
			
			// Creating new chat, if not present in table.
			if($chatid === "null") {
				$sql1 = "INSERT INTO chats (user1id, user2id) VALUES (:senderid, :receiverid)";
				$stmt1 = $conn->prepare($sql1);
				$stmt1->execute(array(":senderid" => $senderid, ":receiverid" => $receiverid));
				$chatid = $conn->lastInsertId();
			}

			// Insering new message.
			$sql2 = "INSERT INTO messages (chatid, senderid, message) VALUES (:chatid, :senderid, :message)";
			$stmt2 = $conn->prepare($sql2);
			$stmt2->execute(array(
				":chatid" => $chatid, ":senderid" => $senderid, ":message" => $message
			));
			$msgid = $conn->lastInsertId();
			
			// Inserting name & temporary name of the file to be attached.
			$extension = pathinfo($filename, PATHINFO_EXTENSION);
			$temp_filename = $msgid . rand(111111, 999999) . "." . $extension;
			$sql3 = "INSERT INTO attachments (msgid, filename, temp_filename) VALUES (:msgid, :filename, :temp_filename)";
			$stmt3 = $conn->prepare($sql3);
			$stmt3->execute(array(
				":msgid" => $msgid, ":filename" => $filename, ":temp_filename" => $temp_filename
			));
		
			header('Content-Type: application/json');
			echo json_encode(array("chatid" => $chatid, "msgid" => $msgid, "temp_filename" => $temp_filename));
		}


		// Saving attached file.
		if($_POST['whatToDo'] === "saveAttachedFile") {
			$msgid = $_POST['msgid'];
			$temp_filename = $_POST['temp_filename'];
			$encoded_file_str = $_POST['encoded_file_str'];
			
			$filepath = ATTACHMENTS . "/" . $temp_filename;
			file_put_contents($filepath, base64_decode($encoded_file_str));

			$sql1= "SELECT msgid FROM attachments WHERE msgid=:msgid";
			$stmt1 = $conn->prepare($sql1);
			$stmt1->execute(array("msgid" => $msgid));
			$row1 = $stmt1->fetch(PDO::FETCH_ASSOC);
			
			if($stmt1->rowCount() !== 0) {
				$sql2= "UPDATE attachments SET isFileUploaded=true WHERE msgid=:msgid";
				$stmt2 = $conn->prepare($sql2);
				$stmt2->execute(array("msgid" => $msgid));
			}
			else if(file_exists($filepath))
				unlink($filepath);
			
			echo strval("1");
		}


		// Cancel uploading the file.
		if($_POST['whatToDo'] === "cancelUpload") {
			$msgid = $_POST['msgid'];

			$sql1 = "SELECT temp_filename FROM attachments WHERE msgid=:msgid";
			$stmt1 = $conn->prepare($sql1);
			$stmt1->execute(array("msgid" => $msgid));
			$row = $stmt1->fetch(PDO::FETCH_ASSOC);
			$temp_filename = $row['temp_filename'];

			$sql2= "DELETE FROM attachments WHERE msgid=:msgid";
			$stmt2 = $conn->prepare($sql2);
			$stmt2->execute(array("msgid" => $msgid));
			
			$sql3= "DELETE FROM messages WHERE msgid=:msgid";
			$stmt3 = $conn->prepare($sql3);
			$stmt3->execute(array("msgid" => $msgid));
			
			$filepath = ATTACHMENTS . "/" . $temp_filename;
			if(file_exists($filepath))
				unlink($filepath);

			echo strval("1");
		}
	}


	// Download the file.
	if($_SERVER['REQUEST_METHOD'] === 'GET' && $_GET['whatToDo'] === "downloadFile") {
		include 'connection.php';

		$msgid = $_GET['msgid'];
		$sql = "SELECT temp_filename FROM attachments WHERE msgid=:msgid";
		$stmt = $conn->prepare($sql);
		$stmt->execute(array("msgid" => $msgid));
		$row = $stmt->fetch(PDO::FETCH_ASSOC);
		$temp_filename = $row['temp_filename'];
	
		header("Location: ".BASE_URL."/".ATTACHMENTS."/".$temp_filename);
		exit;
	}
?>

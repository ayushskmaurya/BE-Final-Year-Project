<?php
	if ($_SERVER['REQUEST_METHOD'] === 'POST') {
		include 'connection.php';
		
		$chatid = $_POST['chatid'];
		$senderid = $_POST['senderid'];
		$receiverid = $_POST['receiverid'];
		$message = $_POST['message'];

		if($chatid === "null") {
			$sql1 = "INSERT INTO chats (user1id, user2id) VALUES (:senderid, :receiverid)";
			$stmt1 = $conn->prepare($sql1);
			$stmt1->execute(array(":senderid" => $senderid, ":receiverid" => $receiverid));
			$chatid = $conn->lastInsertId();
		}

		$sql2 = "INSERT INTO messages (chatid, senderid, message) VALUES (:chatid, :senderid, :message)";
		$stmt2 = $conn->prepare($sql2);
		$stmt2->execute(array(
			":chatid" => $chatid, ":senderid" => $senderid, ":message" => $message
		));

		echo strval($chatid);
	}
?>

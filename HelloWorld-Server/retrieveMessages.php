<?php
	if ($_SERVER['REQUEST_METHOD'] === 'POST') {
		include 'connection.php';

		$is_group = $_POST['isGroup'];
		$chat_id = $_POST['chatid'];
		$user_id = $_POST['userid'];
		$sql2 = "";

		// Updating: The message is seen by the recipient.
		if ($is_group === "no") {
			$sql1 = "UPDATE messages SET isMsgSeen=true ";
			$sql1 .= "WHERE chatid=:chat_id AND senderid<>:user_id AND isMsgSeen=false";
			$stmt1 = $conn->prepare($sql1);
			$stmt1->execute(array("chat_id" => $chat_id, "user_id" => $user_id));

			$sql2 = "SELECT messages.msgid, senderid, message, dateTime, isMsgSeen, ";
			$sql2 .= "COALESCE(filename, '') AS filename, ";
			$sql2 .= "COALESCE(isFileUploaded, '') AS isFileUploaded ";
			$sql2 .= "FROM messages ";
		}

		// Updating: The last retrieval time of the messages by the member of the group.
		if ($is_group === "yes") {
			$sql1 = "UPDATE group_members SET lastRetrievalDateTime=now() ";
			$sql1 .= "WHERE chatid=:chat_id AND userid=:user_id";
			$stmt1 = $conn->prepare($sql1);
			$stmt1->execute(array("chat_id" => $chat_id, "user_id" => $user_id));

			$sql2 = "SELECT messages.msgid, senderid, users.name AS sendername, message, dateTime, isMsgSeen, ";
			$sql2 .= "COALESCE(filename, '') AS filename, ";
			$sql2 .= "COALESCE(isFileUploaded, '') AS isFileUploaded ";
			$sql2 .= "FROM messages ";
			$sql2 .= "LEFT JOIN users ";
			$sql2 .= "ON messages.senderid=users.userid ";
		}

		$sql2 .= "LEFT JOIN attachments ";
		$sql2 .= "ON messages.msgid=attachments.msgid ";
		$sql2 .= "WHERE chatid=:chat_id ";
		$sql2 .= "ORDER BY dateTime";

		$stmt2 = $conn->prepare($sql2);
		$stmt2->execute(array("chat_id" => $chat_id));

		$res = array();
		while($row2 = $stmt2->fetch(PDO::FETCH_ASSOC))
			array_push($res, $row2);
		
		header('Content-Type: application/json');
		echo json_encode($res);
	}
?>

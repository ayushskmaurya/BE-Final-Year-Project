<?php
	if ($_SERVER['REQUEST_METHOD'] === 'POST') {
		include 'connection.php';

		$user_id = $_POST['userid'];

		$sql = "SELECT chats.chatid, users.name, users.profile_image, ";
		$sql .= "COALESCE(messages.message, '') AS message, ";
		$sql .= "COALESCE(messages.dateTime, '') AS dateTime, ";
		$sql .= "IF(messages.senderid<>:user_id AND messages.isMsgSeen=false, 1, 0) AS isNewMsg ";
		$sql .= "FROM chats ";
		$sql .= "INNER JOIN users ";
		$sql .= "ON (CASE WHEN chats.user1id=:user_id THEN chats.user2id WHEN chats.user2id=:user_id THEN chats.user1id END) = users.userid ";
		$sql .= "LEFT JOIN (SELECT chatid, max(dateTime) AS mDateTime FROM messages GROUP BY chatid) AS maxDateTime ";
		$sql .= "ON chats.chatid = maxDateTime.chatid ";
		$sql .= "LEFT JOIN messages ";
		$sql .= "ON maxDateTime.chatid = messages.chatid AND maxDateTime.mDateTime = messages.dateTime ";
		$sql .= "ORDER BY messages.dateTime DESC";

		$stmt = $conn->prepare($sql);
		$stmt->execute(array("user_id" => $user_id));

		$res = array();
		while($row = $stmt->fetch(PDO::FETCH_ASSOC))
			array_push($res, $row);
		
		header('Content-Type: application/json');
		echo json_encode($res);
	}
?>
